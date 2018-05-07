package com.zx.seckill.result;

public class CodeMsg {

	private int code;
	private String msg;
	//封装异常信息
	//通用异常
	public static CodeMsg SUCCESS = new CodeMsg(0,"success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务器异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101,"服务器异常,%s");
	public static CodeMsg SESSION_ERROR = new CodeMsg(500102,"服务器会话异常,%s");
	//登录模块
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"密码为空，错误！");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号为空，错误！");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号格式错误！");
	public static CodeMsg USER_NOT_EXIST = new CodeMsg(500214,"用户不存在！");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500214,"密码输入错误！");
	//秒杀模块
	public static CodeMsg SECKILL_OVER = new CodeMsg(500300,"已全部秒杀完毕！");
	public static CodeMsg SECKILL_REPEATE = new CodeMsg(500301,"重复秒杀！");
	public static CodeMsg SECKILL_ERROR = new CodeMsg(500302,"非法请求！");
	//订单模块
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在！");

	public CodeMsg fillMsg(Object...args) {
		int code = this.code;
		String msg = String.format(this.msg, args);
		return new CodeMsg(code, msg);
	}
	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
