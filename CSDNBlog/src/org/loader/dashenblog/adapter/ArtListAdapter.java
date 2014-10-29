package org.loader.dashenblog.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ArtListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, String>> mData;
	
	public ArtListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
		mContext = context;
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			convertView = View.inflate(mContext, R.layout.art_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_art_item_title);
			holder.des = (TextView) convertView.findViewById(R.id.tv_art_item_des);
			holder.date = (TextView) convertView.findViewById(R.id.tv_art_item_date);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(mData.get(position).get("title"));
		holder.des.setText(mData.get(position).get("des"));
		holder.date.setText(mData.get(position).get("date"));
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		TextView des;
		TextView date;
	}
}
