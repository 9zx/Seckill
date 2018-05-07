package com.zx.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;

@Service
public class JedisPoolFactory {

	@Autowired
	RedisConfig redisConfig;
	
	@Bean
	public JedisPool jedisFactory(){
//		JedisPoolConfig poolConfig = new JedisPoolConfig();
		JedisPool jedisPool = new JedisPool(redisConfig.getHost(),redisConfig.getPort());
		return jedisPool;
	}
	
}
