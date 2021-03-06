package org.loader.dashenblog.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, String>> mData;
	
	public CategoryAdapter(Context context, ArrayList<HashMap<String, String>> data) {
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
			convertView = View.inflate(mContext, R.layout.category_item, null);
			holder = new ViewHolder();
			holder.cateName = (TextView) convertView.findViewById(R.id.tv_cate_name);
			holder.cateCount = (TextView) convertView.findViewById(R.id.tv_cate_count);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.cateName.setText(mData.get(position).get("name"));
		holder.cateCount.setText(mData.get(position).get("count"));
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView cateName;
		TextView cateCount;
	}
}
