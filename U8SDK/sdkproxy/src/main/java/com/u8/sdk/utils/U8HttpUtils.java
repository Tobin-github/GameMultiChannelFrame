package com.u8.sdk.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

public class U8HttpUtils {
	
	private final static String PARAMETER_SEPARATOR = "&";
	private final static String NAME_VALUE_SEPARATOR = "=";
		
	/**
	 * Http GET 请求
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static String httpGet(String urlStr, Map<String, String> params) {
		
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
        	
    		String paramsEncoded = "";
    		if(params != null){
    			paramsEncoded = urlParamsFormat(params, "UTF-8");
    		}
    		
    		String fullUrl = urlStr + "?" + paramsEncoded;
    		
    		Log.d("U8SDK", "the fullUrl is "+fullUrl);
        	
            url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            
            if(connection.getResponseCode() == 200){
            	
                in = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuffer strBuffer = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuffer.append(line);
                }
                result = strBuffer.toString();
            }else{
            	Log.e("U8SDK", "get connection failed. code:"+connection.getResponseCode() + ";url:"+fullUrl);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return result;
    }
	
	/**
	 * Http POST 请求
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static String httpPost(String urlStr, Map<String, String> params) {
				
		
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
        	
    		String paramsEncoded = "";
    		if(params != null){
    			paramsEncoded = urlParamsFormat(params, "UTF-8");
    		}        	
        	
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            DataOutputStream dop = new DataOutputStream(connection.getOutputStream());
            dop.writeBytes(paramsEncoded);
            dop.flush();
            dop.close();
 
            if(connection.getResponseCode() == 200){
                in = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuffer strBuffer = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuffer.append(line);
                }
                result = strBuffer.toString();
            }else{
            	Log.e("U8SDK", "post connection failed. code:"+connection.getResponseCode() + ";url:"+urlStr);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return result;
    }
	


	
	/**
	 * Returns a String that is suitable for use as an application/x-www-form-urlencoded
     * list of parameters in an HTTP PUT or HTTP POST.
	 * @param params
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlParamsFormat(Map<String, String> params, String charset) throws UnsupportedEncodingException{
		final StringBuilder sb = new StringBuilder();
		for(String key : params.keySet()){
			final String val = params.get(key);
			if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(val)){
				final String encodedName = URLEncoder.encode(key, charset);
				final String encodedValue = URLEncoder.encode(val, charset);
				if(sb.length() > 0){
					sb.append(PARAMETER_SEPARATOR);
				}
				sb.append(encodedName).append(NAME_VALUE_SEPARATOR)
				.append(encodedValue);
			}

			
		}
		
		return sb.toString();
	}
	
}
