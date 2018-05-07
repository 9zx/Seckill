package com.zx.seckill.activemq;


import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.zx.seckill.redis.RedisService;

@Service
public class Producer  {
	
	@Autowired
	private JmsMessagingTemplate jmsTemplate;
	@Autowired
	RedisService redisService;
	@Autowired
	private Queue queue;
	
	private static Logger log = LoggerFactory.getLogger(Consumer.class);
	
	public void send(Object message) {
		String msg = redisService.beanToString(message);
		log.info("发出message:"+message);
		jmsTemplate.convertAndSend(this.queue, msg);
	}

	public void sendSeckillMessage(SeckillMessage seckillMessage) {
		String msg = redisService.beanToString(seckillMessage);
		jmsTemplate.convertAndSend(this.queue,msg);
	}


//	public void run(String... arg0) throws Exception {
//		
//		send("This is a message.");  
//	}
}
