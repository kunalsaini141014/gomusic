package com.gomusic.app.activity;

import java.io.UnsupportedEncodingException;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Cache.Entry;
import com.android.volley.toolbox.ImageLoader;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gomusic.app.R;
import com.gomusic.app.R.layout;
import com.gomusic.app.adapter.SongsListAdapter;
import com.gomusic.app.helpers.alert.AlertUtil;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.network.NetworkUtil;
import com.gomusic.app.helpers.network.volley.CachedStringRequest;
import com.gomusic.app.model.AlbumDetails;
import com.gomusic.app.model.Song;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ArtistAlbumsActivity extends BaseActivity implements
		OnItemClickListener {
	private ProgressBarCircularIndeterminate progressBar;
	private ListView albumListView;
	private SongsListAdapter adapter;
	private AlertUtil alertUtil;
	private static final int[] buttons = new int[] {
			R.string.text_dialog_button_ok, R.string.text_dialog_button_cancel };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_albums);
		this.setUpActionBar();
		initView();
		String albumUrl = getIntent().getStringExtra(
				Constants.INTENT_DATA_ALBUM_URL);
		String artist = getIntent().getStringExtra(
				Constants.INTENT_DATA_ARTIST_NAME);
		fetchAlbumDetails(albumUrl);
		getCustomActionBar().setTitle(artist + " Albums");
	}

	private void initView() {
		progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
		albumListView = (ListView) findViewById(R.id.Albums_List_View);
		adapter = new SongsListAdapter(this, null, R.layout.album_list_item);
		albumListView.setAdapter(adapter);
		albumListView.setOnItemClickListener(this);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.home, menu); return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void fetchAlbumDetails(String url) {
		showProgress(true);
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(url);
		if (entry != null) {
			try {
				String data = new String(entry.data, Constants.CHARSET);
				initViewWithValues(data);
				showProgress(false);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				showProgress(false);
			}

		} else {
			// Request a string response from the provided URL.
			CachedStringRequest stringRequest = new CachedStringRequest(
					Request.Method.GET, url, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							initViewWithValues(response);
							showProgress(false);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							showProgress(false);
						}
					});
			// Add the request to the RequestQueue.
			AppController.getInstance().addToRequestQueue(stringRequest);
		}
	}

	private void initViewWithValues(String response) {
		adapter.setItems(Song.parseHtml(response));
	}

	private void showProgress(boolean show) {
		progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		final Song song = (Song) adapter.getItem(position);
		if (NetworkUtil.isConnected()) {
			if (NetworkUtil.isConnectedFast()) {
				continueAction(song);
			} else {
				alertUtil = new AlertUtil(this,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK:
									continueAction(song);
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
				alertUtil = new AlertUtil(this);
				alertUtil.showMessageDialog(R.string.dialog_title_warning,
						getString(R.string.no_internet_connection));
			}

		}
	}

	private void continueAction(Song song) {
		Intent intent = new Intent(this, AlbumDetailsActivity.class);
		intent.putExtra(Constants.INTENT_DATA_ALBUM_URL, song.downloadLink);
		startActivity(intent);
	}
}
