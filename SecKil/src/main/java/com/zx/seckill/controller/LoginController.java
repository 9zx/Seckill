package com.zx.seckill.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.zx.seckill.redis.GoodsKey;
import com.zx.seckill.redis.LoginKey;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.SeckillUserServiceImpl;
import com.zx.seckill.util.ValidatorUtil;
import com.zx.seckill.vo.GoodsVO;
import com.zx.seckill.vo.LoginVO;

//4之前 400~600qbs
//5之前 1500+qbs

@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	RedisService redisService;
	@Autowired
	SeckillUserServiceImpl seckillUserService;
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	@Autowired
	ApplicationContext applicationContext;
	
	//登录页面缓冲在redis中且过期时间很长，因为基本上不发生改变
	@RequestMapping(value="/toLogin",produces="text/html")
	@ResponseBody
	public String toLogin(HttpServletRequest request,HttpServletResponse response,Model model){
		//到redis缓冲中取页面
		String loginHtml = redisService.get(LoginKey.getLoginHtml, "", String.class);
		if(loginHtml != null){
			return loginHtml;
		}
		//没取到就手动渲染
		SpringWebContext ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		loginHtml = thymeleafViewResolver.getTemplateEngine().process("login", ctx);
		if(loginHtml != null){
//			System.out.println("html不是空");
			redisService.set(LoginKey.getLoginHtml, "",loginHtml);
		}
		return loginHtml;
	}
	
	@RequestMapping("/doLogin")	
	@ResponseBody
	public Result<Boolean> doLogin(@Valid LoginVO loginvo,HttpServletResponse response){
		log.info(loginvo.toString());
		//参数效验,之后变高级了，用jrs参数效验+自定义注解进行效验，再用全局异常拦截拦截异常给用户友好显示
//		String passInput = loginvo.getPassword();
//		String mobile = loginvo.getMobile(); 
//		if (passInput == null) {
//			return Result.error(CodeMsg.PASSWORD_EMPTY);
//		}
//		if(mobile == null){
//			return Result.error(CodeMsg.MOBILE_EMPTY);
//		}
//		if(!ValidatorUtil.isMobile(mobile)){
//			return Result.error(CodeMsg.MOBILE_ERROR);
//		}
		//登录过程中参数校验、信息匹配如果有异常，全局异常拦截器就会处理，返回给页面结果，
		//代码更干净，所有的异常情况都交由全局异常处理器处理了
		seckillUserService.login(loginvo,response);
		return Result.success(true);
	}
}
