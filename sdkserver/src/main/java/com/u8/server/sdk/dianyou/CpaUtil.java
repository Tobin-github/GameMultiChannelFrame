package com.u8.server.sdk.dianyou;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CpaUtil {
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String,String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	
	/**
	 * 验证签名是否有效
	 * @param params	请求过来的参数
	 * @param securityKey	SDK密钥
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(Map<String,String> params,String securityKey) throws Exception{
		boolean ret = false;
		String sign = params.get("sign");
		if(sign!=null){
			params.remove("sign");
			String link = createLinkString(params);
			String signStr = "app=A001&cbi=&ct=1512030903859&fee=120&pt=1512030894000&sdk=0001&ssid=1368259418092732417&st=1&tcd=IWD12007890000002131173044332&uid=1200789&ver=1ce196a7129934e28726f40c8a1f0644fb3243fa35f165f20";
			String hexSting = getHexSting(md5(signStr));
			if(sign.equals(getHexSting(md5(link+securityKey)))){
				ret = true;
			}
		}
		return ret;
	}
	
	public static String md5(String text) throws Exception {
		byte[] bytes = text.getBytes("UTF-8");
		
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		bytes = messageDigest.digest();
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bytes.length; i ++)
		{
			if((bytes[i] & 0xff) < 0x10)
			{
				sb.append("0");
			}

			sb.append(Long.toString(bytes[i] & 0xff, 16));
		}
		
		return sb.toString().toLowerCase();
	}
	
	/**
	 * 取十六进制子串
	 * @param str
	 * @return
	 */
	public static String getHexSting(String str){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			sb.append(Integer.toHexString(str.charAt(i)));
		}
		return sb.toString();
	}
	
	//http请求参数模拟
	public static void main(String[] args) throws Exception {
		Map<String,String> requestParams = new HashMap<String,String>();
		requestParams.put("app","A001");
		requestParams.put("sdk","0001");
		requestParams.put("cbi","");
		requestParams.put("ct","1512030903859");
		requestParams.put("fee","120");
		requestParams.put("pt","1512030894000");
		requestParams.put("ssid","1368259418092732417");
		requestParams.put("st","1");
		requestParams.put("tcd","IWD12007890000002131173044332");
		requestParams.put("uid","1200789");
		requestParams.put("ver","1");
		requestParams.put("sign","6432383031643564313961303766353034326566623363666665326266333432");
		
		String key = "MDAwMDAwMDA1NjgzODFmODAxNTY4YzA1MGQzNTAwMGM=";
		if(verify(requestParams,key)){
			System.out.println("验证成功");
		}else{
			System.out.println("验证失败");
		}
	}
	
}
