package com.gomusic.app.activity;

import java.io.UnsupportedEncodingException;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gomusic.app.R;
import com.gomusic.app.helpers.alert.AlertUtil;
import com.gomusic.app.helpers.app.AppController;
import com.gomusic.app.helpers.constants.Constants;
import com.gomusic.app.helpers.network.NetworkUtil;
import com.gomusic.app.helpers.network.volley.CachedStringRequest;
import com.gomusic.app.model.SongDetails;

public class SongDetailActivity extends BaseActivity implements OnClickListener{
	private TextView songTitle, songArtist, album, play48Bit, play128Bit,
			play320Bit, download48Bit, download128Bit, download320Bit;
	private ImageView albumIcon;
	private ImageLoader imageLoader;
	private SongDetails details;
	private RelativeLayout songDetails;
	private LinearLayout playDetails,downloadDetails;
	private ProgressBarCircularIndeterminate progressBar;
	private AlertUtil alertUtil;
	private static final int[] buttons = new int[]{R.string.text_dialog_button_ok,R.string.text_dialog_button_cancel};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_detail);
		this.setUpActionBar();
		initView();
		imageLoader = AppController.getInstance().getImageLoader();
		String songUrl = getIntent().getStringExtra(
				Constants.INTENT_DATA_SONG_URL);
		fetchSongDetails(songUrl);
	}

	private void initView() {
		songDetails = (RelativeLayout)findViewById(R.id.song_details);
		playDetails = (LinearLayout)findViewById(R.id.play_details);
		downloadDetails = (LinearLayout)findViewById(R.id.download_details);
		progressBar = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar);
		
		songTitle = (TextView) songDetails.findViewById(R.id.song_title);
		songArtist = (TextView) songDetails.findViewById(R.id.song_artist);
		songArtist.setOnClickListener(this);
		album = (TextView) songDetails.findViewById(R.id.song_album);
		albumIcon = (ImageView) songDetails.findViewById(R.id.song_image);
		
		play48Bit = (TextView) playDetails.findViewById(R.id.play_48_bit);
		play48Bit.setOnClickListener(this);
		play128Bit = (TextView) playDetails.findViewById(R.id.play_128_bit);
		play128Bit.setOnClickListener(this);
		play320Bit = (TextView) playDetails.findViewById(R.id.play_320_bit);
		play320Bit.setOnClickListener(this);
		
		download48Bit = (TextView) downloadDetails.findViewById(R.id.download_48_bit);
		download48Bit.setOnClickListener(this);
		download128Bit = (TextView) downloadDetails.findViewById(R.id.download_128_bit);
		download128Bit.setOnClickListener(this);
		download320Bit = (TextView) downloadDetails.findViewById(R.id.download_320_bit);
		download320Bit.setOnClickListener(this);
		
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}*/

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

	private void fetchSongDetails(String url) {
		showProgress(true);
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		url = Constants.CATEGORY_URL + url;
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
		details = SongDetails.parseHtml(response);
		songTitle.setText("Song : " + details.songTitle);
		songArtist.setText("Artist : " + details.songArtist);
		album .setText("Album : " + details.album);
		if(!TextUtils.isEmpty(details.linkFor48Bit)){
			play48Bit .setText("Play in 48 Kbps");
			download48Bit.setText("Download In 48 Kbps");
		}else{
			play48Bit.setVisibility(View.GONE);
			play48Bit.setVisibility(View.GONE);
		}
		
		if(!TextUtils.isEmpty(details.linkFor128Bit)){
			play128Bit.setText("Play in 128 Kbps");
			download128Bit.setText("Download In 128 Kbps");
		}else{
			play128Bit.setVisibility(View.GONE);
			download128Bit.setVisibility(View.GONE);
		}
		
		if(!TextUtils.isEmpty(details.linkFor320Bit)){
			play320Bit.setText("Play in 320 Kbps");
			download320Bit .setText("Download In 320 Kbps");
		}else{
			play320Bit.setVisibility(View.GONE);
			download320Bit.setVisibility(View.GONE);
		}
		
		// Loading image with placeholder and error image
		if(!TextUtils.isEmpty(details.albumIcon)){
			imageLoader.get(details.albumIcon, ImageLoader.getImageListener(
					albumIcon, R.drawable.ic_app_logo,
					R.drawable.ic_app_logo));
		}
		getCustomActionBar().setTitle(details.songTitle);
	}

	@Override
	public void onClick(View v) {
		final int actionId = v.getId();
		if(NetworkUtil.isConnected()){
			if(NetworkUtil.isConnectedFast()){
				continueAction(actionId);
			}else{
				alertUtil = new AlertUtil(this,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK:
							continueAction(actionId);
							break;
						}
					}
				});
				alertUtil.showMessageDialog(R.string.dialog_title_warning, getString(R.string.slow_internet_connection),buttons);
			}
		}else{
			alertUtil = new AlertUtil(this);
			alertUtil.showMessageDialog(R.string.dialog_title_warning, getString(R.string.no_internet_connection));
		}
	}
	
	private void continueAction(int actionId) {
		switch(actionId){
		case R.id.song_artist :
			Intent intent = new Intent(this,
					ArtistAlbumsActivity.class);
			intent.putExtra(Constants.INTENT_DATA_ALBUM_URL,details.artitstLink);
			intent.putExtra(Constants.INTENT_DATA_ARTIST_NAME, details.songArtist);
			startActivity(intent);
			break;
		case R.id.play_48_bit :
			playMp3File(details.linkFor48Bit);
			break;
		case R.id.play_128_bit:
			playMp3File(details.linkFor128Bit.replace("p128", "p48"));
			break;
		case R.id.play_320_bit :
			playMp3File(details.linkFor320Bit);
			break;
		case R.id.download_48_bit :
			downloadMp3File(details.linkFor48Bit,details.songTitle+"-48Kbps");
			break;
		case R.id.download_128_bit :
			downloadMp3File(details.linkFor128Bit.replace("p128", "p48"),details.songTitle+"-128Kbps");
			break;
		case R.id.download_320_bit :
			downloadMp3File(details.linkFor320Bit,details.songTitle+"-320Kbps");
			break;
		}
	}
	
	private void downloadMp3File(String mp3File,String title) {
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mp3File));
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle(title);
        
		// This put the download in the same Download dir the browser uses
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp3");

		// When downloading music and videos they will be listed in the player
		// (Seems to be available since Honeycomb only)
        request.allowScanningByMediaScanner();

		// Notify user when download is completed
		// (Seems to be available since Honeycomb only)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		// Start download
		DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		dm.enqueue(request);
	}
	
	private void playMp3File(String uri) {
		Uri myUri = Uri.parse(uri);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
		intent.setDataAndType(myUri, "audio/*"); 
		startActivity(intent);
	}
	
	private void showProgress(boolean show) {
		songDetails.setVisibility(show ? View.GONE : View.VISIBLE);
		playDetails.setVisibility(show ? View.GONE : View.VISIBLE);
		downloadDetails.setVisibility(show ? View.GONE : View.VISIBLE);
		progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}
}
