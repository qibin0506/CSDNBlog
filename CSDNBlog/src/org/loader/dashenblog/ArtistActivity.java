package org.loader.dashenblog;

import java.util.Timer;
import java.util.TimerTask;

import org.loader.dashenblog.customview.CustomScrollView;
import org.loader.dashenblog.customview.TitleView;
import org.loader.dashenblog.engine.ProcessArtist;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ZoomButton;

public class ArtistActivity extends Activity {
	private CustomScrollView mScrollView; // 添加了滚动监听事件的ScrollView
	private TitleView mTitleView;  // 模仿Actionbar
	private TextView mContentView; // 内容
	private ProgressBar mWait;  // 等待
	
	private LinearLayout mZoom;
	private ZoomButton mZoomout; // 缩小
	private ZoomButton mZoomin; // 放大
	
	private volatile boolean isContentPressed; // 双击放大
	private int mZoomHint = 0;  // 最大到2
	private float[] mTextSizes = new float[] {15.0f, 17.0f, 19.0f, 21.0f};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.artist_layout);
		
		if(null == getIntent().getStringExtra("title")) {
			String url = getIntent().getStringExtra("url");
			new ProcessArtist().process(url, new ArtistProcessListener());
			initViews(false);
		}else {
			initViews(true);
		}
	}

	private void initViews(boolean isAttach) {
		mScrollView = (CustomScrollView) findViewById(R.id.sc_scroll);
		mTitleView = (TitleView) findViewById(R.id.title);
		mContentView = (TextView) findViewById(R.id.content);
		mWait = (ProgressBar) findViewById(R.id.wait);
		mTitleView.setWriterIconVisible(false);
		mContentView.setOnClickListener(new DoubleClickListener());
		mZoom = (LinearLayout) findViewById(R.id.zoom);
		mZoomin = (ZoomButton) findViewById(R.id.zoomin);
		mZoomout = (ZoomButton) findViewById(R.id.zoomout);
		
		if(isAttach) {
			mWait.setVisibility(View.GONE);
			mTitleView.setTitleText(getIntent().getStringExtra("title"));
			mContentView.setText(getIntent().getStringExtra("content"));
		}
		
		initListeners();
	}

	private void initListeners() {
		mScrollView.setOnScrollChangedListener(new ScrollChangedListener());
		mZoomin.setOnClickListener(new ZoomClickListener());
		mZoomout.setOnClickListener(new ZoomClickListener());
		
		mTitleView.setOnTitleIconClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
	}
	
	private class ScrollChangedListener implements CustomScrollView.OnScrollChangedListener {
		@Override
		public void onScrollYChanged() {
			if(mZoom.getVisibility() == View.VISIBLE) {
				mZoom.setVisibility(View.GONE);
			}
		}
	}
	
	private class ZoomClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.zoomin:
				zoomIn();break;
			case R.id.zoomout:
				zoomOut();break;
			}
		}
	}
	
	// 缩小
	private void zoomOut() {
		if(mZoomHint > 0) {
			// 这里有问题
			mContentView.setTextSize(mTextSizes[--mZoomHint]);
			if(mZoomHint == 0) {
				mZoom.setVisibility(View.GONE);
			}
		}
	}
	
	// 放大
	private void zoomIn() {
		if(mZoomHint < mTextSizes.length-1) {
			mContentView.setTextSize(mTextSizes[++mZoomHint]);
		}
	}
	
	private class DoubleClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if(isContentPressed) {
				zoomIn();
				isContentPressed = false;
				mZoom.setVisibility(View.VISIBLE);
				return;
			}
			
			isContentPressed = true;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					isContentPressed = false;
				}
			}, 500);
		}
	}
	
	private class ArtistProcessListener implements ProcessArtist.Listener {
		@Override
		public void onResult(String title, String content) {
			mWait.setVisibility(View.GONE);
			if(!TextUtils.isEmpty(title)) {
				mTitleView.setTitleText(title);
			}
			
			if(!TextUtils.isEmpty(content)) {
				mContentView.setText(content);
				return;
			}
			
			mContentView.setText("拉取内容失败");
		}
	}
	
	public void back() {
		finish();
		overridePendingTransition(R.anim.translate_none, R.anim.translate_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
