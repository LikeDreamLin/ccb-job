package com.ccb.job.admin.core.util;

import java.util.UUID;

public class UUIDGenerator {
	
	
	/**
	 * 生成6位随机数
	 * @return
	 */
	public static  String getUUID(){
		UUID uuid =  UUID.randomUUID();
		String str = uuid.toString().replace("-", "");
		str = str.substring(0,2)+str.substring(9,11)+str.substring(14,16);
		return str;
		
	}
	
	
	/**
	 * 生成8位uuid
	 * @return
	 */
	public static  String getEightUUID(){
		UUID uuid =  UUID.randomUUID();
		String str = uuid.toString().replace("-", "");
		str = str.substring(0,2)+str.substring(9,11)+str.substring(14,16)+str.substring(20,22);
		return str;
	}
	
/*
	public static  void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(getEightUUID());
		}
	}*/
}
