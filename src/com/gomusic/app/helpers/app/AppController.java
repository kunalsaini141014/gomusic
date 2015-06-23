package com.gomusic.app.helpers.app;

import java.io.File;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.gomusic.app.R;
import com.gomusic.app.helpers.network.volley.LruBitmapCache;

@ReportsCrashes(
		mailTo = "kunalsaini141014@gmail.com", 
		customReportContent = {ReportField.APP_VERSION_CODE,ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
		mode = ReportingInteractionMode.TOAST, 
		resToastText = R.string.crash_toast_text)
public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private File cacheDir;
	private File filesDir;
	private ImageLoader mImageLoader;
	
	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		// The following line triggers the initialization of ACRA
        ACRA.init(this);
  	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}
	
	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(com.android.volley.Request<T> req,
			String tag) {
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

	@Override
	public File getCacheDir() {
		if (cacheDir == null) {
			cacheDir = (getExternalCacheDir() != null) ? getExternalCacheDir()
					: super.getCacheDir();
		}

		return cacheDir;
	}

	@Override
	public File getFilesDir() {
		if (filesDir == null) {
			filesDir = (getExternalFilesDir(null) != null) ? getExternalFilesDir(null)
					: super.getFilesDir();
		}
		return filesDir;
	}

	public int dpToPx(int dp) {
		return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
	}
	
	public float pxToDP(int px) {
		float dp = px / (getResources().getDisplayMetrics().densityDpi / 160f);
		return dp;
	}
	
	public static void setPreference(String key,String value){
		SharedPreferencesManager.setPreference(key,value);
	}
}