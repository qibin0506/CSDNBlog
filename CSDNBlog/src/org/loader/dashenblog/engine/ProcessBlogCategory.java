package org.loader.dashenblog.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.loader.dashenblog.utils.Comms;
import org.loader.dashenblog.utils.HttpUtils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * 获取该用户的博客分类
 * @author qibin
 */
@SuppressWarnings("unchecked")
@SuppressLint("HandlerLeak")
public class ProcessBlogCategory {
	private static ExecutorService sThreadPool = Executors.newSingleThreadExecutor();
	
	public void process(final String csdnId, final Listener l) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Object[] attach = (Object[]) msg.obj;
				l.onResult(attach[0].toString(), (ArrayList<HashMap<String, String>>)attach[1]);
			}
		};
		
		sThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils http = new HttpUtils();
				String result = http.get(Comms.BLOG + csdnId);
				
				ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String,String>>();
				String title = "";
				if(!TextUtils.isEmpty(result)) {
					title = parseResult(result, res);
				}
				Object[] msgAttach = new Object[] {title, res};
				Message msg = handler.obtainMessage(0, msgAttach);
				msg.sendToTarget();
			}
		});
	}
	
	/**
	 * @param result 该页的html源码
	 * @return 博客的title
	 */
	private String parseResult(String data, ArrayList<HashMap<String, String>> result) {
		if(null == data) {
			return null;
		}
		
		String title = "";
		
		try {
			// 解析文档
			Document doc = Jsoup.parse(data);
			try {
				Element titleElement = doc.select("#blog_title a").get(0);
				title = titleElement.text();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			// 获取id是panel_Category的文档
			// 通过观察源码 只要子文档是“文章分类”的ok
			Elements elems = doc.select("#panel_Category");
			Element headPanel = null;
			Element bodyPanel = null;
			for(Element e : elems) {
				headPanel = e.child(0);
				if(headPanel.text().equals("文章分类")) {
					bodyPanel = e.select(".panel_body").get(0);
					break;
				}
			}
			
			if(null == bodyPanel) {
				return title;
			}
			
			// 获取该文档下的所有a标记
			Elements links = bodyPanel.select("a[href]");
			HashMap<String, String> temp;
			Element count;
			// 循环a标记
			for(Element e : links) {
				count = (Element) e.nextSibling();
				temp = new HashMap<String, String>();
				temp.put("name", e.text());      //获取文本 <a href="">text</a>
				temp.put("url", e.attr("href")); //获取网址 <a href="url">text</a>
				temp.put("count", count.text());
				result.add(temp);
			}
		} catch (Exception e) {
			result.clear();
		}
		
		return title;
	}
	
	public interface Listener {
		public void onResult(String title, ArrayList<HashMap<String, String>> result);
	}
}
