package com.gomusic.app.helpers.alert;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gomusic.app.R;

public class ToastUtil {
	private static LayoutInflater inflater;
	private static View toastLayout;
	private static TextView textView;

	public static synchronized void showToast(Context ctx, String text,
			int duration,int intlayoutID) {
		if (inflater == null) {
			inflater = LayoutInflater.from(ctx);
		}
		if (toastLayout == null) {
			toastLayout = inflater.inflate(intlayoutID, null);
			textView = (TextView) toastLayout.findViewById(R.id.toast_text);
		}
		textView.setText(text);
		Toast toast = new Toast(ctx);
		toast.setGravity(Gravity.BOTTOM, 0, 150);
		toast.setDuration(duration);
		toast.setView(toastLayout);
		toast.show();
	}
	
	public static synchronized void showToast(Context ctx, int textId,
			int duration) {
		ToastUtil.showToast(ctx,ctx.getResources().getString(textId),duration,R.layout.toast_layout);
	}
	
	public static synchronized void showToast(Context ctx, int textId,
			int duration,int intlayoutID) {
		ToastUtil.showToast(ctx,ctx.getResources().getString(textId),duration,R.layout.toast_layout);
	}
	
	public static synchronized void showToast(Context ctx, String text,
			int duration) {
		ToastUtil.showToast(ctx,text,duration,R.layout.toast_layout);
	}
}
