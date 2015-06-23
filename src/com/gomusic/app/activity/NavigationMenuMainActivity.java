package com.gomusic.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gomusic.app.R;
import com.gomusic.app.adapter.SectionsPagerAdapter;
import com.gomusic.app.fragment.NavigationDrawerFragment;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.view.SimpleTabItem;
import com.gomusic.app.helpers.view.SlidingTabLayout;
import com.gomusic.app.model.MusicCategory;

public class NavigationMenuMainActivity extends BaseActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		SearchView.OnQueryTextListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	/**
	 * A custom {@link ViewPager} title strip which looks much like Tabs present
	 * in Android v4.0 and above, but is designed to give continuous feedback to
	 * the user when scrolling.
	 */
	private SlidingTabLayout mSlidingTabLayout;
	/**
	 * A {@link ViewPager} which will be used in conjunction with the
	 * {@link SlidingTabLayout} above.
	 */
	private ViewPager mViewPager;
	/**
	 * List of {@link SamplePagerItem} which represent this sample's tabs.
	 */
	private List<SimpleTabItem> mTabs = new ArrayList<SimpleTabItem>();
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private FrameLayout navigationDrawerContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		navigationDrawerContainer = (FrameLayout) findViewById(R.id.navigation_drawer_container);
		mNavigationDrawerFragment = new NavigationDrawerFragment(this);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.navigation_drawer, mNavigationDrawerFragment)
				.commitAllowingStateLoss();
		mTitle = getTitle();
		// BEGIN_INCLUDE (populate_tabs)
		/**
		 * Populate our tab list with tabs. Each item contains a title,
		 * indicator color and divider color, which are used by
		 * {@link SlidingTabLayout}.
		 */
		mTabs.add(new SimpleTabItem(Constants.TOP_20_SONGS, 0, 0));
		mTabs.add(new SimpleTabItem(Constants.TOP_20_ALBUMS, 0, 0));
		mTabs.add(new SimpleTabItem(Constants.LATEST_ALBUMS, 0, 0));
		mTabs.add(new SimpleTabItem(Constants.ARTISTS, 0, 0));
		mTabs.add(new SimpleTabItem(Constants.ALL_ALBUMS, 0, 0));

		// BEGIN_INCLUDE (setup_viewpager)
		// Get the ViewPager and set it's PagerAdapter so that it can display
		// items
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(new SectionsPagerAdapter(
				getSupportFragmentManager(), mTabs));
		// END_INCLUDE (setup_viewpager)

		// BEGIN_INCLUDE (setup_slidingtablayout)
		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the
		// ViewPager has had
		// it's PagerAdapter set.
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		int[] colors = new int[] { Color.WHITE, Color.WHITE, Color.WHITE };
		int[] dcolors = new int[] { Color.TRANSPARENT, Color.TRANSPARENT,
				Color.TRANSPARENT };
		mSlidingTabLayout.setSelectedIndicatorColors(colors);
		mSlidingTabLayout.setDividerColors(dcolors);
		mSlidingTabLayout.setViewPager(mViewPager);
		mNavigationDrawerFragment.setUp(navigationDrawerContainer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position,
			MusicCategory category) {
		// update the main content by replacing fragments
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(category == null ? mTitle : category.text);
		Intent intent = new Intent(Constants.ACTION_MENU_ITEM_CLICK);
		Constants.INTENT_DATA_CAT_ID = category._id;
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getCustomActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.home, menu);
			// restoreActionBar();
			setupSearchView(menu);
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	private void setupSearchView(Menu menu) {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final MenuItem searchItem = menu.findItem(R.id.action_filter);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		if (searchManager != null && searchView != null) {
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(this);
			searchView
					.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View view,
								boolean queryTextFocused) {
							ActivityCompat
									.invalidateOptionsMenu(NavigationMenuMainActivity.this);
							if (!queryTextFocused
									&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
									&& searchItem.isActionViewExpanded()) {
								searchItem.collapseActionView();
							}
						}
					});
		}
	}

	public boolean onQueryTextChange(String newText) {
		Intent intent = new Intent(Constants.ACTION_QUERY_TEXT_CHANGED);
		Constants.INTENT_DATA_SEARCHABLE_SEQUENCE = newText;
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_filter) {
			ActivityCompat.invalidateOptionsMenu(this);
			return true;
		} else if (id == R.id.action_view) {
			Intent i = new Intent();
			i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
