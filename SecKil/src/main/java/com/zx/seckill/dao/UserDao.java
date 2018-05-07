package com.zx.seckill.dao;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import com.zx.seckill.pojo.User;

@Mapper
public interface UserDao {

	@Select("select * from user where id = #{id}")
	public User getUserById(@Param("id")int id);
}
