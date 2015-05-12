package com.ylsg365.pai.util;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class HttpMethodHelper {
    private static final String TAG = "HttpMethodHelper";
	public static HttpGet getHttpGet(String url) {
		HttpGet get = new HttpGet(url);
//        LogUtil.logd(TAG, String.format("getHttpGet  cookie=%s",  ReaderApplication.getInstance().getHttpCookies()));
		return get;
	}
	
	public static HttpPost getHttpPost(String url) {
		HttpPost post = new HttpPost(url);
//        LogUtil.logd(TAG, String.format("getHttpPost  cookie=%s", ReaderApplication.getInstance().getHttpCookies()));
		return post;
	}
}
