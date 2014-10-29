package org.loader.dashenblog.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ConcernAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, String>> mData;
	private OnDeleteListener mListener;
	
	public ConcernAdapter(Context context,
			ArrayList<HashMap<String, String>> data,
			OnDeleteListener l) {
		mContext = context;
		mData = data;
		mListener = l;
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
			convertView = View.inflate(mContext, R.layout.concern_item, null);
			
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_concern_title);
			holder.btn = (ImageButton) convertView.findViewById(R.id.ib_concern_btn);
		
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText(mData.get(position).get("csdnid"));
		final int pos = position;
		holder.btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onDelete(mData.get(pos).get("_id"));
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		ImageButton btn;
	}
	
	public interface OnDeleteListener {
		public void onDelete(String id);
	}
}
