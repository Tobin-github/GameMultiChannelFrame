package com.u8.server.sdk.i4;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;

/**
 * 
 * @author Jonny
 *
 */
public class PayCore {
	
	/** = */
	public static final String QSTRING_EQUAL = "=";

	/** & */
	public static final String QSTRING_SPLIT = "&";
	
    /** 
     * 除去请求要素中的空值和签名参数
     * @param para 请求要素
     * @return 去掉空值与签名参数后的请求要素
     */
    public static Map<String, String> paraFilter(Map<String, String> para) {
        Map<String, String> result = new HashMap<String, String>();
        if (para == null || para.size() <= 0) {
            return result;
        }
        for (String key : para.keySet()) {
            String value = para.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase(PayService.SIGNATURE)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str 需要解析的字符串
	 * @return 解析的结果map
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str) {
		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
			curChar = str.charAt(i);// 取当前字符
			if (curChar == '&') {// 如果读取到&分割符
				putKeyValueToMap(temp, isKey, key, map);
				temp.setLength(0);
				isKey = true;
			} else {
				if (isKey) {// 如果当前生成的是key
					if (curChar == '=') {// 如果读取到=分隔符
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else {// 如果当前生成的是value
					temp.append(curChar);
				}
			}
		}
		putKeyValueToMap(temp, isKey, key, map);
		return map;
	}
	
	/**
     * 生成签名
     * @return 解析签名的结果map
	 * @throws Exception 
     */
    public static Map<String, String> parseSignature(String sign, String publicKey) throws Exception {
		BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] dcDataStr = base64Decoder.decodeBuffer(sign);
        byte[] plainData = RSADecrypt.decryptByPublicKey(dcDataStr, publicKey);
        String parseString = new String(plainData);
        System.out.println("parseString=" + parseString);
		return parseQString(parseString);
    }
	
	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map) {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}
    
}
