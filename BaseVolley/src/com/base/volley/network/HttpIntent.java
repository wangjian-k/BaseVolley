package com.base.volley.network;

import java.util.HashMap;

import android.content.Intent;

import com.base.volley.Request;

public class HttpIntent extends Intent {

	public static final String URI = "uri";

	public static final String METHOD = "method";

	public static final String IS_SET_COOKIE = "is_set_cookie";

	public static final String READ_TIMEOUT = "read_timeout";

	public static final String CONNECT_TIMEOUT = "connect_timeout";

	public static final String POST_PARAM = "post_param";

	public static final String IS_UPDATE_COOKIE = "is_update_cookie";

	/**
	 * 是否缓存接口返回数据到本地
	 */
	public static final String IS_CACHE_DATA = "is_cache_data";

	/**
	 * 连接超时，读取超时
	 */
	public int connectTimeout, readTimeout;

	private Object parseObject;
	
	private HashMap<String,String> header = new HashMap<String,String>();
	
	public static final String FILE_NAME = "file_name";

	public HttpIntent(String uri) {
		this(uri, HttpRequestManager.METHOD_GET, false);
	}

	public HttpIntent(String uri, String reqMethod) {
		this(uri, reqMethod, false);
	}

	public HttpIntent(String uri, boolean isSetCookie) {
		this(uri, HttpRequestManager.METHOD_GET, isSetCookie);
	}
	
	public HttpIntent(String uri, String reqMethod, boolean isSetCookie) {
		this(uri, reqMethod, isSetCookie, false);
	}

	public HttpIntent(String uri, String reqMethod, boolean isSetCookie,
			boolean isUpdateCookie) {
		putExtra(URI, uri);
		putExtra(METHOD, reqMethod);
		putExtra(IS_SET_COOKIE, isSetCookie);
		putExtra(IS_CACHE_DATA, true);
		putExtra(CONNECT_TIMEOUT, 5000);
		putExtra(READ_TIMEOUT, 7000);
		putExtra(IS_UPDATE_COOKIE, isUpdateCookie);
	}


	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		putExtra(CONNECT_TIMEOUT, connectTimeout);
	}

	/**
	 * @param readTimeout
	 *            the readTimeout to set
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		putExtra(READ_TIMEOUT, readTimeout);
	}

	public void putParseObject(Object o) {
		parseObject = o;
	}

	public Object getParseObject() {
		return parseObject;
	}

	public HashMap<String,String> getHeader() {
		return header;
	}

	public void setHeader(String key,String value) {
		this.header.put(key, value);
	}

}
