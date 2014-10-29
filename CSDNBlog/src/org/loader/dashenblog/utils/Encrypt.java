package org.loader.dashenblog.utils;

public class Encrypt {
	// md5加密
	public static String md5(String str) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5"); // 得到实例
			md.reset(); // 重置
			md.update(str.getBytes("UTF-8")); // 使用指定的 byte 数组更新摘要
			byte[] hash = md.digest(); // 使用指定的 byte 数组对摘要进行最后更新，然后完成摘要计算
			int len = hash.length;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len; i++) {
				if (1 == Integer.toHexString(0xFF & hash[i]).length()) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(0xFF & hash[i]));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
