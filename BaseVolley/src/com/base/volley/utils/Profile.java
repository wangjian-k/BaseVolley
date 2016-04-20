package com.base.volley.utils;

import android.content.Context;

import com.base.volley.utils.logger.Logger;
import com.base.volley.RequestQueue;
import com.base.volley.toolbox.ImageLoader;
import com.base.volley.toolbox.Volley;

public class Profile {

	public static Context mContext;
	public static final int TIMEOUT = 10000;// 网络访问超时
	public static String TAG;// log默认标签
	public static String User_Agent;// UA
	public static String COOKIE = null;// 服务器返回的 Cookie 串
	public static boolean isLogined;// 登录状态
	public static String sCachePath = null;
	public static RequestQueue sHttpRequestQueue = null;
	public static RequestQueue sImageRequestQueue = null;
	private static ImageLoader sImageLoader = null;
	private static boolean sImageViewFadeIn = true;
	public static final int FADE_IN_TIME = 150;
	public static final boolean useLruCache = true;
	private static int sCompressRatio = 100;
	private static int sSamleSize = 1;

	/**
	 * @param tag
	 *            TAG
	 * @param useragent
	 * @param context
	 */
	public static void initProfile(String tag, String useragent, Context context) {
		TAG = tag;
		User_Agent = useragent;
		mContext = context;
		if (mContext != null) {
			sCachePath = mContext.getCacheDir().getAbsolutePath();
			sHttpRequestQueue = Volley.newRequestQueue(context, "cache",
					5 * 1024 * 1024, 5);
			sImageRequestQueue = Volley.newRequestQueue(context, "images",
					50 * 1024 * 1024, 15);
		}
	}

	public static void setCompressRatio(int compressRatio) {
		if (compressRatio > 0 && compressRatio <= 100) {
			sCompressRatio = compressRatio;
			Logger.d(TAG, "sCompressRatio = " + sCompressRatio);
		}
	}

	public static int getCompressRatio() {
		return sCompressRatio;
	}

	public static void setSampleSize(int sampleSize) {
		if (sampleSize > 0 && sampleSize < 16) {
			sSamleSize = sampleSize;
			Logger.d(TAG, "sSamleSize = " + sSamleSize);
		}
	}

	public static int getSampleSize() {
		return sSamleSize;
	}

	/**
	 * 设置登陆状态
	 * 
	 * @param bLogined
	 * @param cookie
	 */
	public static void setLoginState(Boolean bLogined, String cookie) {
		isLogined = bLogined;
		COOKIE = cookie;
	}

	public static ImageLoader getImageLoader() {
		if (sImageLoader == null) {
			sImageLoader = new ImageLoader(sImageRequestQueue);
		}
		return sImageLoader;
	}

	public static void setImageViewFadeIn(boolean fadeIn) {
		sImageViewFadeIn = fadeIn;
	}

	public static boolean getImageViewFadeIn() {
		return sImageViewFadeIn;
	}

}
