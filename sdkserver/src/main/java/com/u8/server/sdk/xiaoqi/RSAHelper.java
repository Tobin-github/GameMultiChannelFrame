package com.u8.server.sdk.xiaoqi;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ant on 2016/11/19.
 */
public class RSAHelper {

    //签名算法
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    //Base64解码
    public static byte[] decode(String str) {
        return Base64.decodeBase64(str.getBytes());
    }

    //Base64编码
    public static String encode(final byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    //从字符串加载公钥
    public static PublicKey loadPublicKeyByStr(String key) throws Exception {
        try {
            String publicKeyStr = "";

            int count = 0;
            for (int i = 0; i < key.length(); ++i)
            {
                if (count < 64)
                {
                    publicKeyStr += key.charAt(i);
                    count++;
                }
                else
                {
                    publicKeyStr += key.charAt(i) + "\r\n";
                    count = 0;
                }
            }
            byte[] buffer = decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            //System.out.println(publicKey);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    //公钥解密
    public static byte[] decrypt(String publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }

        PublicKey key = loadPublicKeyByStr(publicKey);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            int inputLen = cipherData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(cipherData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(cipherData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }
    public static String buildHttpQuery(Map<String, String> data) throws UnsupportedEncodingException {
        String builder = new String();
        for (Map.Entry<String, String> pair : data.entrySet()) {
            builder += URLEncoder.encode(pair.getKey(), "utf-8") + "=" + URLEncoder.encode(pair.getValue(), "utf-8") + "&";
        }
        return builder.substring(0, builder.length() - 1);
    }

    public static Map<String, String> decodeHttpQuery(String httpQuery) throws UnsupportedEncodingException
    {
        Map<String, String> map = new TreeMap<String, String>();

        for(String s: httpQuery.split("&")) {
            String pair[] = s.split("=");
            map.put(URLDecoder.decode(pair[0], "utf-8"), URLDecoder.decode(pair[1], "utf-8"));
        }

        return map;
    }


    //RSA验签名检查
    public static boolean doCheck(String content, String sign, String publicKey)
    {
        try
        {
            PublicKey key = loadPublicKeyByStr(publicKey);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(key);
            //System.out.println(content.getBytes());
            signature.update(content.getBytes());

            boolean bverify = signature.verify(decode(sign));
            return bverify;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

}
