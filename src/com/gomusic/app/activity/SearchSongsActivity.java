package com.gomusic.app.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gomusic.app.R;
import com.gomusic.app.adapter.SongsListAdapter;
import com.gomusic.app.helpers.alert.AlertUtil;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.network.NetworkUtil;
import com.gomusic.app.helpers.network.volley.JsonRequest;
import com.gomusic.app.helpers.network.volley.VolleyErrorListener;
import com.gomusic.app.model.Song;

public class SearchSongsActivity extends FragmentActivity implements
		OnItemClickListener {
	private ArrayList<Song> songs;
	private EditText searchBarText;
	private ProgressBarCircularIndeterminate progressBar;
	private ListView songsListView;
	private SongsListAdapter adapter;
	private AlertUtil alertUtil;
	private static final int[] buttons = new int[] {
			R.string.text_dialog_button_ok, R.string.text_dialog_button_cancel };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_songs);
		initView();
	}

	private void initView() {
		searchBarText = (EditText) findViewById(R.id.searchBarText);
		progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
		songsListView = (ListView) findViewById(R.id.album_songs_listview);
		adapter = new SongsListAdapter(this, null, R.layout.album_list_item);
		songsListView.setAdapter(adapter);
		songsListView.setOnItemClickListener(this);
		progressBar.setVisibility(View.GONE);
		fetchAlbumDetails("http://pleer.com/browser-extension/search?q=pitbull&limit=35&page=1");
	}

	private void fetchAlbumDetails(String url) {
		showProgress(true);
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(url);
		if (entry != null) {
			try {
				String data = new String(entry.data, Constants.CHARSET);
				try {
					initViewWithValues(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				showProgress(false);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				showProgress(false);
			}

		} else {
			JsonRequest jsonObjReq = new JsonRequest(Method.GET, url, null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							initViewWithValues(response);
							showProgress(false);
						}
					}, new VolleyErrorListener(this) {

						@Override
						public void handleVolleyError(VolleyError error,
								String message) {
							showProgress(false);
						}

					});

			jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsonObjReq);
		}
	}

	private void initViewWithValues(JSONObject response) {
		ArrayList<Song> songs = new ArrayList<Song>();
		try {
			JSONArray arrays = response.getJSONArray("tracks");
			if (arrays != null && arrays.length() > 0) {
				for (int i = 0; i < arrays.length(); i++) {
					JSONObject obj = arrays.getJSONObject(i);
					Song song = new Song(obj.getString("track"),
							obj.getString("artist"), obj.getString("file"));
					songs.add(song);
				}
			}
		} catch (Exception ex) {

		}

		adapter.setItems(songs);
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
								case R.string.text_dialog_button_ok:
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
		Intent intent = new Intent(this, SongDetailActivity.class);
		intent.putExtra(Constants.INTENT_DATA_SONG_URL, song.downloadLink);
		startActivity(intent);
	}
}
