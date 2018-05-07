package com.zx.seckill.redis;

public class GoodsKey extends BasePrefix {
	
	public static final int GK_EXPIRE = 60;
	private GoodsKey(int expireSeconds,String prefix){
		super(expireSeconds,prefix);
	}
	
	public static GoodsKey getGoodsList = new GoodsKey(GK_EXPIRE,"glk:");
	public static GoodsKey getGoodsDetail = new GoodsKey(GK_EXPIRE,"gdk:");
	public static GoodsKey getGoodStockCount = new GoodsKey(0,"gsck:");
	public static GoodsKey GoodsOver = new GoodsKey(0,"go:");
}
