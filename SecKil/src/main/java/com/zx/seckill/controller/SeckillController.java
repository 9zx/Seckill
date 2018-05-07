package com.zx.seckill.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.seckill.activemq.Producer;
import com.zx.seckill.activemq.SeckillMessage;
import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillOrder;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.GoodsKey;
import com.zx.seckill.redis.PathKey;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.GoodsServiceImpl;
import com.zx.seckill.service.OrderServiceImpl;
import com.zx.seckill.service.SeckillServiceImpl;
import com.zx.seckill.service.SeckillUserServiceImpl;
import com.zx.seckill.util.UUIDUtil;
import com.zx.seckill.vo.GoodsVO;


@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean{
	
	@Autowired
	SeckillUserServiceImpl seckillUserServiceImpl;
	@Autowired
	GoodsServiceImpl goodsServiceImpl;
	@Autowired
	OrderServiceImpl orderServiceImpl;
	@Autowired
	SeckillServiceImpl  seckillServiceImpl;
	@Autowired
	RedisService redisService;
	@Autowired
	Producer producer;
	//存放每个goods是否被秒杀完，因为秒杀完就没必要再去redis种做判断了,更快
	private ConcurrentHashMap<Long, Boolean> localOverMap = new ConcurrentHashMap<Long, Boolean>();
	//容器初始化时回调的方法
	public void afterPropertiesSet() throws Exception {
		List<GoodsVO> goodsList = goodsServiceImpl.getGoodsVOList();
		if(goodsList == null){
			return;
		}
		for(GoodsVO goods : goodsList){
			redisService.set(GoodsKey.getGoodStockCount,""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
	
	@RequestMapping(value="/{path}/doSeckill",method=RequestMethod.POST)
	@ResponseBody
	public Result<Integer> doSeckill(Model model,
			@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
			HttpServletResponse response,
			long goodsId,
			@PathVariable("path")String path
			){
		//可以自己重写SringMVC提供的方法参数自定义，将根据cookieToken获取user这些代码
		//封装到自定义参数中，然后给该方法直接传个SeckillUser对象就行了
		//SpringMVC自身的Controller的参数也是这么来的，就是封装好了，我们也可以自己重写想要的参数。
		if(cookieToken == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		model.addAttribute("user", user);
		boolean isPath = seckillServiceImpl.checkPath(user.getId(), goodsId, path);
		if(!isPath){
			return Result.error(CodeMsg.SECKILL_ERROR);
		}
		//判断库存内存标记,减少redis访问
		boolean isOver = localOverMap.get(goodsId);
		if(isOver){
			return Result.error(CodeMsg.SECKILL_OVER);
		}
		//预减库存，返回的减完之后的值
		long stockCount = redisService.decr(GoodsKey.getGoodStockCount, ""+goodsId);
		if(stockCount < 0){
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.SECKILL_OVER);
		}
		//根据订单判断是否已秒杀过，防止重复秒杀
//		System.out.println(user.getId());
//		System.out.println(goodsId);
//		System.out.println(orderServiceImpl.getSeckillOrederByUserIdGoodsId(user.getId(), goodsId));
		SeckillOrder seckillOrder = orderServiceImpl.getSeckillOrederByUserIdGoodsId(user.getId(), goodsId);
		if(seckillOrder != null){
			return Result.error(CodeMsg.SECKILL_REPEATE);
		}
		//入队列
		SeckillMessage seckillMessage = new SeckillMessage();
		seckillMessage.setUser(user);
		seckillMessage.setGoodsId(goodsId);
		producer.sendSeckillMessage(seckillMessage);
		return Result.success(0);//排队中
		/* 没有队列优化时的实现
		GoodsVO goods = goodsServiceImpl.getGoodsVOByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			return Result.error(CodeMsg.SECKILL_OVER);
		}
		//根据订单判断是否已秒杀过，防止重复秒杀
		if(orderServiceImpl.getSeckillOrederByUserIdGoodsId(user.getId(), goodsId) != null){
			return Result.error(CodeMsg.SECKILL_REPEATE);
		}
		//核心业务:事务执行,减库存，并写入秒杀订单，
		OrderInfo orderInfo = seckillServiceImpl.executeSeckill(user,goods);
		return Result.success(orderInfo);
		*/
	}

	//返回-1：秒杀失败，返回0：排队种，返回1：成功
	@RequestMapping(value="/result",method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> result(Model model,
			@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
			HttpServletResponse response,
			long goodsId
			){
		if(cookieToken == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		model.addAttribute("user", user);
		SeckillOrder seckillOrder = orderServiceImpl.getSeckillOrederByUserIdGoodsId(user.getId(), goodsId);
		long result = seckillServiceImpl.getSeckillResult(user.getId(),goodsId);
		return Result.success(result);
	}
	
	
	@RequestMapping(value="/path",method=RequestMethod.GET)
	@ResponseBody
	public Result<String> path(Model model,
			@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
			HttpServletResponse response,
			long goodsId
			){
		if(cookieToken == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		String path = UUIDUtil.uuid();
		redisService.set(PathKey.SeckillPath,""+user.getId()+"_"+goodsId, path);
		return Result.success(path);
	}
}
