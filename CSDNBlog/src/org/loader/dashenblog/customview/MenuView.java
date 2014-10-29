package org.loader.dashenblog.customview;

import org.loader.dashenblog.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MenuView extends FrameLayout {
	private TextView mConcernMenu;
	private TextView mLocalBlogMenu;
	private TextView mClearCacheMenu;
	private TextView mBlogNotifyMenu;
	private TextView mAboutMenu;
	
	public MenuView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		LayoutInflater.from(context).inflate(R.layout.custom_menuview_layout, this);
		mConcernMenu = (TextView) findViewById(R.id.menu_concern);
		mLocalBlogMenu = (TextView) findViewById(R.id.menu_local_blog);
		mClearCacheMenu = (TextView) findViewById(R.id.menu_clear);
		mBlogNotifyMenu = (TextView) findViewById(R.id.menu_blog_notify);
		mAboutMenu = (TextView) findViewById(R.id.menu_about);
	}
	
	public void setOnMenuItemClickListener(View.OnClickListener l) {
		mConcernMenu.setOnClickListener(l);
		mLocalBlogMenu.setOnClickListener(l);
		mClearCacheMenu.setOnClickListener(l);
		mBlogNotifyMenu.setOnClickListener(l);
		mAboutMenu.setOnClickListener(l);
	}
}
