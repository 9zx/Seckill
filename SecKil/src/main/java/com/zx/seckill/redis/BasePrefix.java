package com.zx.seckill.redis;

public abstract class BasePrefix implements KeyPrefix {

	//过期时间，我们没用到，到设为0永不过期了，实现开发会用到。
	private int expireSeconds;
	private String prefix;
	
	public BasePrefix(String prefix){
		this(0, prefix);
	}
	
	public BasePrefix(int expireSeconds, String prefix) {
		super();
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	@Override
	public int expireSeconds() {
		// 默认为0永不过期
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		//通过类名保证Key的不重复
		String className = getClass().getSimpleName();
		return className+":" + prefix;
	}

}
