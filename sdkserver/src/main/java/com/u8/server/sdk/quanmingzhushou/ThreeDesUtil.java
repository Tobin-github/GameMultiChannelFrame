package com.u8.server.sdk.quanmingzhushou;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * author Administrator on 2015-09-21.
 */
public class ThreeDesUtil {

    static {
        //添加新安全算法,如果用JCE就要把它添加进去
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    /**
     * args在java中调用sun公司提供的3DES加密解密算法时，需要使
     * 用到$JAVA_HOME/jre/lib/目录下如下的4个jar包：
     * jce.jar
     * security/US_export_policy.jar
     * security/local_policy.jar
     * ext/sunjce_provider.jar
     */


    private static final String Algorithm = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish

    //keybyte为加密密钥，长度为24字节
    //src为被加密的数据缓冲区（源）
    private static byte[] encryptMode(byte[] keybyte, byte[] src) throws Exception {
        //生成密钥
        SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
        //加密
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        return c1.doFinal(src);//在单一方面的加密或解密
    }

    //keybyte为加密密钥，长度为24字节
    //src为加密后的缓冲区
    private static byte[] decryptMode(byte[] keybyte, byte[] src) throws Exception {
        //生成密钥
        SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
        //解密
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        return c1.doFinal(src);
    }


//    private static byte[] hex(String str) throws Exception {
//        String f = MD5Util.getMD5HexString(str);
//        byte[] bkeys = f.getBytes();
//        byte[] enk = new byte[24];
//        System.arraycopy(bkeys, 0, enk, 0, 24);
//        return enk;
//    }

    /**
     * 加密数据
     *
     * @param key 秘钥
     * @param str 需要加密内容
     * @return 加密后的数据
     */
    public static String thressDesEncrypt(String key, String str) throws Exception {
        return Base64.encode(encryptMode(key.getBytes(), str.getBytes()));
    }

    /**
     * 解密数据
     *
     * @param key 秘钥
     * @param str 需要解密内容
     * @return 解密后的数据
     */
    public static String thressDesDecrypt(String key, String str) throws Exception {
        return new String(decryptMode(key.getBytes(), Base64.decode(str)));
    }
}
