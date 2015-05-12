package com.ylsg365.pai.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ylsg365.pai.util.ImageFileNameGenerator;
import com.ylsg365.pai.util.LogUtil;

import java.io.File;


public class YinApplication extends Application{
	private static final String TAG = "YinApplication";
	private static YinApplication instance;
    protected SharedPreferences sharedPreference;
	private RequestQueue mRequestQueue;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
        //初始化图片下载工具
        initImageLoader(this);

        sharedPreference = getSharedPreferences(Constants.CONFIG_FILE_NAME, MODE_PRIVATE);
	}

    private void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, "imageloader/Cache");

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
                        // 如Bitmap.Config.ARGB_8888
                        // .showImageOnLoading(R.drawable.ic_launcher) //默认图片
//                .showImageForEmptyUri(R.drawable.default_cover) // url爲空會显示该图片，自己放在drawable里面的
//                .showImageOnFail(R.drawable.default_cover)// 加载失败显示的图片
                        // .displayer(new RoundedBitmapDisplayer(5)) // 圆角，不需要请删除
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3) // default
                .memoryCache(new WeakMemoryCache()).memoryCacheSize(2 * 1024 * 1024) // 缓存到内存的最大数据
                .discCacheSize(50 * 1024 * 1024) // 缓存到文件的最大数据
                .diskCache(new UnlimitedDiscCache(cacheDir)) //文件缓存目录
                .discCacheFileNameGenerator(new ImageFileNameGenerator()).discCacheFileCount(1000) // 文件数量
                .defaultDisplayImageOptions(options). // 上面的options对象，一些属性配置
                        build();
        ImageLoader.getInstance().init(config); // 初始化
    }

    public SharedPreferences getSharedPreference(){
        return sharedPreference;
    }


	@Override
	public void onLowMemory() {
		super.onLowMemory();
        LogUtil.logd(TAG, "onLowMemory");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
        LogUtil.logd(TAG, "onTerminate");
	}

	/**
	 * @return 返回context，能够动态获取资源（在任意位置获取应用程序Context）
	 */
	public static YinApplication getInstance() {
		return instance;
	}


	/**
	 * 网络是否可用
	 * @return
	 */
	/*public boolean isNetworkAvailable(){
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isAvailable();
	}*/

	/**
	 * 网络是否连接通
	 * @return
	 */
	public boolean isNetworkConnected(){
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	public boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null)
            return mMobileNetworkInfo.isConnected();
	    return false;
	}

	public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null)
            return mWiFiNetworkInfo.isConnected();
	    return false;
	}


	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
