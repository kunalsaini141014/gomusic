package com.gomusic.app.helpers.alert;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Spanned;

import com.gomusic.app.helpers.constants.Constants;

public class AlertUtil {
	private AlertDialog.Builder builder;
	private AlertDialogCallback callback;
	private Context context;
	private OnClickListener clickListener;

	public AlertUtil(Context context, OnClickListener clickListener) {
		this.context = context;
		this.clickListener = clickListener;
	}

	public AlertUtil(Context context) {
		this.context = context;
	}

	public void showItemsDialog(int title, String[] types) {
		if (builder == null) {
			builder = new Builder(context);
			builder.setCancelable(true);
		}
		builder.setTitle(title);
		builder.setItems(types, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (clickListener != null) {
					clickListener.onClick(dialog, which);
				} else if (callback != null
						&& context instanceof AlertDialogCallback) {
					callback = (AlertDialogCallback) context;
					callback.onDialogItemSelected(which);
				}
			}
		});
		builder.show();
	}

	public void showItemsDialog(int title, int itemsID) {
		if (builder == null) {
			builder = new Builder(context);
			builder.setCancelable(true);
		}
		builder.setTitle(title);
		builder.setItems(itemsID, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (clickListener != null) {
					clickListener.onClick(dialog, which);
				} else if (context != null
						&& context instanceof AlertDialogCallback) {
					callback = (AlertDialogCallback) context;
					callback.onDialogItemSelected(which);
				}
			}

		});
		builder.show();
	}

	/**
	 * 
	 * @param title
	 * @param messageId
	 * @param buttons
	 *            - buttons should be in order positive,cancel,n
	 */
	public void showMessageDialog(int title, String message, int[] buttons) {
		if (builder == null) {
			builder = new Builder(context);
			builder.setCancelable(true);
		}
		builder.setTitle(title);
		builder.setMessage("\n"+message+"\n\n");
		if (buttons != null) {
			for (int i = 0; i < buttons.length; i++) {
				switch (i) {
				case 0:
					builder.setPositiveButton(buttons[0],
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (clickListener != null) {
										clickListener
												.onClick(
														dialog,
														Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK);
									} else if (callback != null
											&& context instanceof AlertDialogCallback) {
										callback = (AlertDialogCallback) context;
										callback.onDialogItemSelected(Constants.DIALOG_INDEX_ZERO_BUTTON_CLICK);
									}
								}

							});
					break;

				case 1:
					builder.setNegativeButton(buttons[1],
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (clickListener != null) {
										clickListener
												.onClick(
														dialog,
														Constants.DIALOG_INDEX_FIRST_BUTTON_CLICK);
									} else if (callback != null
											&& context instanceof AlertDialogCallback) {
										callback = (AlertDialogCallback) context;
										callback.onDialogItemSelected(Constants.DIALOG_INDEX_FIRST_BUTTON_CLICK);
									}
								}

							});
					break;
				case 2:
					builder.setNeutralButton(buttons[2], new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (clickListener != null) {
								clickListener
										.onClick(
												dialog,
												Constants.DIALOG_INDEX_SECOND_BUTTON_CLICK);
							} else if (callback != null
									&& context instanceof AlertDialogCallback) {
								callback = (AlertDialogCallback) context;
								callback.onDialogItemSelected(Constants.DIALOG_INDEX_SECOND_BUTTON_CLICK);
							}
						}

					});
					break;
				}
			}
		}
		builder.show();
	}
	
	public void showMessageDialog(int title, int messageId, int[] buttons) {
		showMessageDialog(title,this.context.getString(messageId),buttons);
	}
	
	public void showMessageDialog(int title, String messageId) {
		showMessageDialog(this.context.getString(title),messageId);
	}

	public void showMessageDialog(String title, String message) {
		if (builder == null) {
			builder = new Builder(context);
			builder.setCancelable(true);
		}
		builder.setTitle(title);
		builder.setMessage("\n"+message+"\n\n");
		builder.show();
	}

	public void showMessageDialog(String title, Spanned message) {
		if (builder == null) {
			builder = new Builder(context);
			builder.setCancelable(true);
		}
		builder.setTitle(title);
		builder.setMessage("\n"+message+"\n\n");
		builder.show();
	}

	public interface AlertDialogCallback {
		public void onDialogItemSelected(int itemPosition);
	}
}
