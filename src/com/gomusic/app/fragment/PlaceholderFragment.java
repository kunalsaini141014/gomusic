package com.gomusic.app.fragment;

import java.io.UnsupportedEncodingException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gomusic.app.R;
import com.gomusic.app.activity.AlbumDetailsActivity;
import com.gomusic.app.activity.SongDetailActivity;
import com.gomusic.app.adapter.SongsListAdapter;
import com.gomusic.app.helpers.alert.AlertUtil;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.network.NetworkUtil;
import com.gomusic.app.helpers.network.volley.CachedStringRequest;
import com.gomusic.app.model.Song;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements
		OnItemClickListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private ListView Songs_List_View;
	private String link = Constants.TAB_ITEM_1;
	private SongsListAdapter adapter;
	private ProgressBarCircularIndeterminate progressBar;
	private AlertUtil alertUtil;
	private static final int[] buttons = new int[] {
			R.string.text_dialog_button_ok, R.string.text_dialog_button_cancel };
	private TextView noContent;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private boolean fromCache = true;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PlaceholderFragment newInstance(int sectionNumber, String arg) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putString("link", arg);
		fragment.setArguments(args);
		fragment.link = arg;
		return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			link = savedInstanceState.getString("link");
		}
		adapter = new SongsListAdapter(getActivity(), null,
				R.layout.album_list_item);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_placeholder,
				container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						fromCache = false;
						fetchSongs(Constants.CATEGORY_URL + link
								+ Constants.INTENT_DATA_CAT_ID);
					}
				});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.White);
		mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.color_action_bar_background));
		Songs_List_View = (ListView) rootView
				.findViewById(R.id.Songs_List_View);
		Songs_List_View.setAdapter(adapter);
		Songs_List_View.setOnItemClickListener(this);
		progressBar = (ProgressBarCircularIndeterminate) rootView
				.findViewById(R.id.progressBar);
		noContent = (TextView) rootView.findViewById(R.id.no_content);
		return rootView;
	}

	@Override
	public void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_MENU_ITEM_CLICK);
		filter.addAction(Constants.ACTION_QUERY_TEXT_CHANGED);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver, filter);
		fetchSongs(Constants.CATEGORY_URL + link + Constants.INTENT_DATA_CAT_ID);
		super.onStart();
	}

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				receiver);
		adapter.removeAll();
		super.onStop();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.ACTION_MENU_ITEM_CLICK)) {
				// Toast.makeText(getActivity(), catID,
				// Toast.LENGTH_SHORT).show();
				fetchSongs(Constants.CATEGORY_URL + link
						+ Constants.INTENT_DATA_CAT_ID);
			} else if (intent.getAction().equals(
					Constants.ACTION_QUERY_TEXT_CHANGED)) {
				adapter.getFilter().filter(
						Constants.INTENT_DATA_SEARCHABLE_SEQUENCE);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		receiver = null;
	}

	private void fetchSongs(String url) {
		showProgress(true);
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(url);
		if (entry != null && fromCache) {
			try {
				String data = new String(entry.data, Constants.CHARSET);
				adapter.setItems(Song.parseHtml(data));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			showProgress(false);

		} else {
			// Request a string response from the provided URL.
			CachedStringRequest stringRequest = new CachedStringRequest(
					Request.Method.GET, url, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							fromCache = true;
							adapter.setItems(Song.parseHtml(response));
							showProgress(false);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							fromCache = true;
							showProgress(false);
						}
					});
			// Add the request to the RequestQueue.
			AppController.getInstance().addToRequestQueue(stringRequest);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		final Song song = (Song) adapter.getItem(position);

		if (NetworkUtil.isConnected()) {
			if (NetworkUtil.isConnectedFast()) {
				continueAction(song);
			} else {
				alertUtil = new AlertUtil(getActivity(),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK:
									continueAction(song);
									break;
								default:
									break;
								}
							}
						});
				alertUtil.showMessageDialog(R.string.dialog_title_warning,
						getString(R.string.slow_internet_connection), buttons);
			}
		} else {

			Cache cache = AppController.getInstance().getRequestQueue()
					.getCache();
			Entry entry = cache.get(Constants.CATEGORY_URL + song.downloadLink);
			if (entry != null) {
				continueAction(song);
			} else {
				alertUtil = new AlertUtil(getActivity());
				alertUtil.showMessageDialog(R.string.dialog_title_warning,
						getString(R.string.no_internet_connection));
			}
		}
	}

	private void continueAction(Song song) {
		if (this.link.equals(Constants.TAB_ITEM_1)) {
			Intent intent = new Intent(getActivity(), SongDetailActivity.class);
			intent.putExtra(Constants.INTENT_DATA_SONG_URL, song.downloadLink);
			getActivity().startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(),
					AlbumDetailsActivity.class);
			intent.putExtra(Constants.INTENT_DATA_ALBUM_URL, song.downloadLink);
			getActivity().startActivity(intent);
		}
	}

	private void showProgress(final boolean show) {
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				progressBar.setVisibility(show ? View.GONE : View.GONE);
			}
		}, 300);
		
		mSwipeRefreshLayout.setRefreshing(show);

		if (show) {
			adapter.removeAll();
			noContent.setVisibility(View.GONE);
		} else {
			noContent.setVisibility(adapter.getCount() > 0 ? View.GONE
					: View.VISIBLE);
		}
	}
	
	private Handler handler = new Handler();
}