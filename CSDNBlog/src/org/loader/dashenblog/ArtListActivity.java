package org.loader.dashenblog;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;
import org.loader.dashenblog.adapter.ArtListAdapter;
import org.loader.dashenblog.customview.CustomListView;
import org.loader.dashenblog.customview.TitleView;
import org.loader.dashenblog.engine.ProcessArtList;
import org.loader.dashenblog.utils.Comms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

public class ArtListActivity extends Activity {
	private TitleView mTitleView;
	private CustomListView mListView;
	private ProgressBar mWait;

	private int mNowPage;
	private int mPageCount;
	private int mLastItem;
	
	private boolean isLoading = true;
	
	private String mUrl;
	
	private ProcessArtList mProcess;
	private ProcessArtListListener mListener;
	
	private ArtListAdapter mAdapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String,String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.art_list_layout);
		
		Intent it = getIntent();
		if(it.getAction().equals(Comms.ACTION_NEW)) {
			mData.clear();
			@SuppressWarnings("unchecked")
			ArrayList<HashMap<String, String>> data = 
					(ArrayList<HashMap<String, String>>) it.getSerializableExtra("data");
			mData.addAll(data);
			initViewsWidthData();
		}else {
			String title = it.getStringExtra("name");
			mUrl = it.getStringExtra("url");
			
			mListener = new ProcessArtListListener();
			mProcess = new ProcessArtList();
			mProcess.process(mUrl, mListener);
		
			initViewsWidthNoData(title);
		}
	}
	
	private void initViews(String title) {
		mTitleView = (TitleView) findViewById(R.id.title);
		mListView = (CustomListView) findViewById(R.id.result);
		mWait = (ProgressBar) findViewById(R.id.wait);
		mTitleView.setTitleText(title);
		mTitleView.setWriterIconVisible(false);
		
		mTitleView.setOnTitleIconClickListener(new TitleIconClickListener());
		mListView.setOnItemClickListener(new ArtListItemClickListener());
		
		mAdapter = new ArtListAdapter(this, mData);
		mListView.setAdapter(mAdapter);
	}
	
	private void initViewsWidthData() {
		String title = "博客更新";
		initViews(title);
		showDataList();
	}

	private void initViewsWidthNoData(String title) {
		initViews(title);
		mListView.setOnScrollListener(new ScrollListener());
	}
	
	private void showDataList() {
		isLoading = false;
		mListView.showFooterView(true);
		mListView.setFooterText(R.string.complete);
		
		mWait.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}
	
	private class ArtListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(ArtListActivity.this, ArtistActivity.class);
			intent.putExtra("url", mData.get(position).get("url"));
			startActivity(intent);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_none);
		}
	}
	
	private class ScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == OnScrollListener.SCROLL_STATE_IDLE 
					&& mLastItem == mData.size()
					&& !isLoading
					&& mPageCount > mNowPage) {
				mProcess.process(mUrl + "/" + (++mNowPage), mListener);
				
				isLoading = true;
				mListView.showFooterView(true);
				mListView.setFooterText(R.string.loading);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mLastItem = firstVisibleItem + visibleItemCount - 1;
		}
	}
	
	private class ProcessArtListListener implements ProcessArtList.Listener {
		@Override
		public void onResult(ArrayList<HashMap<String, String>> result,
				int nowPage, int pageCount) {
			showDataList();
			
			mNowPage = nowPage;
			mPageCount = pageCount;
			if(null != result && !result.isEmpty()) {
				mData.addAll(result);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private class TitleIconClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			back();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void back() {
		finish();
		overridePendingTransition(R.anim.translate_none, R.anim.translate_out);
	}
}
