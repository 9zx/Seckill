package com.zx.seckill.controller;

import org.apache.commons.pool2.UsageTracking;

import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.seckill.redis.RedisService;
import com.zx.seckill.redis.UserKey;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.UserServiceImpl;
import com.zx.seckill.pojo.User;

@Controller
public class DemoController {

	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	RedisService redisService;
	
	@RequestMapping("/test")
	@ResponseBody
	public String home(){
		return "hello word";
	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello(){
		return Result.success("成功了");
	}
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(){
		return "hello";
	}
	
	@RequestMapping("/db/getUserById")
	@ResponseBody
	public Result<User> getUserById(){
		User user = userServiceImpl.getUserById(1);
		System.out.println(user);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet(){
		User user = redisService.get(UserKey.getById,""+1, User.class);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet(){
		User user = new User();
		user.setId(1);
		user.setName("张鑫");
		boolean b = redisService.set(UserKey.getById,""+1, user);
		return Result.success(b);
	}
	
}
