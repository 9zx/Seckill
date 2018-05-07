package com.zx.seckill.controller;

import java.util.ArrayList;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.GoodsKey;
import com.zx.seckill.redis.LoginKey;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.redis.SeckillUserKey;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.GoodsServiceImpl;
import com.zx.seckill.service.SeckillUserServiceImpl;
import com.zx.seckill.vo.GoodsDetailVO;
import com.zx.seckill.vo.GoodsVO;

@Controller
@RequestMapping("/goods")
public class GoodController {

	@Autowired
	SeckillUserServiceImpl seckillUserServiceImpl;
	
	@Autowired
	GoodsServiceImpl goodsServiceImpl;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	//goodsList页面缓冲在redis中，过期时间较短，因为物品信息可能发生改变
	@RequestMapping(value="/toList",produces="text/html")
	@ResponseBody
	public String toList(Model model,
			@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
			HttpServletResponse response,
			HttpServletRequest request
			){
		//可以自己重写SringMVC提供的方法参数自定义，将根据cookieToken获取user这些代码
		//封装到自定义参数中，然后给该方法直接传个SeckillUser对象就行了
		//SpringMVC自身的Controller的参数也是这么来的，就是封装好了，我们也可以自己重写想要的参数。
		SpringWebContext ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		if(cookieToken == null){
			String loginHtml = redisService.get(LoginKey.getLoginHtml, "", String.class);
			if(loginHtml == null){
				loginHtml = thymeleafViewResolver.getTemplateEngine().process("login", ctx);
				redisService.set(LoginKey.getLoginHtml, "",loginHtml);
			}
			return loginHtml;
		}
//		System.out.println(cookieToken);
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
//		System.out.println("toList"+user);
		if(user == null){
			String loginHtml = redisService.get(LoginKey.getLoginHtml, "", String.class);
			if(loginHtml == null){
				loginHtml = thymeleafViewResolver.getTemplateEngine().process("login", ctx);
				redisService.set(LoginKey.getLoginHtml, "",loginHtml);
			}
			return loginHtml;
		}
		redisService.set(SeckillUserKey.token, cookieToken, user);
		model.addAttribute("user", user);
		//到redis缓冲中取页面
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(html != null){
			return html;
		}
		List<GoodsVO> goodsList = goodsServiceImpl.getGoodsVOList();
//		System.out.println(user);
		model.addAttribute("goodsList",goodsList);
//		return "goodsList";
		ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		//没取到就手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("GoodsList", ctx);
		if(html != null){
			System.out.println("html不是空");
			redisService.set(GoodsKey.getGoodsList, "",html);
		}
		return html;
	}
	
	//物品详情页做成静态页面，返回给页面数据就好，页面自己填充并展示，之后浏览器会缓冲该页面，再次请求时，
	//如果数据没改变服务器会响应302浏览器就直接去缓冲中找(需配合SpringBoot提供的静态页面配置)
	//因为物品详情更是基本不变，这样更节约资源。
	@RequestMapping(value="/toDetail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVO> toDetail(Model model,
			@CookieValue(value=SeckillUserServiceImpl.COOIKE_TOKEN_NAME,required=false)String cookieToken,
 			@PathVariable("goodsId")long goodsId,
 			HttpServletResponse response
			){
		//可以自己重写SringMVC提供的方法参数自定义，将根据cookieToken获取user这些代码
		//封装到自定义参数中，然后给该方法直接传个SeckillUser对象就行了
		//SpringMVC自身的Controller的参数也是这么来的，就是封装好了，我们也可以自己重写想要的参数。
//		SpringWebContext ctx = new SpringWebContext(request,response,
//    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		if(cookieToken == null){
//			String loginHtml = redisService.get(LoginKey.getLoginHtml, "", String.class);
//			if(loginHtml == null){
//				loginHtml = thymeleafViewResolver.getTemplateEngine().process("login", ctx);
//				redisService.set(LoginKey.getLoginHtml, "",loginHtml);
//			}
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		SeckillUser user = seckillUserServiceImpl.getUserByCookieToken(response,cookieToken);
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		model.addAttribute("user", user);
		System.out.println("toDetail"+user);
		//先从缓冲中取页面
//		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
//		if(html != null){
//			return html;
//		}
		//缓冲中没有页面再取数据库中查数据，手动渲染页面
		GoodsVO goods = goodsServiceImpl.getGoodsVOByGoodsId(goodsId);
//		model.addAttribute("goods",goods);
		//
		long startTime = goods.getStartDate().getTime();
		long endTime = goods.getEndDate().getTime();
		long nowTime = System.currentTimeMillis();
		int seckillStatus = 0;  //秒杀状态标志
		int remainSeconds = 0;  //秒杀开启倒计时时间
		if(nowTime < startTime){
			System.out.println("秒杀未开启");
			seckillStatus = 0; //秒杀未开启状态标志
			remainSeconds = (int) ((startTime - nowTime)/1000);
		}else if(nowTime > endTime){
			System.out.println("秒杀已技术");
			seckillStatus = -1; //秒杀结束状态标识
			remainSeconds = -1;
		}else {
			System.out.println("秒杀开启中");
			seckillStatus = 1; //秒杀开启状态标志
			remainSeconds = 0;
		}
		GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
		goodsDetailVO.setGoods(goods);
		goodsDetailVO.setMiaoshaStatus(seckillStatus);
		goodsDetailVO.setRemainSeconds(remainSeconds);
		goodsDetailVO.setUser(user);
		System.out.println(goodsDetailVO);
		return Result.success(goodsDetailVO);
		//手动渲染
//		html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", ctx);
//		if(html != null){
//			System.out.println("html不是空");
//			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId,html);
//		}

	}
}
