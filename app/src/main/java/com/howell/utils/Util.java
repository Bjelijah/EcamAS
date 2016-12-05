package com.howell.utils;


import android.content.Context;
import android.net.TrafficStats;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yangyu
 *	��������������������
 */
public class Util {
	/**
	 * �õ��豸��Ļ�Ŀ��
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * �õ��豸��Ļ�ĸ߶�
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * �õ��豸���ܶ�
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * ���ܶ�ת��Ϊ����
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}
	
	
	private static long lastTotalRxBytes = 0;
	private static long lastTimeStamp = 0;
	
	private static long getTotalRxBytes(Context context){
		return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED?0:(TrafficStats.getTotalRxBytes()/1024);
	}
	
	public static String getDownloadSpeed(Context context){
		long nowTotalRxBytes = getTotalRxBytes(context);
		long nowTimeStemp = System.currentTimeMillis();
		long speed = (nowTotalRxBytes - lastTotalRxBytes)*1000 / (nowTimeStemp - lastTimeStamp);
		lastTimeStamp = nowTimeStemp;
		lastTotalRxBytes = nowTotalRxBytes;
//		if(speed == 0 ){
//			if(!isNetConnect(context)){
//				return null;
//			}
//		}
		
		return String.valueOf(speed) + "kb/s";
	}
	

	public static boolean isIP(String addr){
		if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
		{
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(addr);
		boolean ipAddress = mat.find();
		return ipAddress;
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}




}
