package com.gomusic.app.helpers.network.volley;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.gomusic.app.R;
import com.gomusic.app.helpers.alert.ToastUtil;

public abstract class VolleyErrorListener implements ErrorListener{
	private Context context;
	
	public VolleyErrorListener(Context context){
		this.context = context;
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		String errorMessage = context.getString(R.string.error_request_failure);
		if (error instanceof TimeoutError) {
			errorMessage = context.getString(R.string.error_network_timeout);
	    }else if (error instanceof NoConnectionError) {
	    	errorMessage = context.getString(R.string.error_no_connection);
	    }  else if (error instanceof AuthFailureError) {
	    	errorMessage = context.getString(R.string.error_auth_failure);
	    } else if (error instanceof ServerError) {
	    	errorMessage = context.getString(R.string.error_server_failure);
	    } else if (error instanceof NetworkError) {
	    	errorMessage = context.getString(R.string.error_network_failure);
	    } else if (error instanceof ParseError) {
	    	errorMessage = context.getString(R.string.error_parsing_failure);
	    }
		ToastUtil.showToast(context,errorMessage,Toast.LENGTH_LONG);
		handleVolleyError(error, errorMessage);
	}
	
	public abstract void handleVolleyError(VolleyError error, String message);

}
