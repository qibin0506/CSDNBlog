package org.loader.dashenblog;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;
import org.loader.dashenblog.adapter.LocalBlogAdapter;
import org.loader.dashenblog.customview.TitleView;
import org.loader.dashenblog.engine.CacheBlogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LocalBlogActivity extends Activity {
	private TitleView mTitleView;
	private ListView mListView;
	
	private LocalBlogAdapter mAdapter;
	private ArrayList<HashMap<String, String>> mData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.local_blog_layout);
		initViews();
	}

	private void initViews() {
		mTitleView = (TitleView) findViewById(R.id.title);
		mListView = (ListView) findViewById(R.id.result);
		
		mTitleView.setTitleText("本地缓存");
		mTitleView.setWriterIconVisible(false);
		mTitleView.setOnTitleIconClickListener(new TitleBackClickListener());
		
		mData = CacheBlogs.getCacheBlogs();
		mAdapter = new LocalBlogAdapter(this, mData);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
	}
	
	private class TitleBackClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			back();
		}
	}
	
	private void back() {
		finish();
		overridePendingTransition(R.anim.translate_none, R.anim.translate_out);
	}
	
	private class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String title = mData.get(position).get("title");
			String content = mData.get(position).get("content");
			Intent intent = new Intent(LocalBlogActivity.this, ArtistActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("content", content);
			startActivity(intent);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_none);
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
}
