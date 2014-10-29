package org.loader.dashenblog.customview;

import org.loader.dashenblog.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleView extends FrameLayout {
	private ImageView mTitleIcon;  // title icon
	private TextView mTitleTitle;  // title
	private ImageView mTitleWriter; // writer logo
	
	public TitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.title_layout, this);
		mTitleIcon = (ImageView) findViewById(R.id.iv_title_icon);
		mTitleTitle = (TextView) findViewById(R.id.tv_title_title);
		mTitleWriter = (ImageView) findViewById(R.id.iv_title_writer);
	}
	
	public void setTitleText(String title) {
		mTitleTitle.setText(title);
	}
	
	public void setTitleIcon(int resId) {
		mTitleIcon.setImageResource(resId);
	}
	
	public void setWriterIconVisible(boolean visible) {
		mTitleWriter.setVisibility(visible ? View.VISIBLE : View.GONE);
		
	}
	
	public void setOnTitleIconClickListener(View.OnClickListener l) {
		mTitleIcon.setOnClickListener(l);
	}
	
	public void setOnTitleWriterClickListener(View.OnClickListener l) {
		mTitleWriter.setOnClickListener(l);
	}
}
