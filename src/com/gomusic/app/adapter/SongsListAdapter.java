package com.gomusic.app.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gomusic.app.R;
import com.gomusic.app.model.Song;

public class SongsListAdapter extends BaseAdapter implements Filterable {
	private ArrayList<Song> items;
    private ArrayList<Song> filteredData = null;
	private LayoutInflater inflater;
	 private ItemFilter mFilter = new ItemFilter();
	 private int layout;

	public SongsListAdapter(Context ctx,ArrayList<Song> items,int layout){
		this.items = items;
		this.filteredData = items;
		inflater = LayoutInflater.from(ctx);
		this.layout = layout;
		if(this.items == null){
			this.items = new ArrayList<Song>();
		}
		if(this.filteredData == null){
			this.filteredData = new ArrayList<Song>();
		}
	}
	
	public SongsListAdapter(Context ctx,ArrayList<Song> items){
		this(ctx,items,R.layout.drawer_menu_item);
	}
	
	@Override
	public int getCount() {
		return filteredData == null || filteredData.isEmpty() ? 0 : filteredData.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredData == null || filteredData.isEmpty() ? null : filteredData.get(position);
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
			holder.textView = (TextView)convertView.findViewById(R.id.menu_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView.setText(filteredData.get(position).title);
		return convertView;
	}

	public synchronized void add(Song item) {
		if(!this.items.contains(item)){
			this.items.add(item);
			this.filteredData.add(item);
			this.notifyDataSetChanged();
		}
	}

	public synchronized void setItems(ArrayList<Song> collection) {
		if (collection == null || collection.isEmpty()) {
			this.removeAll();
		} else {
			this.items = collection;
			this.filteredData = new ArrayList<Song>(collection);
		}
		this.notifyDataSetChanged();
	}

	public synchronized void addAll(ArrayList<Song> collection) {
		if (collection != null && !collection.isEmpty()) {
			for(Song itm : collection){
				this.add(itm);
			}
		}
		this.notifyDataSetChanged();
	}

	public synchronized void removeAll() {
		this.items.clear();
		this.filteredData.clear();
		this.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Song> list = items;

            int count = list.size();
            final ArrayList<Song> nlist = new ArrayList<Song>(count);

            Song filterableSong ;

            for (int i = 0; i < count; i++) {
            	filterableSong = list.get(i);
                if (filterableSong.title.toLowerCase().contains(filterString)) {
                    nlist.add(filterableSong);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }

    }
	
	private static class ViewHolder{
		private TextView textView;
	}
}

