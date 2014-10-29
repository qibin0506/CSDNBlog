package org.loader.dashenblog.customview;

import org.loader.dashenblog.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWrapper;
	private ViewGroup mMenu;
	private ViewGroup mContent;
	
	private int mScreenWidth;
	private int mMenuWidth;
	private int mPaddingRight;
	
	private boolean isOpen;
	private boolean once;
	
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);
		mPaddingRight = (int) ta.getDimension(R.styleable.SlidingMenu_paddingRight, 50);
		ta.recycle();
		
		mPaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mPaddingRight, context.getResources().getDisplayMetrics());
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mWrapper = (LinearLayout) getChildAt(0);
		mMenu = (ViewGroup) mWrapper.getChildAt(0);
		mContent = (ViewGroup) mWrapper.getChildAt(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(!once) {
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mPaddingRight;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed) {
			scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		// 1 ~ 0
		float scale = l * 1.0f / mMenuWidth;
		mMenu.setTranslationX(l - (mMenuWidth / 7) * scale);
		
		// content 1 ~ 0.7 => scale*0.3 + 0.7
		mContent.setPivotX(0);
		mContent.setPivotY(mContent.getMeasuredHeight() / 2);
		mContent.setScaleX(0.8f + 0.2f * scale);
		mContent.setScaleY(0.8f + 0.2f * scale);
		
		// menu 0.7 ~ 1
		mMenu.setPivotX(0);
		mMenu.setPivotY(mMenu.getMeasuredHeight() / 2);
		mMenu.setScaleX(1.0f - 0.3f * scale);
		mMenu.setScaleY(1.0f - 0.3f * scale);
		mMenu.setAlpha(1.0f - 0.3f * scale);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_UP) {
			int scrollX = getScrollX();
			if(scrollX >= mMenuWidth / 2) {
				smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			}else {
				smoothScrollTo(0, 0);
				isOpen = true;
			}
			
			return true;
		}
		
 		return super.onTouchEvent(ev);
	}
	
	public void toggle() {
		if(isOpen) {
			smoothScrollTo(mMenuWidth, 0);
		}else {
			smoothScrollTo(0, 0);
		}
		
		isOpen = !isOpen;
	}
	
	public void scrollToNormal() {
		scrollTo(mMenuWidth, 0);
		isOpen = false;
	}
}
