package com.zx.seckill.redis;

public class LoginKey extends BasePrefix{

	public static final int LK_EXPIRE = 3600;
	private LoginKey(int expireSeconds,String prefix){
		super(expireSeconds,prefix);
	}
	
	public static LoginKey getLoginHtml = new LoginKey(LK_EXPIRE,"login:");
//	public static LoginKey getGoodsDetail = new LoginKey(LK_EXPIRE,"gdk:");
	
}
