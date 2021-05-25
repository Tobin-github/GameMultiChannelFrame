package com.u8.server.sdk.lenovo;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class RSA 
{
	private static final String  SIGN_ALGORITHMS = "MD5WithRSA";
	
	/**
	* RSA验签名检查
	* @param content 待签名数据
	* @param sign 签名值
	* @param ali_public_key  爱贝公钥
	* @param input_charset 编码格式
	* @return 布尔值
	*/
	public static String str;
	public static boolean verify(String content, String sign, String iapp_pub_key, String input_charset)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = Base64.decode(iapp_pub_key);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		
			java.security.Signature signature = java.security.Signature
			.getInstance(SIGN_ALGORITHMS);
		
			signature.initVerify(pubKey);
			signature.update( content.getBytes(input_charset) );
		
			return signature.verify( Base64.decode(sign) );
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	* RSA签名
	* @param content 待签名数据
	* @param privateKey 商户私钥
	* @param input_charset 编码格式
	* @return 签名值
	*/
	public static String sign(String content, String privateKey, String input_charset)
	{
        try 
        {
        	PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decode(privateKey) ); 
        	KeyFactory          keyf        = KeyFactory.getInstance("RSA");
        	PrivateKey          priKey 		= keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update( content.getBytes(input_charset) );
            byte[] signed = signature.sign();
            return Base64.encode(signed);
         
        	//System.out.println("响应数据："+signed);
            
          
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        
        return null;
    }
	
	
	public static String md5s(String plainText) {
		String buff = null;
		  try {
		   MessageDigest md = MessageDigest.getInstance("MD5");
		   md.update(plainText.getBytes());
		   byte b[] = md.digest();
		   int i;

		   StringBuffer buf = new StringBuffer("");
		   for (int offset = 0; offset < b.length; offset++) {
		    i = b[offset];
		    if (i < 0)
		     i += 256;
		    if (i < 16)
		     buf.append("0");
		    buf.append(Integer.toHexString(i));
		   }
		   buff =buf.toString();
		    Base64.encode(buff.getBytes());
		    System.out.println("base64:"+ Base64.encode(buff.getBytes()));
		   str = buf.toString();
		   System.out.println("result: " + buf.toString());// 32位的加密
		   System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		  } catch (NoSuchAlgorithmException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();

		  }
		return Base64.encode(buff.getBytes());
	
	
	
	
  }
	
}
