package org.loader.dashenblog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.loader.dashenblog.ArtListActivity;
import org.loader.dashenblog.R;
import org.loader.dashenblog.db.ConcernDB;
import org.loader.dashenblog.engine.NotifyBlog;
import org.loader.dashenblog.utils.Comms;
import org.loader.dashenblog.utils.NetUtils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class BlogNotifyService extends IntentService {
	public BlogNotifyService() {
		super("BlogNotifyService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 这是在线程中执行的
		// 当前时间与csdn博客发表的时间对比
		// 注意“置顶博客”
		if(!NetUtils.isNetworkConnected(this)) {
			return;
		}
		
		long lastTime = getSharedPreferences("config", Context.MODE_PRIVATE)
				.getLong("lasttime", 0);
		
		// 如果当前时间-最后更新时间小于最大期限
		if(System.currentTimeMillis() / 100000 * 100000 - lastTime < Comms.MAX_TIME) {
			return;
		}
		
		// 走到这一步的可能性 1： 定时器到时， 2： 网络连接状态改变
		// 条件1： 网络是连接状态
		// 条件2： 现在的时间-最后一次更新的时间  > MAX_TIME
		final ArrayList<HashMap<String, String>> res = new ConcernDB(this).findAll();
		final ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
	
		for(Iterator<HashMap<String, String>> iter=res.iterator();iter.hasNext();) {
			String csdnid = iter.next().get("csdnid");
			new NotifyBlog().get(lastTime, csdnid, result);
		}
		
		getSharedPreferences("config", Context.MODE_PRIVATE).edit()
		.putLong("lasttime", System.currentTimeMillis() / 100000 * 100000).commit();
		
		setAlarm();
		// 如果检测到更新
		if(result.size() > 0) {
			notification(result);
		}
	}

	private void setAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getService(
				this, 1, new Intent(this, BlogNotifyService.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + Comms.MAX_TIME,Comms.MAX_TIME, pi);
	}
	
	private void notification(ArrayList<HashMap<String, String>> result) {
		String text = "你关注的博客有" + result.size() + "篇更新";
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.ic_launcher, text,System.currentTimeMillis());
		
		Intent intent = new Intent(this, ArtListActivity.class);
		intent.setAction(Comms.ACTION_NEW);
		intent.putExtra("data", result);
		
		PendingIntent pi = PendingIntent.getActivity(this, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.setLatestEventInfo(this, text, text + ",点击查看", pi);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND;
		
		nm.notify(getClass().getName(), R.drawable.ic_launcher, notification);
	}
}
