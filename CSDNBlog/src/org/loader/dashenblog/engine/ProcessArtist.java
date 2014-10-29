package org.loader.dashenblog.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.loader.dashenblog.utils.Comms;
import org.loader.dashenblog.utils.Encrypt;
import org.loader.dashenblog.utils.HttpUtils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class ProcessArtist {
	private static ExecutorService sThreadPool = Executors.newSingleThreadExecutor();
	
	public void process(final String url, final Listener l) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String[] blog = (String[]) msg.obj;
				l.onResult(blog[0], blog[1]);
			}
		};
		
		sThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				String[] blog = {null, null};
				
				if(TextUtils.isEmpty(url)) {
					Message msg = handler.obtainMessage(0, blog);
					msg.sendToTarget();
					return;
				}
				
				String reqUrl = Comms.BLOG.substring(0, Comms.BLOG.length()-1) + url;
				
				// 如果不存在本地缓存
				if(!CacheBlogs.getCacheByKey(Encrypt.md5(url), blog)) {
					// 不存在
					HttpUtils http = new HttpUtils();
					String html = http.get(reqUrl);
					if(!TextUtils.isEmpty(html)) {
						Document doc = Jsoup.parse(html);
						parse(doc, blog);
						// 如果标题和内容都不为空
						if(!TextUtils.isEmpty(blog[0]) && !TextUtils.isEmpty(blog[1])) {
							CacheBlogs.cacheBlog(Encrypt.md5(url), blog[0], blog[1]);
						}
					}
				}
				
				Message msg = handler.obtainMessage(0, blog);
				msg.sendToTarget();
			}
		});
	}
	
	private void parse(Document doc, String[] blog) {
		try {
			Element titleElement = doc.select(".link_title").get(0);
			blog[0] = titleElement.text();
		} catch (Exception e) {
			blog[0] = null;
		}
		
		try {
			Element contentElement = doc.select("#article_content").get(0);
			
			Elements contents = contentElement.children();
			StringBuilder sb = new StringBuilder();
			for(Element e : contents) {
				sb.append(e.text() + "\n");
			}
			
			blog[1] = sb.toString();
		} catch (Exception e) {
			blog[1] = null;
			e.printStackTrace();
		}
	}
	
	public interface Listener {
		public void onResult(String title, String content);
	}
}
