package com.ylsg365.pai.app;

/**
 * Created by ylsg365 on 2015-02-08.
 */
public class Constants {
    public static final String SITE_DOMAIN = "182.92.170.38:18080"; //测试ip
    public static final String SITE_DOMAIN2 = "182.92.170.38:18082"; //文件上传
    public static final String SITE_DOMAIN_FILE = SITE_DOMAIN2;
    public static final String WEB_HOST = "" + SITE_DOMAIN; //前缀暂时为空
    public static final String WEB_PAY="http://"+SITE_DOMAIN+"/myxxPay/notify_url.jsp";
    public static final String WEB_SERVER_DOMAIN = "http://" + WEB_HOST + "/Weitie/client/";
    public static final String WEB_SERVER_DOMAIN2 = "http://" + SITE_DOMAIN2 + "/Weitie/client/";
    public static final String WEB_IMG_DOMIN = "http://" + WEB_HOST;

    public static final String CONFIG_FILE_NAME = "config";

    public static final String WX_APP_ID = "wx40c36f8a01759da5";  //微信APPID

    public static final String APP_KEY = "767463442";     // 新浪的APP_KEY
    public static final String REDIRECT_URL = "http://www.sina.com";// 应用的回调页
    public static final String SCOPE =           // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
}
