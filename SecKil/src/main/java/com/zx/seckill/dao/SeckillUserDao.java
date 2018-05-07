package com.zx.seckill.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zx.seckill.pojo.SeckillUser;

@Mapper
public interface SeckillUserDao {

	@Select("select * from seckill_user where id = #{id}")
	public SeckillUser getSeckillUserById(long id);

	@Update("update seckill_user set password = #{password} where id = #{id}")
	public void update(SeckillUser seckillUser);
}
