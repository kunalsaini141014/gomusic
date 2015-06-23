package com.gomusic.app.helpers.view;

public class SimpleTabItem {
	private final CharSequence mTitle;
	private final int mIndicatorColor;
	private final int mDividerColor;

	public SimpleTabItem(CharSequence title, int indicatorColor, int dividerColor) {
		mTitle = title;
		mIndicatorColor = indicatorColor;
		mDividerColor = dividerColor;
	}

	/**
	 * @return the title which represents this tab. In this sample this is used
	 *         directly by
	 *         {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
	 */
	public CharSequence getTitle() {
		return mTitle;
	}

	/**
	 * @return the color to be used for indicator on the
	 *         {@link SlidingTabLayout}
	 */
	public int getIndicatorColor() {
		return mIndicatorColor;
	}

	/**
	 * @return the color to be used for right divider on the
	 *         {@link SlidingTabLayout}
	 */
	public int getDividerColor() {
		return mDividerColor;
	}
}