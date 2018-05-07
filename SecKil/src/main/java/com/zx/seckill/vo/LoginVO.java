package com.zx.seckill.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.zx.seckill.validator.IsMobile;

public class LoginVO {

	//JSR参数效验注解+自定义注解完成参数效验，高级
	@NotNull
	@IsMobile
	private String mobile;
	
	@NotNull
	@Length(min=32)
	private String password;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LoginVO [mobile=" + mobile + ", password=" + password + "]";
	}
	
	
}
