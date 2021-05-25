package com.u8.server.sdk.anzhi;

import java.security.MessageDigest;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.sun.crypto.provider.SunJCE;

public class Des3Util extends SecurityUtil {

	private static final String ALGORITHM = "DESede";
	private static SunJCE sunJCE;

	// 定义 加密算法,可用 DES,DESede,Blowfish
	// keybyte为加密密钥，长度为24字节
	// src为被加密的数据缓冲区（源）
	public byte[] encrypt(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
			// 加密
			Cipher c1 = Cipher.getInstance(ALGORITHM);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区
	public byte[] decrypt(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
			// 解密
			Cipher c1 = Cipher.getInstance(ALGORITHM);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


	// 解密 Base64（3DES（消息体））
	public static  String decrypt(String args, String key) {
		try {
			Des3Util d3u = new Des3Util();

			Security.addProvider(new SunJCE());

			return d3u.decryptFromBase64(key, args, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	// 加密 Base64（3DES（消息体））
	public static  String encrypt(String body, String key) {
		try {
			Des3Util d3u = new Des3Util();

			Security.addProvider(new SunJCE());

			return d3u.encryptToBase64(key, body, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 消息签名
	public static String getSigned(String buf){
		try {
		byte[] input;
			input = buf.getBytes("UTF-8");
		MessageDigest alga = MessageDigest.getInstance("MD5");
		// MessageDigest alga = MessageDigest.getInstance("SHA-1");
		alga.update(input);
		byte[] md5Hash = alga.digest();
//		System.out.println("MD5:" + new String(md5Hash));
		if (md5Hash != null) {
			return (new sun.misc.BASE64Encoder()).encode(md5Hash);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
