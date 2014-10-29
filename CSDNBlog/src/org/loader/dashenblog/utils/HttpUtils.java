package org.loader.dashenblog.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public String get(String url) {
		DefaultHttpClient client = null;
		String result = null;
		try {
			client = new DefaultHttpClient();
			client.setParams(getParams());
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if(200 == response.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != client) {
				client.getConnectionManager().shutdown();
			}
		}
		
		return result;
	}
	
	public HttpParams getParams() {
		HttpParams params = new BasicHttpParams();
		
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams
				.setUserAgent(
						params,
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2188.2 Safari/537.36");
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		
		ConnManagerParams.setTimeout(params, 10000);
		
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 10000);
		
		return params;
	}
}
