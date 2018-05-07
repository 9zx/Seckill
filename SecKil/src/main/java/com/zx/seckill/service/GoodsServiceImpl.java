package com.zx.seckill.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zx.seckill.dao.GoodsDao;
import com.zx.seckill.pojo.SeckillGoods;
import com.zx.seckill.vo.GoodsVO;

@Service
public class GoodsServiceImpl {

	@Autowired
	GoodsDao dao;
	
	public List<GoodsVO> getGoodsVOList(){
		return dao.getGoodsVOList();
	}

	public GoodsVO getGoodsVOByGoodsId(long goodsId) {
		// TODO Auto-generated method stub
		return dao.getGoodsVOByGoodsId(goodsId);
	}

	public boolean reduceStockCount(GoodsVO goods) {
		SeckillGoods seckillGoods = new SeckillGoods();
		seckillGoods.setGoodsId(goods.getId());
		int res = dao.reduceStockCount(seckillGoods);
		return res>0;
	}
}
