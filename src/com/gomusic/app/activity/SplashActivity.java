package com.gomusic.app.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gomusic.app.R;
import com.gomusic.app.helpers.alert.AlertUtil;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.app.BeanHelper;
import com.gomusic.app.helpers.app.SharedPreferencesManager;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.network.volley.CachedStringRequest;
import com.gomusic.app.helpers.network.volley.VolleyErrorListener;
import com.gomusic.app.model.MusicCategory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {
	private AlertUtil alertUtil;
	private static final int[] buttons = new int[]{R.string.text_dialog_button_retry,R.string.text_dialog_button_exit};
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		handler = new Handler();
		fetchCategories();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {

		}
	};

	private void fetchCategories() {
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(Constants.CATEGORY_URL);
		if (entry != null) {
			try {
				String data = new String(entry.data, Constants.CHARSET);
				Continue(data,1000);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
			// Request a string response from the provided URL.
			CachedStringRequest stringRequest = new CachedStringRequest(
					Request.Method.GET, Constants.CATEGORY_URL,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							Continue(response,10);
						}
					}, new VolleyErrorListener(SplashActivity.this) {
						
						@Override
						public void handleVolleyError(VolleyError error, String message) {
							boolean ifCached = SharedPreferencesManager.getBooleanPreference(Constants.CACHE_KEY, false);
							if(!ifCached){
								alertUtil = new AlertUtil(SplashActivity.this,new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										switch(which){
										case Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK:
											fetchCategories() ;
											break;
										case Constants.DIALOG_INDEX_FIRST_BUTTON_CLICK:
											finish();
											break;
										}
									}
								});
								alertUtil.showMessageDialog(R.string.dialog_title_warning, getString(R.string.no_internet_connection),buttons);
							}
						}						
					});
					
			// Add the request to the RequestQueue.
			AppController.getInstance().addToRequestQueue(stringRequest);
		}

	}
	
	private void Continue(final String response,final int delay){
		//To avoid blocking UI thread while parsing HTML
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<MusicCategory> categories = MusicCategory.parseHtml(response);
				Collections.sort(categories);
				BeanHelper.addObjectForKey(categories, Constants.MENU_CATEGORY_LIST);
				handler.postDelayed(task, delay);
			}
		}).start();
		
		SharedPreferencesManager.setPreference(Constants.CACHE_KEY, true);
		Intent intent=new Intent(this,NavigationMenuMainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private Runnable task = new Runnable() {
		
		@Override
		public void run() {
			LocalBroadcastManager.getInstance(SplashActivity.this).sendBroadcast(new Intent(Constants.BROADCAST_ACTION_CATEGORY));
		}
	};
}
