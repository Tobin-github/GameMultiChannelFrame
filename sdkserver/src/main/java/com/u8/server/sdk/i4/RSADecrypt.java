package com.u8.server.sdk.i4;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;

/** */
/**
 * RSA安全编码组件
 * 
 * @version 1.0
 * @since 1.0
 */
public class RSADecrypt extends Coder {

	// 默认公钥(openssl)
	public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClLm/49JpZUfoy6e9DLmLJh+jaE36wcR5gJJEwx8e8yiQtD54kN9IFKTblJz4DKEHb/4sIntc6eIeIgjISofxI9zLvo3vyWEpTGKVkTMncE2VY4HKgSU0NvMmNq8N6f++EM8ObzjwgsruAGSzzsMCelOyhSJTZpJRK325XxzPC0QIDAQAB";
	public static final String KEY_ALGORITHM = "RSA";
	/** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** *//**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {

        byte[] keyBytes = new BASE64Decoder().decodeBuffer(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

	/** */
	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key)
			throws Exception {
		byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
	}

	public static void main(String[] args) {
		// 文档测试数据
		String testDataStr = "fD+R7EHYm4eV038aVIa3tRQpxnqJx2bJ2t3Mpg7CwWZOS4NVje2WbSL90Fg79hInLKZWa5yvCUhkLRfNbyTndS4y6UckCd8ERdSKmSDqjNxjrQOHilqWVFIjeJTa4qM275OWqMwg+vbFdVXsw61spXrAh1hddIIg+aVzQreaPjs=";
		try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] dcDataStr = base64Decoder.decodeBuffer(testDataStr);
            byte[] plainData = RSADecrypt.decryptByPublicKey(dcDataStr, RSADecrypt.DEFAULT_PUBLIC_KEY);  
            System.out.println("文档测试数据明文长度:" + plainData.length);  
            System.out.println(new String(plainData));
            
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
        }  
	}

}