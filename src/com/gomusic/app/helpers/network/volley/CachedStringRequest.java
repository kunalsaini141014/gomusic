package com.gomusic.app.helpers.network.volley;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.app.SharedPreferencesManager;
import com.gomusic.app.helpers.constants.Constants;

public class CachedStringRequest extends StringRequest {
	public CachedStringRequest(int method, String url,
			Response.Listener<String> listener,
			Response.ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	public CachedStringRequest(String url, Response.Listener<String> listener,
			Response.ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, parseIgnoreCacheHeaders(response));
	}

	/*
	 * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
	 * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
	 * 
	 * @param response The network response to parse headers from
	 * 
	 * @return a cache entry for the given response, or null if the response is
	 * not cacheable.
	 */
	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
		long now = System.currentTimeMillis();

		Map<String, String> headers = response.headers;
		long serverDate = 0;
		String serverEtag = null;
		String headerValue;

		headerValue = headers.get("Date");
		if (headerValue != null) {
			serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
		}

		serverEtag = headers.get("ETag");

		final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache
															// will be hit, but
															// also refreshed on
															// background
		final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache
														// entry expires
														// completely
		final long softExpire = now + cacheHitButRefreshed;
		final long ttl = now + cacheExpired;

		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.etag = serverEtag;
		entry.softTtl = softExpire;
		entry.ttl = ttl;
		entry.serverDate = serverDate;
		entry.responseHeaders = headers;

		return entry;
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
		headers.put(Constants.HEADER_USER_AGENT, getUserAgent());
		return headers;
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