package com.zx.seckill.activemq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.result.Result;
import com.zx.seckill.service.GoodsServiceImpl;
import com.zx.seckill.service.OrderServiceImpl;
import com.zx.seckill.service.SeckillServiceImpl;
import com.zx.seckill.service.SeckillUserServiceImpl;
import com.zx.seckill.vo.GoodsVO;

@Service
public class Consumer {

	private static Logger log = LoggerFactory.getLogger(Consumer.class);

	@Autowired
	SeckillUserServiceImpl seckillUserServiceImpl;
	@Autowired
	GoodsServiceImpl goodsServiceImpl;
	@Autowired
	OrderServiceImpl orderServiceImpl;
	@Autowired
	SeckillServiceImpl  seckillServiceImpl;
	@Autowired
	RedisService redisService;
	
	@JmsListener(destination=ActiveMQConfig.SECKILL_QUEUE)  
	public void receive(String message){
		log.info("收到message："+message);
		SeckillMessage seckillMessage = redisService.stringToBean(message, SeckillMessage.class);
		SeckillUser user = seckillMessage.getUser();
		long goodsId = seckillMessage.getGoodsId();
		GoodsVO goods = goodsServiceImpl.getGoodsVOByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			return;
		}
		//根据订单判断是否已秒杀过，防止重复秒杀
		if(orderServiceImpl.getSeckillOrederByUserIdGoodsId(user.getId(), goodsId) != null){
			return;
		}
		//核心业务:事务执行,减库存，并写入秒杀订单，
		OrderInfo orderInfo = seckillServiceImpl.executeSeckill(user,goods);
	}
}
