package org.loader.dashenblog.engine;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.loader.dashenblog.utils.Comms;
import org.loader.dashenblog.utils.HttpUtils;

public class NotifyBlog {
	
	public void get(long time, String csdnid, ArrayList<HashMap<String, String>> returnResult) {
		try {
			HttpUtils http = new HttpUtils();
			String html = http.get(Comms.BLOG + csdnid);
			if(null != html) {
				parse(time, html, returnResult);
			}
		} catch (Exception e) {
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void parse(long time, String html, ArrayList<HashMap<String, String>> res) {
		try {
			Document doc = Jsoup.parse(html);
			
			Elements titles = doc.select(".article_title"); // titles
			Elements description = doc.select(".article_description"); // 描述
			Elements postDate = doc.select(".link_postdate"); // 发表时间
		
			Element titleTemp;
			HashMap<String, String> temp;
			
			for(int i=0;i<postDate.size();i++) {
				String date = postDate.get(i).text().trim();
				if(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date).getTime() >= time) {
					temp = new HashMap<String, String>();
					titleTemp = titles.get(i).select(".link_title").get(0);
				
					temp.put("title", titleTemp.text());
					temp.put("url", titleTemp.select("a[href]").get(0).attr("href"));
					temp.put("des", description.get(i).text());
					temp.put("date", postDate.get(i).text());
					res.add(temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
