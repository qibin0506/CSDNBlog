package org.loader.dashenblog;

import java.util.ArrayList;
import java.util.HashMap;

import org.loader.dashenblog.R;
import org.loader.dashenblog.adapter.ConcernAdapter;
import org.loader.dashenblog.adapter.ConcernAdapter.OnDeleteListener;
import org.loader.dashenblog.customview.TitleView;
import org.loader.dashenblog.db.ConcernDB;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ConcernActivity extends Activity {
	private TitleView mTitleView;
	private LinearLayout mWrapper;
	private EditText mEditText;
	private ImageButton mAddBtn;
	private ListView mConcernListView;
	
	private ConcernAdapter mAdapter;
	private ArrayList<HashMap<String, String>> mData =
			new ArrayList<HashMap<String,String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.concern_layout);
		
		initViews();
	}

	private void initViews() {
		mTitleView = (TitleView) findViewById(R.id.title);
		mWrapper = (LinearLayout) findViewById(R.id.search);
		mEditText = (EditText) findViewById(R.id.et_addr);
		mAddBtn = (ImageButton) findViewById(R.id.btn_search);
		mConcernListView = (ListView) findViewById(R.id.result);
		
		mTitleView.setTitleText("我的关注");
		mTitleView.setOnTitleWriterClickListener(new TitleListener());
		mTitleView.setOnTitleIconClickListener(new TitleListener());
		
		mAddBtn.setImageResource(R.drawable.add);
		mAddBtn.setOnClickListener(new AddClickListener());
		
		mConcernListView.setOnItemClickListener(new ConcernItemClickListener());
		
		initAdapter();
	}
	
	private class ConcernItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			back(mData.get(position).get("csdnid"));
		}
	}

	private void initAdapter() {
		mAdapter = new ConcernAdapter(this, mData, new OnDeleteListener() {
			@Override
			public void onDelete(String id) {
				new ConcernDB(ConcernActivity.this).deleteById(id);
				initData();
			}
		});
		mConcernListView.setAdapter(mAdapter);
		initData();
	}
	
	
	private void initData() {
		mData.clear();
		mData.addAll(new ConcernDB(this).findAll());
		mAdapter.notifyDataSetChanged();
	}

	private void toggleWriteEdit() {
		if(mWrapper.getVisibility() == View.GONE) {
			mWrapper.setVisibility(View.VISIBLE);
			ObjectAnimator anim = ObjectAnimator.ofFloat(mWrapper, "scaleX", 0.0f, 1.0f);
			anim.setDuration(500);
			anim.start();
		}else {
			ObjectAnimator anim = ObjectAnimator.ofFloat(mWrapper, "scaleX", 1.0f, 0.0f);
			anim.setDuration(500);
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationCancel(Animator animation) {
					mWrapper.setVisibility(View.GONE);
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					mWrapper.setVisibility(View.GONE);
				}
			});
			anim.start();
		}
	}
	
	private class AddClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String csdnid = mEditText.getText().toString().trim();
			if(!TextUtils.isEmpty(csdnid)) {
				if(new ConcernDB(ConcernActivity.this).addConcern(csdnid)) {
					initData();
					Toast.makeText(ConcernActivity.this,"添加成功", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(ConcernActivity.this,"添加失败", 
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class TitleListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.iv_title_icon:
				back(null);
				break;
			case R.id.iv_title_writer:
				toggleWriteEdit();
				break;
			}
		}
	}
	
	private void back(String csdnid) {
		if(null != csdnid) {
			Intent intent = getIntent();
			intent.putExtra("csdnid", csdnid);
			setResult(RESULT_OK, intent);
		}else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
		overridePendingTransition(R.anim.translate_none, R.anim.translate_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			back(null);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
