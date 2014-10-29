package org.loader.dashenblog.customview;

import org.loader.dashenblog.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomListView extends ListView {
	private RelativeLayout mFooterLayout;
	private TextView mFooterText;
	
	public CustomListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mFooterLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.footer_view, null);
		mFooterText = (TextView) mFooterLayout.findViewById(R.id.footer_text);
		addFooterView(mFooterLayout);
	}
	
	public void showFooterView(boolean isShow) {
		mFooterLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}
	
	public void setFooterText(int resId) {
		mFooterText.setText(resId);
	}
}
