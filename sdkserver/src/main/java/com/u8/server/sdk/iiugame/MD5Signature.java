package com.u8.server.sdk.iiugame;


import java.io.UnsupportedEncodingException;

import java.security.SignatureException;




public class MD5Signature{

	public static String sign(String content, String key) throws Exception {
		
		String tosign = (content == null ? "" : content) + key;
		System.out.println(tosign);
        try {
            return MD5.getMD5String(getContentBytes(tosign, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("MD5签名发生异常!", e);
        }
		
	}

	public static boolean verify(String content, String sign, String key)
			throws Exception {
		  String tosign = (content == null ? "" : content) + key;

	        try {
	            String mySign = MD5.getMD5String(getContentBytes(tosign, "utf-8"));
                System.out.println("mySign : "+mySign);
	            return equals(mySign, sign) ? true : false;
	        } catch (UnsupportedEncodingException e) {
	            throw new SignatureException("MD5验证签名发生异常!", e);
	        }
	}
	
	public static boolean verify(String content, String sign, String key,String charset) throws Exception {
	    String tosign = (content == null ? "" : content) + key;
	    try {
	        String mySign = MD5.getMD5String(getContentBytes(tosign, charset));
	
	        return equals(mySign, sign) ? true : false;
	    } catch (UnsupportedEncodingException e) {
	        throw new SignatureException("MD5验证签名发生异常!", e);
	    }
	}
	
	 /**
     * @param content
     * @param charset
     * @return
     * @throws java.security.SignatureException
     * @throws java.io.UnsupportedEncodingException
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
    

}