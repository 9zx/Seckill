package com.zx.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.zx.seckill.dao.SeckillDao;
import com.zx.seckill.pojo.Goods;
import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillOrder;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.GoodsKey;
import com.zx.seckill.redis.PathKey;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.vo.GoodsVO;

@Service
public class SeckillServiceImpl {

	@Autowired
	OrderServiceImpl orderServiceImpl;
	//习惯上不引入其他的Dao，只引用自己的dao，而可以引用其他的service。
	@Autowired
	GoodsServiceImpl goodsServiceImpl;
	@Autowired
	RedisService redisService;
	
	@Transactional
	public OrderInfo executeSeckill(SeckillUser user, GoodsVO goods) {
		//减库存
		boolean success = goodsServiceImpl.reduceStockCount(goods);
		if(success){
			//减库存成功就写入订单
			return orderServiceImpl.createOrder(user,goods);
		}else{
			setGoodSOver(goods.getId());
			return null;
		}
	}

	private void setGoodSOver(Long goodsId) {
		redisService.set(GoodsKey.GoodsOver, ""+goodsId, true);
	}


	private boolean getGoodsOver(long goodsId) {
		System.out.println(goodsId);
		return redisService.get(GoodsKey.GoodsOver, ""+goodsId, boolean.class);
	}

	public long getSeckillResult(Long userId, long goodsId) {
		SeckillOrder order = orderServiceImpl.getSeckillOrederByUserIdGoodsId(userId, goodsId);
		System.out.println("order:"+order);
		if(order != null){
			System.out.println(order.getOrderId());
			return order.getOrderId();
		}else{
			if(getGoodsOver(goodsId)){
				return -1;
			}else{
				return 0;
			}
		}
	}
	
	public boolean checkPath(Long userId,Long goodsId,String path){
		if(path.equals(redisService.get(PathKey.SeckillPath,""+userId+"_"+goodsId, String.class))){
			return true;
		}else{
			return false;
		}
	}

}
