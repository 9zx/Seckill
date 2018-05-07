package com.zx.seckill.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.GoodsServiceImpl;
import com.zx.seckill.service.OrderServiceImpl;
import com.zx.seckill.service.SeckillUserServiceImpl;
import com.zx.seckill.vo.GoodsVO;
import com.zx.seckill.vo.OrderDetailVO;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	SeckillUserServiceImpl seckillUserServiceImpl;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	@Autowired
	GoodsServiceImpl goodsServiceImpl;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVO> info(Model model,
    		@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
			HttpServletResponse response,
    		@RequestParam("orderId") long orderId) {
    	if(cookieToken == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		System.out.println("orderId:"+orderId);
    	OrderInfo order = orderServiceImpl.getOrderById(orderId);
    	if(order == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long goodsId = order.getGoodsId();
    	GoodsVO goods = goodsServiceImpl.getGoodsVOByGoodsId(goodsId);
    	OrderDetailVO vo = new OrderDetailVO();
    	vo.setOrder(order);
    	vo.setGoods(goods);
   
    	return Result.success(vo);
    }
    
}
