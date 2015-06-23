package com.gomusic.app.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gomusic.app.R;

public class AlphabeticListAdapter extends BaseAdapter {
	private List<String> items;
	private LayoutInflater inflater;
	 private int layout;

	public AlphabeticListAdapter(Context ctx){
		inflater = LayoutInflater.from(ctx);
		this.layout = R.layout.list_item_alphabet;
		if(this.items == null){
			this.items = Arrays.asList(new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"});
		}
	}
	
	@Override
	public int getCount() {
		return items == null || items.isEmpty() ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items == null || items.isEmpty() ? null : items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(this.layout, parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.alphabet);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView.setText(items.get(position));
		return convertView;
	}
	
	private static class ViewHolder{
		private TextView textView;
	}
}

