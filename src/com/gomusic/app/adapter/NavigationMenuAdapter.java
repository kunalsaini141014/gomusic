package com.gomusic.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gomusic.app.R;
import com.gomusic.app.model.MusicCategory;
import com.gomusic.app.model.Song;

public class NavigationMenuAdapter extends BaseAdapter{
	private ArrayList<MusicCategory> items;
	private LayoutInflater inflater;

	public NavigationMenuAdapter(Context ctx,ArrayList<MusicCategory> items){
		this.items = items;
		inflater = LayoutInflater.from(ctx);
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
			convertView = inflater.inflate(R.layout.drawer_menu_item, parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView)convertView.findViewById(R.id.menu_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView.setText(items.get(position).text);
		return convertView;
	}
	
	public void setItems(ArrayList<MusicCategory> collection) {
		if (collection == null || collection.isEmpty()) {
			this.removeAll();
		} else {
			this.items = collection;
		}
		this.notifyDataSetChanged();
	}

	public void addAll(ArrayList<MusicCategory> collection) {
		if (collection != null && !collection.isEmpty()) {
			this.items.addAll(collection);
		}
		this.notifyDataSetChanged();
	}

	public void removeAll() {
		this.items.clear();
		this.notifyDataSetChanged();
	}

	private static class ViewHolder{
		private TextView textView;
	}
}
