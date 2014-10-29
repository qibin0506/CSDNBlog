package org.loader.dashenblog.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

// 尼玛， android的ScrollView竟然不能监听滑动事件
// 这里实现一下
public class CustomScrollView extends ScrollView {
	private OnScrollChangedListener mListener;
	
	public CustomScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(null != mListener && Math.abs(t - oldt) > 10) {
			mListener.onScrollYChanged();
		}
	}
	
	public void setOnScrollChangedListener(OnScrollChangedListener l) {
		mListener = l;
	}
	
	public interface OnScrollChangedListener {
		public void onScrollYChanged();
	}
}
