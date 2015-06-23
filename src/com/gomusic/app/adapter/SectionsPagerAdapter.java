package com.gomusic.app.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gomusic.app.R;
import com.gomusic.app.fragment.PlaceholderFragment;
import com.gomusic.app.fragment.SectionedFragment;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.view.SimpleTabItem;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	private List<SimpleTabItem> mTabs;
	private Map<Integer, String> sections = new HashMap<Integer, String>(); 

	public SectionsPagerAdapter(FragmentManager fm,List<SimpleTabItem> mTabs) {
		super(fm);
		this.mTabs = mTabs;
		this.sections.put(1,Constants.TAB_ITEM_1);
		this.sections.put(2, Constants.TAB_ITEM_2);
		this.sections.put(3, Constants.TAB_ITEM_3);
		this.sections.put(4, Constants.TAB_ITEM_4);
		this.sections.put(5, Constants.TAB_ITEM_5);
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class
		// below).
		if(position == 3 || position == 4){
			return SectionedFragment.newInstance(position + 1,this.sections.get(position + 1));
		}else{
			return PlaceholderFragment.newInstance(position + 1,this.sections.get(position + 1));
		}
		
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return this.mTabs.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTabs.get(position).getTitle();
	}
}