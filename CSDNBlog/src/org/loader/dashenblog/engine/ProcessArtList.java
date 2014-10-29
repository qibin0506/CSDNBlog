package org.loader.dashenblog.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.loader.dashenblog.utils.HttpUtils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * 获取分类中的文章列表
 * @author qibin
 */
@SuppressWarnings("unchecked")
public class ProcessArtList {
	private static ExecutorService sThreadPool = Executors.newFixedThreadPool(1);
	
	public void process(final String url, final Listener l) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				l.onResult((ArrayList<HashMap<String, String>>)msg.obj, msg.arg1, msg.arg2);
			}
		};
		
		sThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils http = new HttpUtils();
				String result = http.get(url);
				
				// 存放结果
				ArrayList<HashMap<String, String>> res = 
						new ArrayList<HashMap<String,String>>();
				// 存放页码信息
				int[] pageInfo = new int[] {1, 1};
				
				if(!TextUtils.isEmpty(result)) {
					Document doc = Jsoup.parse(result);
					parseResult(doc, res);  // 解析结果
					getPageInfo(doc, pageInfo); // 解析页码信息
				}
				
				Message msg = handler.obtainMessage(0, res);
				msg.arg1 = pageInfo[0];
				msg.arg2 = pageInfo[1];
				msg.sendToTarget();
			}
		});
	}
	
	// 0->nowpage  1->pageCount 
	private void getPageInfo(Document doc, int[] pageInfo) {
		try {
			Element pageElement = doc.select("#papelist").get(0);
			int nowPage = Integer.parseInt(pageElement.select("strong").get(0).text());
			
			String pageCountStr = pageElement.select("span").get(0).text();
			pageCountStr = pageCountStr.replaceAll("\\s*.*\\s*共", "");
			int pageCount = Integer.parseInt(pageCountStr.replaceAll("页", ""));
			
			pageInfo[0] = nowPage;
			pageInfo[1] = pageCount;
		} catch (Exception e) {
			pageInfo[0] = 1;
			pageInfo[1] = 1;
		}
	}
	
	private void parseResult(Document doc, ArrayList<HashMap<String, String>> res) {
		try {
			Elements titles = doc.select(".article_title"); // titles
			Elements description = doc.select(".article_description"); // 描述
			Elements postDate = doc.select(".link_postdate"); // 发表时间
			
			// title url des data
			Element titleTemp;
			HashMap<String, String> temp;
			for(int i=0;i<titles.size();i++) {
				titleTemp = titles.get(i).select(".link_title").get(0);
				temp = new HashMap<String, String>();
				temp.put("title", titleTemp.text());
				temp.put("url", titleTemp.select("a[href]").get(0).attr("href"));
				temp.put("des", description.get(i).text());
				temp.put("date", postDate.get(i).text());
				res.add(temp);
			}
		} catch (Exception e) {
			res.clear();
		}
	}
	
	public interface Listener  {
		public void onResult(ArrayList<HashMap<String, String>> result, int nowPage, int pageCount);
	}
}
