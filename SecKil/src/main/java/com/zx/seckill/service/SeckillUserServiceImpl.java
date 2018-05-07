package com.zx.seckill.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zx.seckill.dao.SeckillUserDao;
import com.zx.seckill.exception.GlobalException;
import com.zx.seckill.pojo.SeckillUser;
import com.zx.seckill.redis.RedisService;
import com.zx.seckill.redis.SeckillUserKey;
import com.zx.seckill.result.CodeMsg;
import com.zx.seckill.util.MD5Util;
import com.zx.seckill.util.UUIDUtil;
import com.zx.seckill.vo.LoginVO;

@Service
public class SeckillUserServiceImpl {

	public static final String COOIKE_TOKEN_NAME = "token";	
	@Autowired
	SeckillUserDao seckillUserDao;
	@Autowired
	RedisService redisService;
	
	//也用到redis缓冲,可能用处不是很大，因为登录不会很多次
	public SeckillUser getSeckillUserById(long id){
		//先从redis缓冲中取用户
		SeckillUser user = redisService.get(SeckillUserKey.getUserById, ""+id, SeckillUser.class);
		if(user != null){
			return user;
		}
		//缓冲中没有，到数据库中取
		user = seckillUserDao.getSeckillUserById(id);
		if(user != null){
			redisService.set(SeckillUserKey.getUserById,""+id, user);
		}
		return user;
	}
	
	//对象更新操作时，不同与页面缓冲，需要先更新数据库再更新缓冲
	//如果遇到更新，就是这个操作
	public boolean updatePassword(String token, long id, String formPass) {
		//取用户
		SeckillUser user = getSeckillUserById(id);
		if(user == null) {
			throw new GlobalException(CodeMsg.USER_NOT_EXIST);
		}
		//更新数据库
		SeckillUser toBeUpdate = new SeckillUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPwToDBPw(formPass, user.getSalt()));
		seckillUserDao.update(toBeUpdate);
		//更新缓冲
		redisService.delete(SeckillUserKey.getUserById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		//更新token，更新其获取到的对象，不能删掉再存，删掉session就不在了，要重新登录了。
		redisService.set(SeckillUserKey.token, token, user);
		return true;
	}

	public boolean login(LoginVO loginvo,HttpServletResponse response) {
		if(loginvo == null){
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String formPw = loginvo.getPassword();
		String mobile = loginvo.getMobile(); 
		SeckillUser seckillUser = getSeckillUserById(Long.parseLong(mobile));
		if(seckillUser == null){
			throw new GlobalException(CodeMsg.USER_NOT_EXIST);
		}
		String dbPw = seckillUser.getPassword();
		String salt = seckillUser.getSalt(); 
		//通过计算输入的密码之后得到的密码
		String calcuPw = MD5Util.formPwToDBPw(formPw, salt);
//		System.out.println(formPw);
//		System.out.println(calcuPw);
		if(!dbPw.equals(calcuPw)){
		//	System.out.println(dbPw);
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//生成cookie，cookie+redis实现分布式session共享
//		String jsessionId = request.getRequestedSessionId();
		String token = UUIDUtil.uuid();
		redisService.set(SeckillUserKey.token, token, seckillUser);
		Cookie cookie = new Cookie(COOIKE_TOKEN_NAME, token);
//		cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
		return true; 
	}

	public SeckillUser getUserByCookieToken(HttpServletResponse response,String cookieToken) {
		if (cookieToken == null) {
			return null;
		}
		SeckillUser seckillUser =  redisService.get(SeckillUserKey.token, cookieToken, SeckillUser.class);
		System.out.println("redis:"+seckillUser);
		if (seckillUser != null) {
			String token = cookieToken;
			redisService.set(SeckillUserKey.token, token, seckillUser);
			Cookie cookie = new Cookie(COOIKE_TOKEN_NAME, token);
//			cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		return seckillUser;
	}
}
