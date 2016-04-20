/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.volley.toolbox;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.base.volley.network.HttpRequestManager;
import com.base.volley.utils.Util;
import com.base.volley.utils.logger.Logger;
import com.base.volley.AuthFailureError;
import com.base.volley.NetworkResponse;
import com.base.volley.Request;
import com.base.volley.Response;
import com.base.volley.Response.ErrorListener;
import com.base.volley.Response.Listener;
import com.base.volley.VolleyError;

/**
 * A canned request for retrieving the response body at a given URL as a Object.
 */
public class JsonRequestWithObject<T> extends JsonRequest<T> {

	private final Listener<T> mListener;

	private Map<String, String> mHeaders;

	private String mParams;

	private Class<T> mClazz;

	public JsonRequestWithObject(int method, String url, String requestBody, Class<T> clazz,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, requestBody, null,errorListener);
//		Logger.e("wangjian","method : " + method);
		mListener = listener;
		mClazz = clazz;
	}

	public JsonRequestWithObject(String url, String requestBody, Class<T> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		this(Method.GET, url, requestBody, clazz, listener, errorListener);
	}

	@Override
	protected void deliverResponse(Response<T> response) {
		if (mListener != null) {
			mListener.onResponse(response);
		}
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		T dataObject = null;
		String jsonString = null;
		boolean success = false;
		try {
			jsonString = new String(response.data, response.charset);
			if (mClazz != null) {
//				Logger.d("wangjian","jsonSting: " + jsonString);
//				Logger.d("wangjian","mClazz : " + mClazz);
				dataObject = JSON.parseObject(jsonString, mClazz);
			}
			if ((jsonString != null) && (mClazz == null || dataObject != null)) {
				success = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (success) {
			return Response.success(dataObject, jsonString,
					HttpHeaderParser.parseCacheHeaders(response));
		}
		return Response.error(new VolleyError(Util.buildHttpErrorMsg("failed",
				-1, HttpRequestManager.DATA_PARSE_ERROR)));
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders;
	}

	public void setHeaders(Map<String, String> headers) {
		mHeaders = headers;
	}

	@Override
	protected String getStringParams() {
		return mParams;
	}

	public void setStringParams(String params) {
		mParams = params;
	}

}
