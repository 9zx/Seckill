package com.zx.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	
	//盐值
	private static final String salt="1a2b3c4d";

	public static String md5(String src){
		return DigestUtils.md5Hex(src);
	}
	
	public static String inputPwFormPw(String inputPw){
		//记住java是length()，js是length。所以用自己的去js中为了保证MD5的规则一样，但用了length导致出错，一直找不到。
		String string = ""+salt.charAt(0)+salt.charAt(2) + inputPw +salt.charAt(5) + salt.charAt(4);
		return md5(string);
	}
	
	public static String formPwToDBPw(String formPw,String salt){
		String string = ""+salt.charAt(0)+salt.charAt(2) + formPw +salt.charAt(5) + salt.charAt(4);
		return md5(string);
	}
	
	//核心调用方法
	public static String inputPwToDBPw(String intputPw,String salt){
		String formPw = inputPwFormPw(intputPw);
		String dbPw = formPwToDBPw(formPw, salt);
		return dbPw; 
	}
	
	public static void main(String[] args) {
		System.out.println(inputPwFormPw("888888"));
		System.out.println(inputPwToDBPw("888888","1a2b3c4d"));
	}
}
