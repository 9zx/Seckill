package com.zx.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

	@Autowired
	JedisPool jedisPool;
	@Autowired
	RedisConfig redisConfig;
	/**
	 * 获取对象
	 * @return
	 */
	public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			if(this.exist(prefix, key)){
				String realKey = prefix.getPrefix() + key;
				String content = jedis.get(realKey);
				T t = stringToBean(content,clazz);
				return t ;
			}else{
				return null;
			}
		}finally {
			if(jedis != null)
				jedis.close();
		}
	}
	
	/**
	 * 设置存储对象
	 * @return
	 */
	public <T> boolean set(KeyPrefix prefix,String key, T value){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			String content = beanToString(value);
			if(content == null || content.length() <= 0) {
				 return false;
			}
			String realKey = prefix.getPrefix() + key;
			//判断过期时间
			int seconds = prefix.expireSeconds();
			if(seconds <= 0){
				jedis.set(realKey, content);
			}else{
				jedis.setex(realKey, seconds, content);
			}
			return true ;
		}finally { 
			if(jedis != null)
				jedis.close();
		}
	}
	/*
	 * 删除对象
	 */
	public boolean delete(KeyPrefix prefix ,String key) {
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			if(this.exist(prefix,key)){
				String realKey = prefix.getPrefix() + key;
				long result = jedis.del(realKey);
				return result>0;
			}
		}finally {
			if(jedis != null)
				jedis.close();
		}
		return false;
	}
	
	/**
	 * 判断对象是否存在
	 * @return
	 */
	public <T> boolean exist(KeyPrefix prefix,String key){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			String realKey = prefix.getPrefix() + key;
			return jedis.exists(realKey);
		}finally {
			if(jedis != null)
				jedis.close();
		}
	}
	
	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.incr(realKey);
		 }finally {
			 if(jedis != null)
					jedis.close();
		 }
	}
	
	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.decr(realKey);
		 }finally {
			 if(jedis != null)
					jedis.close();
		 }
	}
	
	public  <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}


	public   <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}



	
}
