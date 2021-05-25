package com.u8.server.sdk.pp;

import java.io.BufferedReader;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.security.InvalidKeyException;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.X509EncodedKeySpec;  
import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.NoSuchPaddingException;  
import sun.misc.BASE64Decoder;  
import sun.misc.BASE64Encoder;
 

public class RSAEncrypt {  
 
	//默认公钥(openssl)

    public static final String DEFAULT_PUBLIC_KEY=   
		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1BvMF7LGlQb7OEetChUg" + "\r\n" +  
		"grG6+/GpaH7os5WapsMbcRHftljf2A1Wgy3GvcbILJRcINWohuhrBQ2+PIBDyBof" + "\r\n" +  
		"eVU/LEvaT1hQyyJ3OOI1Qa/vXPtXCTUPjfKk5d+0jr7xKa1rES0xJF8s6Bpll6QA" + "\r\n" +  
		"nfuiSEbBq0O5TTFJAmPR0o9+Ity0retQ0W91O4rrCkfS2aSMsKeA5aaz1ixFwDS3" + "\r\n" +  
		"4dpAO0gqhFUvyHITWkS0n7/4MAVqCIoVSfZwIFZ7k2Bf39EouAYbkuYue6rxIlbV" + "\r\n" +  
		"wABAcopMxr4aHbbJRs7Ll62uHyio10jIHXesdz3Ur4GrKOSomay6vAaT4RjggeCv" + "\r\n" +  
		"SwIDAQAB" + "\r\n";
  
    /** 
     * 私钥 
     */  
    private RSAPrivateKey privateKey;  
  
    /** 
     * 公钥 
     */  
    private RSAPublicKey publicKey;  
      
    /** 
     * 字节数据转字符串专用集合 
     */  
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};  
    
    /** 
     * 获取私钥 
     * @return 当前的私钥对象 
     */  
    public RSAPrivateKey getPrivateKey() {  
        return privateKey;  
    }  
    
    /** 
     * 获取公钥 
     * @return 当前的公钥对象 
     */  
    public RSAPublicKey getPublicKey() {  
        return publicKey;  
    }  
  
    /** 
     * 随机生成密钥对 
     */  
    public void genKeyPair(){  
        KeyPairGenerator keyPairGen= null;  
        try {  
            keyPairGen= KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        keyPairGen.initialize(1024, new SecureRandom());  
        KeyPair keyPair= keyPairGen.generateKeyPair();  
        this.privateKey= (RSAPrivateKey) keyPair.getPrivate();  
        this.publicKey= (RSAPublicKey) keyPair.getPublic();  
    }  
  
    /** 
     * 从文件中输入流中加载公钥 
     * @param in 公钥输入流 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public void loadPublicKey(InputStream in) throws Exception{
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            loadPublicKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("公钥数据流读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥输入流为空");  
        }  
    }  
  
  
    /** 
     * 从字符串中加载公钥 
     * @param publicKeyStr 公钥数据字符串 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public void loadPublicKey(String publicKeyStr) throws Exception{  
    	//System.out.println("publicKeyStr:"+ publicKeyStr);
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");  
        } catch (IOException e) {  
            throw new Exception("公钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");  
        }  
    }  
  
    /** 
     * 公钥加密过程 
     * @param publicKey 公钥 
     * @param plainTextData 明文数据 
     * @return 
     * @throws Exception 加密过程中的异常信息 
     */  
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{  
        if(publicKey== null){  
            throw new Exception("加密公钥为空, 请设置");  
        }  
        Cipher cipher= null;  
        try {
        	//使用默认RSA
            cipher= Cipher.getInstance("RSA");
            //cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());    
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            byte[] output= cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此加密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("加密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("明文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("明文数据已损坏");  
        }  
    }  

    /** 
     * 公钥解密过程 
     * @param publicKey 公钥 
     * @param cipherData 密文数据 
     * @return 明文 
     * @throws Exception 解密过程中的异常信息 
     */  
    public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception{  
        if (publicKey== null){  
            throw new Exception("解密公钥为空, 请设置");  
        }  
        Cipher cipher= null;  
        try {  
        	//使用默认RSA
            cipher= Cipher.getInstance("RSA");
            //cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            byte[] output= cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("解密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }         
    }  
  
      
    /** 
     * 字节数据转十六进制字符串 
     * @param data 输入数据 
     * @return 十六进制内容 
     */  
    public static String byteArrayToString(byte[] data){  
        StringBuilder stringBuilder= new StringBuilder();  
        for (int i=0; i<data.length; i++){  
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移   
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);  
            //取出字节的低四位 作为索引得到相应的十六进制标识符   
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i<data.length-1){  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }  
  
  
    public static void main(String[] args){  
        RSAEncrypt rsaEncrypt= new RSAEncrypt();  
        //rsaEncrypt.genKeyPair();   
  
        //加载公钥   
        try {  
            rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);  
            System.out.println("加载公钥成功");  
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载公钥失败");  
        }  
        
        //文档测试数据 
        String testDataStr = "d7C8ph77SaqWsSk+T2KpHXKuhplBdZOosP9a7XnQAziC4A0aO8yQG0RdyMz/Ya2G77V0ufOq0QyHdv25dONOwuCGrq+fUMrn+l8D5fdIsGI0mIvbVVum2A3arxuG0toMhqIlxKD88CIs2hyEMit6exRRMnFgHFjcDh1KVajHC7DecfmhRunQctPFX9Z2JxIpLMGYsqb6qKqSaO0sdfamnFpl2ozwSKBTijAECj7Xx354SiLJTqbsERWx1b5dLR/iuZpODSY9IY3RHdEJ60e+ggk1q+n5MHEdL+M9tnbqw7kYsiLYSVvFJ7YTyqSR4qGC/GyGUAJdNiiNjB8MOGsUBQ==";    
  
        try {
            BASE64Encoder base64Encoder = new BASE64Encoder();
            BASE64Decoder base64Decoder = new BASE64Decoder();
            
            byte[] dcDataStr = base64Decoder.decodeBuffer(testDataStr);
            byte[] plainData = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), dcDataStr);  
            System.out.println("文档测试数据明文长度:" + plainData.length);  
            System.out.println(RSAEncrypt.byteArrayToString(plainData));  
            System.out.println(new String(plainData));
            
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
        }  
    }  
}  