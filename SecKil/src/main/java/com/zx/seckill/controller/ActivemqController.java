package com.zx.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.seckill.activemq.Consumer;
import com.zx.seckill.activemq.Producer;

@Controller
@RequestMapping("/active")
public class ActivemqController {

	@Autowired
	Producer producer;
	@Autowired
	Consumer consumer;
	
	@RequestMapping("/test")
	@ResponseBody
	public void activetest(){
		producer.send("测试成功！");
	}
}
