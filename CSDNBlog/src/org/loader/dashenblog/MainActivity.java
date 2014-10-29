package org.loader.dashenblog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.loader.dashenblog.adapter.CategoryAdapter;
import org.loader.dashenblog.customview.MenuView;
import org.loader.dashenblog.customview.SlidingMenu;
import org.loader.dashenblog.customview.TitleView;
import org.loader.dashenblog.engine.CacheBlogs;
import org.loader.dashenblog.engine.ProcessBlogCategory;
import org.loader.dashenblog.service.BlogNotifyService;
import org.loader.dashenblog.utils.Comms;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SlidingMenu mSlidingMenu;
	private TitleView mTitle;
	private LinearLayout mSearchWrapper;
	private EditText mSearchContent;
	private ImageButton mSearchBtn;
	private ListView mListView;
	private ProgressBar mProgress;
	private MenuView mMenuView;
	
	private CategoryAdapter mAdapter;
	private ArrayList<HashMap<String, String>> mData = 
			new ArrayList<HashMap<String,String>>();
	
	private boolean canExit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
		mTitle = (TitleView) findViewById(R.id.title);
		mSearchWrapper = (LinearLayout) findViewById(R.id.search);
		mSearchContent = (EditText) findViewById(R.id.et_addr);
		mSearchBtn = (ImageButton) findViewById(R.id.btn_search);
		mListView = (ListView) findViewById(R.id.result);
		mProgress = (ProgressBar) findViewById(R.id.wait);
		mMenuView = (MenuView) findViewById(R.id.menu_view);
		
		mSearchContent.setSelectAllOnFocus(true);
		initListeners();
		
		mAdapter = new CategoryAdapter(this, mData);
		mListView.setAdapter(mAdapter);
		
		Toast.makeText(this, "点击填写按钮，填写CSDN网站ID开始", Toast.LENGTH_LONG).show();
	}

	private void initListeners() {
		mTitle.setOnTitleIconClickListener(new ShowMenuListener());
		mTitle.setOnTitleWriterClickListener(new ShowSearchPanelListener());
		mSearchBtn.setOnClickListener(new SearchBlogListener());
		mListView.setOnItemClickListener(new CategroyItemClickListener());
		
		mMenuView.setOnMenuItemClickListener(new MenuClickListener());
	}
	
	private void toggleSearchWrapper() {
		if(mSearchWrapper.getVisibility() == View.GONE) {
			mSearchWrapper.setVisibility(View.VISIBLE);
			ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchWrapper, "scaleX", 0.0f, 1.0f);
			anim.setDuration(500);
			anim.start();
		}else {
			ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchWrapper, "scaleX", 1.0f, 0.0f);
			anim.setDuration(500);
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mSearchWrapper.setVisibility(View.GONE);
				}
				@Override
				public void onAnimationCancel(Animator animation) {
					mSearchWrapper.setVisibility(View.GONE);
				}
			});
			anim.start();
		}
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		outState.putSerializable("bundle", mData);
//		super.onSaveInstanceState(outState);
//	}
	
	private class MenuClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.menu_concern:
				startActivityForResult(new Intent(MainActivity.this, ConcernActivity.class), 0);
				overridePendingTransition(R.anim.translate_in, R.anim.translate_none);
				break;
			case R.id.menu_local_blog:
				startActivity(new Intent(MainActivity.this, LocalBlogActivity.class));
				overridePendingTransition(R.anim.translate_in, R.anim.translate_none);
				break;
			case R.id.menu_clear:
				CacheBlogs.clearBlogCache();
				Toast.makeText(MainActivity.this, "缓存清理完毕", Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_blog_notify:
				startNotifyService();
				break;
			case R.id.menu_about:
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("使用帮助");
				builder.setMessage("本软件要求提供对应博主的csdn帐号\n例如：博客地址：http://blog.csdn.net/qibin0506对应的csdn帐号是qibin0506\n\n点击填写图标开始填写帐号\n\n可以将经常浏览的博客帐号添加到“我的关注”\n在“我的关注”中可直接查看该博主博客\n开启“博客提醒”后，可定期提醒关注博主的博客更新。\n\n额，打个小广告：应届生求职！！\n邮箱：qibin0506@gmail.com\n\n\n by 亓斌");
				builder.setPositiveButton("确定", null);
				builder.create().show();
				break;
			}
		}
	}
	
	private void startNotifyService() {
		final SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		int selected = sp.getInt("selected", 1);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("开启博客提醒");
		builder.setSingleChoiceItems(R.array.notify, selected, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sp.edit().putInt("selected", which).commit();
				PendingIntent pi = PendingIntent.getService(
						MainActivity.this, 1, new Intent(
								MainActivity.this,
								BlogNotifyService.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
		
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				if(0 == which) {
					// 8h 更新一次
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(), Comms.MAX_TIME, pi);
				}else {
					am.cancel(pi);
				}
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("取消", null);
		builder.create().show();
	}
	
	private class ShowMenuListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			mSlidingMenu.toggle();
		}
	}
	
	private class CategroyItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(MainActivity.this, ArtListActivity.class);
			intent.setAction(Comms.ACTION_LIST);
			intent.putExtra("name", mData.get(position).get("name").toString());
			intent.putExtra("url", mData.get(position).get("url").toString());
			startActivity(intent);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_none);
		}
	}
	
	private class SearchBlogListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String csdnId = mSearchContent.getText().toString().trim();
			if(TextUtils.isEmpty(csdnId)) {
				return;
			}
			
			mProgress.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			
			ProcessBlogCategory pb = new ProcessBlogCategory();
			pb.process(csdnId, new ProcessBlogCategory.Listener() {
				@Override
				public void onResult(String title, ArrayList<HashMap<String, String>> result) {
					mProgress.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					mData.clear();
				
					if(!TextUtils.isEmpty(title)) {
						mTitle.setTitleText(title);
					}
					
					if(null != result && !result.isEmpty()) {
						mData.addAll(result);
						mAdapter.notifyDataSetChanged();
					}else {
						mAdapter.notifyDataSetInvalidated();
						Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	private class ShowSearchPanelListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			toggleSearchWrapper();
		}
	}
	
	private void exit() {
		if(canExit) {
			finish();
			return;
		}
		
		canExit = true;
		Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				canExit = false;
			}
		}, 2000);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			exit();
			return true;
		}else if(KeyEvent.KEYCODE_MENU == event.getKeyCode()) {
			mSlidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(RESULT_CANCELED == resultCode) {
			return;
		}
		
		String csdnid = data.getStringExtra("csdnid");
		mSearchContent.setText(csdnid);
		mSearchBtn.performClick();
		mSlidingMenu.scrollToNormal();
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
