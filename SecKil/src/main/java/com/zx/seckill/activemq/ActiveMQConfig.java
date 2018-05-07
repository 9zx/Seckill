package com.zx.seckill.activemq;

import javax.jms.Queue;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.validator.PublicClassValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class ActiveMQConfig {

	public static final String SECKILL_QUEUE = "seckill-queue";
	@Bean
	public Queue queue(){
		return new ActiveMQQueue(SECKILL_QUEUE);
	}
	
	@Bean
	public ActiveMQTopic topic(){
		return new ActiveMQTopic(SECKILL_QUEUE);
	}
	
	@Bean
	public ActiveMQConnectionFactory activeMQConnectionFactory(){
		
		 ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
				 ActiveMQConnectionFactory.DEFAULT_USER, 
				 ActiveMQConnectionFactory.DEFAULT_PASSWORD, 
				 "tcp://192.168.110.128:61616");
		 return activeMQConnectionFactory;
	}
}
