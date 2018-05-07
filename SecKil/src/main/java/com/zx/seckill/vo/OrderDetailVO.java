package com.zx.seckill.vo;

import com.zx.seckill.pojo.OrderInfo;

public class OrderDetailVO {

	private GoodsVO goods;
	private OrderInfo order;
	public GoodsVO getGoods() {
		return goods;
	}
	public void setGoods(GoodsVO goods) {
		this.goods = goods;
	}
	public OrderInfo getOrder() {
		return order;
	}
	public void setOrder(OrderInfo order) {
		this.order = order;
	}

}
