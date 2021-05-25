package com.u8.server.sdk.i4;

import java.util.Map;




/**
 * 
 * @author Jonny
 *
 */
public class PayService{

	public static final String SIGNATURE = "sign";

	public static final String CHARSET = "utf-8";
	
    /**
     * 异步通知消息验证
     * @param para 异步通知消息
     * @return 验证结果
     */
    public static boolean verifySignature(Map<String, String> para, String publicKey) {
        try {
			String respSignature = para.get(SIGNATURE);
			// 除去数组中的空值和签名参数
			Map<String, String> filteredReq = PayCore.paraFilter(para);
			Map<String, String> signature = PayCore.parseSignature(respSignature, publicKey);
			for (String key : filteredReq.keySet()) {
			    String value = filteredReq.get(key);
			    String signValue = signature.get(key);
			    if (!value.equals(signValue)) {
			    	return false;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
	
}
	