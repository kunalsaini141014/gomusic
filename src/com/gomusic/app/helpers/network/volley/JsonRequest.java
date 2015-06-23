package com.gomusic.app.helpers.network.volley;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gomusic.app.helpers.app.SharedPreferencesManager;
import com.gomusic.app.helpers.constants.Constants;

public class JsonRequest extends JsonObjectRequest {
	private String acceptType;
	private int method;
	
	/**
	 * @param method
	 * @param url
	 * @param params
	 *            A {@link HashMap} to post with the request. Null is allowed
	 *            and indicates no parameters will be posted along with request.
	 * @param listener
	 * @param errorListener
	 */
	public JsonRequest(int method, String url, JSONObject jsonObject,Listener<JSONObject> listener,
			ErrorListener errorListener,String acceptType) {
		super(method, url, jsonObject, listener, errorListener);
		this.acceptType = acceptType;
		this.method = method;
	}
	
	/**
	 * @param method
	 * @param url
	 * @param params
	 *            A {@link HashMap} to post with the request. Null is allowed
	 *            and indicates no parameters will be posted along with request.
	 * @param listener
	 * @param errorListener
	 */
	public JsonRequest(int method, String url, JSONObject jsonObject,Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(method, url, jsonObject, listener, errorListener);
		this.acceptType = Constants.CONTENT_TYPE_JSON;
		this.method = method;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		headers.put("Accept", this.acceptType);
		if(this.method == Request.Method.GET){
			headers.put("Content-Type", Constants.CONTENT_TYPE_JSON);
			headers.put("charset", Constants.CHARSET);
		}
		headers.put(Constants.HEADER_USER_AGENT, getUserAgent());
		return headers;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
	    try {
	            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	            if(this.method == Request.Method.GET){
	            	 return Response.success(new JSONObject(jsonString),enforceClientCaching(HttpHeaderParser.parseCacheHeaders(response), response));
	            }else{
	            	return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));
	            }
	           
	        }catch (UnsupportedEncodingException e) {
	            return Response.error(new ParseError(e));
	        }catch (JSONException je) {
	            return Response.error(new ParseError(je));
	        }
	}
	
	//protected static final int defaultClientCacheExpiry = 1000 * 60 * 60; // milliseconds; = 1 hour
	protected static final int defaultClientCacheExpiry = 1000 * 60 * 5; // milliseconds; = 1 min

	protected Cache.Entry enforceClientCaching(Cache.Entry entry, NetworkResponse response) {
	    if (getClientCacheExpiry() == null) return entry;

	    long now = System.currentTimeMillis();

	    if (entry == null) {
	        entry = new Cache.Entry();
	        entry.data = response.data;
	        entry.etag = response.headers.get("ETag");
	        entry.softTtl = now + getClientCacheExpiry();
	        entry.ttl = entry.softTtl;
	        entry.serverDate = now;
	        entry.responseHeaders = response.headers;
	    } else if (entry.isExpired()) {
	        entry.softTtl = now + getClientCacheExpiry();
	        entry.ttl = entry.softTtl;
	    }

	    return entry;
	}

	protected Integer getClientCacheExpiry() {
	    return defaultClientCacheExpiry;
	}
	
	public static String getUserAgent() {
		String userAgent = SharedPreferencesManager.getStringPreference(Constants.USER_AGENT_STRING, "");
		if(TextUtils.isEmpty(userAgent)){
			userAgent = String.format( Constants.USER_AGENT_STRING, Build.VERSION.RELEASE,Build.MODEL + " Build/"+Build.ID);
			SharedPreferencesManager.setPreference(Constants.USER_AGENT_STRING, userAgent);
		}
		return userAgent;
	}
}
