package com.u8.server.sdk.nubia;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;


public class MD5Util{

	public static String sign(String content, String key) throws Exception {
		
		String tosign = (content == null ? "" : content) + key;
		
        try {
            return DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("MD5签名发生异常!", e);
        }
		
	}

	public static boolean verify(String content, String sign, String key)
			throws Exception {
		  String tosign = (content == null ? "" : content) + key;

	        try {
	            String mySign = DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));

	            return equals(mySign, sign) ? true : false;
	        } catch (UnsupportedEncodingException e) {
	            throw new SignatureException("MD5验证签名发生异常!", e);
	        }
	}
	
	public static boolean verify(String content, String sign, String key,String charset) throws Exception {
	    String tosign = (content == null ? "" : content) + key;
	    try {
	        String mySign = DigestUtils.md5Hex(getContentBytes(tosign, charset));
	
	        return equals(mySign, sign) ? true : false;
	    } catch (UnsupportedEncodingException e) {
	        throw new SignatureException("MD5验证签名发生异常!", e);
	    }
	}
	
	 /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException 
     */
    protected static byte[] getContentBytes(String content, String charset)
             throws UnsupportedEncodingException {
        if (isEmpty(charset)) {
            return content.getBytes();
        }

        return content.getBytes(charset);
    }
    
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }
    
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }
    
    public static void main(String[] args) {
    	try {
    		
    		Map<String, String> parameterMap = new HashMap<String, String>();
    		parameterMap.put("app_id", 151015+"");
    		parameterMap.put("uid", 150+"");
    		parameterMap.put("cp_order_id", "nb1451635941847");
    		parameterMap.put("amount", "1.00");
    		parameterMap.put("product_name", "牛币");
    		parameterMap.put("product_des", "牛币可以交易");
    		parameterMap.put("number", 1+"");
    		parameterMap.put("data_timestamp", "1458824465");
    		String verifyData = ParameterUtil.getSignData(parameterMap);

    		String appId = "151015";//开发者平台获取的appId
    		String secret_key = "e30c8e49063140xxxxxxxxxxxxxx";   //开发者平台获取的secret_key
			System.out.println(verifyData);
			String signk = MD5Util.sign(verifyData,":"+appId+":" +secret_key);
			System.out.println(signk);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}