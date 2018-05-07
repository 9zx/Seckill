package com.zx.seckill.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import com.zx.seckill.pojo.OrderInfo;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.vo.GoodsVO;

@Mapper
public interface SeckillDao {

	@Transactional
	@Insert("")
	public OrderInfo executeSeckill(SeckillUser user, GoodsVO goods);

	
}
