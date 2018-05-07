package com.zx.seckill.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//加载配置文件中"redis"打头的配置属性
@ConfigurationProperties(prefix="spring.redis")
public class RedisConfig {
	
	private String host;
    private int port;
    private int timeout; //等待几秒超时
	public String getHost() {
		System.out.println("host:"+host);
		return host;
	}
	public void setHost(String host) {
		this.host = host;

	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
    
}
