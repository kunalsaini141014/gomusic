package com.gomusic.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.gomusic.app.R;
import com.gomusic.app.fragment.BaseFragment;

public class BaseActivity extends ActionBarActivity {
	protected FragmentManager mFm;
	protected static final String TAG_TASK_FRAGMENT = "current_task_fragment";
	protected BaseFragment mTaskFragment;
	protected ActionBar actionBar;
	protected FragmentTransaction fTransaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
	}

	public ActionBar getCustomActionBar() {
		return actionBar;
	}

	public void setUpActionBarWithDrawer() {
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	public void setUpActionBar() {
		// To display action bar app logo
		actionBar.setIcon(R.drawable.ic_launcher);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	public void setUpSearchActionBar() {
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
