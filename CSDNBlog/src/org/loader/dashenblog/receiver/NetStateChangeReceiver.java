package org.loader.dashenblog.receiver;

import org.loader.dashenblog.service.BlogNotifyService;
import org.loader.dashenblog.utils.NetUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetStateChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		int selected = context.getSharedPreferences("config",
				Context.MODE_PRIVATE).getInt("selected", 1);
		
		if(0 == selected && NetUtils.isNetworkConnected(context)) {
			context.startService(new Intent(context, BlogNotifyService.class));
		}
	}
}
