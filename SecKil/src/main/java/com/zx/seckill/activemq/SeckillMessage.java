package com.zx.seckill.activemq;

import java.util.Date;

import com.zx.seckill.pojo.SeckillUser;

public class SeckillMessage {
	
	private static SeckillUser user;
	private long goodsId;

	public SeckillUser getUser() {
		return user;
	}
	public void setUser(SeckillUser user) {
		this.user = user;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	
	
}
