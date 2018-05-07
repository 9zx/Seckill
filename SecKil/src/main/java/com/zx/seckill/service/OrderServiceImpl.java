package com.zx.seckill.service;

import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zx.seckill.dao.OrderDao;
import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillOrder;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.OrderKey;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.vo.GoodsVO;

@Service
public class OrderServiceImpl {

	@Autowired
	OrderDao dao;
	@Autowired
	RedisService redisService;
	
	public SeckillOrder getSeckillOrederByUserIdGoodsId(long userId,long goodsId) {
//		return dao.getSeckillOrederByUserIdGoodsId(userId,goodsId);
		
		SeckillOrder seckillOrder = redisService.get(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goodsId, SeckillOrder.class);
		return seckillOrder;
	}

	@Transactional
	public OrderInfo createOrder(SeckillUser user, GoodsVO goods) {
		
		OrderInfo orderInfo =  new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(null);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getSeckillPrice());
		orderInfo.setUserId(user.getId());
		orderInfo.setStatus(0);
		dao.insertOrder(orderInfo);
		SeckillOrder seckillOrder = new SeckillOrder();
		seckillOrder.setGoodsId(goods.getId());
		seckillOrder.setOrderId(orderInfo.getId());
		seckillOrder.setUserId(user.getId());
		dao.insertSeckillDao(seckillOrder);
		redisService.set(OrderKey.getSeckillOrderByUidGid, ""+user.getId()+"_"+goods.getId(), seckillOrder);
		return orderInfo;
		
	}

	public OrderInfo getOrderById(long orderId) {
		return dao.getOrderById(orderId);
	}


}
