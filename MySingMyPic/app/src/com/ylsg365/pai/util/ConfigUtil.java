package com.ylsg365.pai.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ylsg365.pai.app.YinApplication;

/**
 * 配置文件操作
 * SharedPreferences 作为dao层
 * @author chenck
 *
 */
public class ConfigUtil {
	public static String getStringValue(String key, String defValue){
		return ConfigUtil.getSharedPreference().getString(key, defValue);
	}
	
	public static String getStringValue(String key){
		return ConfigUtil.getStringValue(key, null);
	}
	
	public static int getIntValue(String key, int defValue){
		return ConfigUtil.getSharedPreference().getInt(key, defValue);
	}
	
	public static int getIntValue(String key){
		return ConfigUtil.getIntValue(key, 0);
	}
	
	public static boolean getBooleanValue(String key, boolean defValue){
		return ConfigUtil.getSharedPreference().getBoolean(key, defValue);
	}
	
	public static boolean getBooleanValue(String key){
		return ConfigUtil.getBooleanValue(key, false);
	}
	
	public static long getLongValue(String key){
		return ConfigUtil.getSharedPreference().getLong(key, 0);
	}
	
	public static void saveValue(String key, Object value){
		Editor editor = YinApplication.getInstance().getSharedPreference().edit();
		if(value instanceof Integer){
			editor.putInt(key, (Integer)value);
		}else if(value instanceof Long){
			editor.putLong(key, (Long)value);
		}else if(value instanceof Boolean){
			editor.putBoolean(key, (Boolean)value);
		}else if(value instanceof Float){
			editor.putFloat(key, (Float)value);
		}else{
			editor.putString(key, (String)value);
		}
		editor.commit();
	}
	
	public static void remove(String... keys){
		Editor editor = YinApplication.getInstance().getSharedPreference().edit();
		for (String key : keys) {
			editor.remove(key);
		}
		editor.commit();
	}
	
	public static SharedPreferences getSharedPreference() {
        return YinApplication.getInstance().getSharedPreference();

    }

    public static String CONFIG_TOKEN = "config_token";
    public static String CONFIG_USER = "config_user";
}
