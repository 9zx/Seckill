package com.zx.seckill.redis;

public class PathKey extends BasePrefix{

	public static final int PK_EXPIRE = 60;
	private PathKey(int expireSeconds,String prefix){
		super(expireSeconds,prefix);
	}

	public static PathKey SeckillPath = new PathKey(60,"sp:");
}
