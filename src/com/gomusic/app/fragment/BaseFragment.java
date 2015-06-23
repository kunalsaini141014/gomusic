package com.gomusic.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {

	protected Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		handler = new Handler();
	}

	@SuppressLint("NewApi")
	protected void simulateClickEvent(final View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			view.setAlpha(0.5f);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (view != null) {
						view.setAlpha(1.0f);
					}
				}
			}, 300);
		}

	}
	
	protected void hideSoftKeyboard(Activity ctx,View view){
		InputMethodManager inputManager = (InputMethodManager)ctx. getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	protected void showSoftKeyboard(Activity ctx,View view) {
	    if (view.requestFocus()) {
	        InputMethodManager imm = (InputMethodManager)
	        		ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	    }
	}
	
	/**
	 * Represents action to be executed when back button is pressed on Activity and then passed on to fragment.
	 *  true - if you consumed the back press event.
	 *  false - if you dont want to consume event and want default action to be taken.
	 * @return
	 */
	public boolean onBackPressed() {
		return false;
	}
	
	/**
	 * Represents action to be executed on Alert Dialog Buttons/Items Click 
	 * @param itemPosition
	 */
	public void onDialogItemSelected(int itemPosition) {
		
	}
}
