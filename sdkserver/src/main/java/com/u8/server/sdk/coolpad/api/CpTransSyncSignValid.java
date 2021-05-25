package com.u8.server.sdk.coolpad.api;

import java.math.BigInteger;

public final class CpTransSyncSignValid {

	/**
	 * Desc:生成签名
	 * 
	 * @param transdata
	 *            需要加密的数据，如{"appid":"1","exorderno":"2"}
	 * @param key
	 *            应用的密钥(商户可从商户自服务系统获取)
	 * @return 签名
	 */
	public static String genSign(String transdata, String key) {
		String sign = "";
		try {
			// 获取privatekey和modkey
			String decodeBaseStr = Base64.decode(key);

			String[] decodeBaseVec = decodeBaseStr.replace('+', '#').split("#");

			String privateKey = decodeBaseVec[0];
			String modkey = decodeBaseVec[1];

			// 生成sign的规则是先md5,再rsa
			String md5Str = MD5.md5Digest(transdata);

			sign = RSAUtil.encrypt(md5Str, new BigInteger(privateKey),
					new BigInteger(modkey));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;

	}

	/**
	 * Desc:cp交易同步签名验证
	 * 
	 * @param transdata
	 *            同步过来的transdata数据
	 * @param sign
	 *            同步过来的sign数据
	 * @param key
	 *            应用的密钥(商户可从商户自服务系统获取)
	 * @return 验证签名结果 true:验证通过 false:验证失败
	 */
	public static boolean validSign(String transdata, String sign, String key) {
		try {
			String md5Str = MD5.md5Digest(transdata);

			String decodeBaseStr = Base64.decode(key);

			String[] decodeBaseVec = decodeBaseStr.replace('+', '#').split("#");

			String privateKey = decodeBaseVec[0];
			String modkey = decodeBaseVec[1];

			String reqMd5 = RSAUtil.decrypt(sign, new BigInteger(privateKey),
					new BigInteger(modkey));

			if (md5Str.equals(reqMd5)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public static void main(String[] args) {
		
		String reqJson = "{\"exorderno\":\"iVk4eRZknftx4vAJm5VE\",\"transid\":\"02115061814204200016\",\"waresid\":1,\"appid\":\"3000962200\",\"feetype\":0,\"money\":1,\"count\":1,\"result\":0,\"transtype\":0,\"transtime\":\"2015-06-18 14:20:59\",\"cpprivate\":\"cp private info!!\",\"paytype\":401}";
		String sign = "56b10877c6ecf3fa3c4805ca8b6f26a8 5fd39828d76b54faf8a034e4d509150b 2519141767960a2e1bfd27b04dbcc8b2";
		String appkey = "RkIwNTlFM0Y5RTEzNTA5NDcxNEMxMkY1OTREQUQxM0VFNEEwRTI2N01UZ3hNamd6T0RRek1ERTVORGd4T0RreU9Ua3JNVGsxTlRBME5EQXlNakF5TmpRM056RTVPRE13TkRZNE5ESTJOekUxTWpVMk5EUXdOREEz";
		
		System.out.println(CpTransSyncSignValid.validSign(reqJson, sign, appkey));

	}

}
