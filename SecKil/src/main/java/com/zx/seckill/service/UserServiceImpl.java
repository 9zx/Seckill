package com.zx.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.zx.seckill.dao.UserDao;
import com.zx.seckill.pojo.User;

@Service
public class UserServiceImpl {

	@Autowired
	UserDao userDao;
	
	public User getUserById(int id){
		return userDao.getUserById(id);
	}
}
