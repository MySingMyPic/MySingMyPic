package com.ylsg365.pai.util;

import android.util.Log;

import com.ylsg365.pai.BuildConfig;

public class LogUtil {
	private static final String TAG = "LogUtil";
	
	public static boolean debug = BuildConfig.DEBUG;
	
	public static void logi(String tag, String msg){
		if(!debug){
			return;
		}
		Log.i(tag, "" + msg);
	}
	
	public static void logd(String tag, String msg){
		if(!debug){
			return;
		}
		Log.d(tag, "" + msg);
	}
	
	public static void loge(String tag, String msg){
		if(!debug){
			return;
		}
		Log.e(tag, "" + msg);
	}
	
	public static void logw(String tag, String msg){
		if(!debug){
			return;
		}
		Log.w(tag, "" + msg);
	}
	
}
