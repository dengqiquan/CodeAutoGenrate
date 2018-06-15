package com.fcore.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/** 
	* @author zhangkui
	* @Title: getCurrentTime 
	* @Description: 获取当前时间
	* @return String
	*/
	public static String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	public static void main(String[] args) {
		System.out.println(getCurrentTime());
	}
}
