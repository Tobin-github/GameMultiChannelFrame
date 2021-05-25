/**
 * 工具类
 */

package com.u8.server.sdk.qihoo360;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

public class Util {

	/**
	 * http请求
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	public static String requestUrl(String url, HashMap<String, String> data)
			throws IOException {

		HttpURLConnection conn;
		try {
			//if GET....
			//URL requestUrl = new URL(url + "?" + httpBuildQuery(data));
			URL requestUrl = new URL(url);
			conn = (HttpURLConnection) requestUrl.openConnection();
		} catch (MalformedURLException e) {
			return e.getMessage();
		}

		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		PrintWriter writer = new PrintWriter(conn.getOutputStream());
		writer.print(httpBuildQuery(data));
		writer.flush();
		writer.close();

		String line;
		BufferedReader bufferedReader;
		StringBuilder sb = new StringBuilder();
		InputStreamReader streamReader = null;
		try {
			streamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
		} catch (IOException e) {
			/*
			Boolean ret2 = true;
			if (ret2) {
				return e.getMessage();
			}
			*/
			streamReader = new InputStreamReader(conn.getErrorStream(), "UTF-8");
		} finally {
			if (streamReader != null) {
				bufferedReader = new BufferedReader(streamReader);
				sb = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 参数编码qi
	 * @param data
	 * @return 
	 */
	public static String httpBuildQuery(HashMap<String, String> data) {
		String ret = "";
		String k, v;
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			k = iterator.next();
			v = data.get(k);
			try {
				ret += URLEncoder.encode(k, "utf8") + "=" + URLEncoder.encode(v, "utf8");
			} catch (UnsupportedEncodingException e) {
			}
			ret += "&";
		}
		return ret.substring(0, ret.length() - 1);
	}

	/**
	 * 签名计算
	 * @param params
	 * @param appSecret
	 * @return 
	 */
	public static String getSign(HashMap params, String appSecret) {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		String k, v;

		String str = "";
		for (int i = 0; i < keys.length; i++) {
			k = (String) keys[i];
			if (k.equals("sign") || k.equals("sign_return")) {
				continue;
			}
            if(params.get(k)==null){
                continue;
            }
			v = (String) params.get(k);

			if (v.equals("0") || v.equals("")) {
				continue;
			}
			str += v + "#";
		}
		return Util.md5(str + appSecret);
	}

	public static String md5(String str) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes("UTF8"));
			byte bytes[] = m.digest();

			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xff) < 0x10) {
					sb.append("0");
				}
				sb.append(Long.toString(bytes[i] & 0xff, 16));
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
}
