package com.zx.seckill.redis;

public class SeckillUserKey extends BasePrefix {
	
	public static final int TOKEN_EXPIRE = 3600*2;
	private SeckillUserKey(int expireSeconds,String prefix){
		super(expireSeconds,prefix);
	}
	
	public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE,"tk:");
	public static SeckillUserKey getUserById = new SeckillUserKey(0,"tk:");
	
}
