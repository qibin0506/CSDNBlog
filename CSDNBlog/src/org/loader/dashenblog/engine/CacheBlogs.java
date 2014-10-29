package org.loader.dashenblog.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

public class CacheBlogs {
	private static final String TAG_TITLE = "title";
	private static final String TAG_CONTENT = "content";
	
	private static String sCacheDir;
	
	private static ReadWriteLock sLock = new ReentrantReadWriteLock();
	
	static {
		if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
			sCacheDir = Environment.getExternalStorageDirectory() + 
					File.separator + "csdn" + File.separator + 
					"cache" + File.separator;
			
			if(!new File(sCacheDir).exists() 
					&& !new File(sCacheDir).mkdirs()) {
				sCacheDir = null;
			}
		}
	}
	
	public static ArrayList<HashMap<String, String>> getCacheBlogs() {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
		if(null == sCacheDir) {
			return result;
		}
		
		HashMap<String, String> temp;
		File[] caches = new File(sCacheDir).listFiles();
		for(File f : caches) {
			temp = new HashMap<String, String>();
			String[] res = {null, null};
			try {
				parseXml(f, res);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			temp.put("title", res[0]);
			temp.put("content", res[1]);
			result.add(temp);
		}
		return result;
	}
	
	// 清理缓存
	public static void clearBlogCache() {
		if(null == sCacheDir) {
			return;
		}
		
		sLock.writeLock().lock();
		try {
			File[] caches = new File(sCacheDir).listFiles();
			for(File f : caches) {
				f.delete();
			}
		} catch (Exception e) {
		} finally {
			sLock.writeLock().unlock();
		}
	}
	
	// 缓存博客
	public static void cacheBlog(String key, String title, String content) {
		if(null == sCacheDir) {
			return;
		}
		
		sLock.writeLock().lock();
		try {
			OutputStream os = new FileOutputStream(sCacheDir + key + ".xml");
			XmlSerializer serializer = Xml.newSerializer();
			
			serializer.setOutput(os, "utf-8");
			serializer.startDocument("utf-8", true);
			
			serializer.startTag(null, TAG_TITLE);
			serializer.text(title);
			serializer.endTag(null, TAG_TITLE);
			
			serializer.startTag(null, TAG_CONTENT);
			serializer.text(content);
			serializer.endTag(null, TAG_CONTENT);
			
			serializer.endDocument();
			serializer.flush();
			os.close();
		} catch (Exception e) {
		} finally {
			sLock.writeLock().unlock();
		}
	}
	
	// 获取一条缓存的博客
	public static boolean getCacheByKey(String key, String[] result) {
		if(null == sCacheDir) {
			return false;
		}
		
		sLock.readLock().lock();
		boolean success = false;
		
		try {
			File file = new File(sCacheDir + key + ".xml");
			if(!file.exists()) {
				throw new Exception();
			}
			parseXml(file, result);
			success = true;
		} catch (Exception e) {
			success = false;
		} finally {
			sLock.readLock().unlock();
		}
		
		return success;
	}
	
	private static void parseXml(File file, String[] result) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		
		InputStream in = new FileInputStream(file);
		parser.setInput(in, "utf-8");
		int type = parser.getEventType();
		String tagName = null;
		
		while(type != XmlPullParser.END_DOCUMENT) {
			if(type == XmlPullParser.START_TAG) {
				tagName = parser.getName();
			}else if(type == XmlPullParser.TEXT) {
				if(tagName.equals(TAG_TITLE)) {
					result[0] = parser.getText().trim();
				}else if(tagName.equals(TAG_CONTENT)) {
					result[1] = parser.getText();
				}
			}
			
			type = parser.next();
		}
	}
}
