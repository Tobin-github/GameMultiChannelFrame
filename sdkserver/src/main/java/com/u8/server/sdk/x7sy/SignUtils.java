package com.u8.server.sdk.x7sy;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author kris
 * @create 2018-01-25 15:16
 */
public class SignUtils {
    //签名算法
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    //公钥
    //private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+zCNgOlIjhbsEhrGN7De2uYcfpwNmmbS6HYYI5KljuYNua4v7ZsQx5gTnJCZ+aaBqAIRxM+5glXeBHIwJTKLRvCxC6aD5Mz5cbbvIOrEghyozjNbM6G718DvyxD5+vQ5c0df6IbJHIZ+AezHPdiOJJjC+tfMF3HdX+Ng/VT80LwIDAQAB";

    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static Map<String, String> generSign(String queryString, String pubKey) {
        try {
            /**************************************************************************************
             * 注意这里假设接收到的数据是下面的一个字符串并且是键值内容是编码过的，如果游戏方接收到的数据并不是这样的话，请根据实际情况操作。
             * 所有参与验签和解密的参数都是没有进行编码的（如果是已经编码的要进行反编码）。
             **************************************************************************************/
            String[] queryStringArr = queryString.split("&");
            String[] queryItemArr = new String[2];
            String[] queryKeyArr = {"encryp_data", "extends_info_data", "game_area", "game_level", "game_orderid", "game_role_id", "game_role_name", "sdk_version", "subject", "xiao7_goid", "sign_data"};
            /************************************************************************************************
             * 使用treemap按键值自动排序
             * 下面将会得到包含：
             * encryp_data			将game_orderid（游戏订单号）、guid（用户唯一标识）、pay_price（道具金额）使用私钥加密起来的，
             * 						游戏厂商需要在验证sign_data后将当前参数使用公钥解密对比【游戏订单号】、【用户唯一标识】、【道具金额】。
             * extends_info_data	当前参数是小7提供的支付透传参数
             * game_area			角色所在的游戏区
             * game_level			用户游戏角色等级
             * game_orderid			游戏订单号
             * game_role_id			游戏角色ID信息
             * game_role_name		游戏角色名称
             * sdk_version			使用当前回调的SDK版本
             * subject				游戏道具名称
             * xiao7_goid			游戏订单在小7的唯一标识
             * sign_data			是对上面所有字段的私钥签名
             ************************************************************************************************/
            Map<String, String> map = new TreeMap<String, String>();
            String tempStr = "";
            Arrays.sort(queryKeyArr);
            for (String str : queryStringArr) {
                queryItemArr = str.split("=");
                if (Arrays.binarySearch(queryKeyArr, queryItemArr[0]) >= 0) {
                    tempStr = "";
                    if (queryItemArr.length == 2) {
                        tempStr = queryItemArr[1];
                    }

                    map.put(queryItemArr[0], URLDecoder.decode(tempStr, "utf-8"));

                }
            }
            for (String q_key : queryKeyArr) {
                if (map.containsKey(q_key) != true) {
                    System.out.print("failed:order error");
                    //System.exit(0);
                    return null;
                }
            }
            String sign = map.get("sign_data");
            map.remove("sign_data");

            String sourceStr = buildHttpQueryNoEncode(map);
            //验签
            /*if (!verifySign(sourceStr, sign, loadPublicKeyByStr(pubKey))) {
                System.out.print("failed:sign_data_verify_failed");
                //System.exit(0);
            }*/
            //解密
            String decryptData = new String(publicKeyDecrypt(loadPublicKeyByStr(pubKey), baseDecode(map.get("encryp_data"))));
            /*************************************************************
             * 下面这里将会返回是一个包含game_orderid、guid、pay_price的双列集合
             * {game_orderid=xxxx, guid=xxxx, pay_price=xxxxx}
             *************************************************************/
            Map<String, String> decryptMap = decodeHttpQueryNoDecode(decryptData);
            /******************************************************
             * 这里需要判断是否存在game_orderid、pay_price、guid三个值。
             ******************************************************/
            if (!decryptMap.containsKey("game_orderid") || !decryptMap.containsKey("pay_price") || !decryptMap.containsKey("guid")) {
                System.out.print("failed:encryp_data_decrypt_failed");
                //System.exit(0);
                return null;
            }
            /*********************************************************************
             * 对比一下解出来的订单号与传递过来的订单号是否一致。这里同时要比较一下当前订单号是否是属于当前小7渠道。
             ********************************************************************/
            if (!decryptMap.get("game_orderid").equals(map.get("game_orderid"))) {
                System.out.print("failed:game_orderid error");
                //System.exit(0);
                return null;
            }
            /********************************************************************
             * 下面这里是一系列的比较（这里需要游戏方对这一系列的参数进行比较，对于每个参数代表的意思，请查看文档。）
             * "game_area"		=>"game_area error",
             * "game_orderid"	=>"game_orderid error",
             * "game_role_id"	=>"game_role_id error",
             * "game_role_name"	=>"game_role_name error",
             * "guid"			=>"guid error",
             * "xiao7_goid"		=>"xiao7_goid error",
             * "pay_price"		=>"pay_price error",
             *********************************************************************/
            /***************************************************************************************************************************
             * 这里比较重要的是要将解密得到的价格pay_price与CP中指定订单中的商品的价格（这个价格的值必须是游戏方定义的值，不能是从前端接收到的值，因为从前端接收到的值是有可能会被篡改的），
             * 注意这里游戏方务必是需要比较订单价格是否与当期那订单对应。
             * 对于价格小7服务器是精确到小数点后面两位返回的。
             * 对于已经给【小7服务器】返回过正确响应并已经发货的订单但是由于【小7服务器】的一些原因还会给游戏支付回调地址发送支付回调请求，这时候游戏厂商无需重新发货，
             * 对比支付信息其中比较重要一点是对比一下xiao7_goid是否与第一次收到的xiao7_goid一致，
             * 如果不一致直接返回failed:xiao7_goid error错误，如果对比所有信息无误后直接返回一个success。
             * 对于一切都是正确就返回success（注意是小写）字符串
             ***************************************************************************************************************************/
            System.out.println("=====参数======   :" + decryptMap);
            return decryptMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String buildHttpQueryNoEncode(Map<String, String> data) throws UnsupportedEncodingException {
        String builder = new String();
        for (Map.Entry<String, String> pair : data.entrySet()) {
            builder += pair.getKey() + "=" + pair.getValue() + "&";
        }
        return builder.substring(0, builder.length() - 1);
    }

    //RSA验签名检查
    public static boolean verifySign(String content, String sign, PublicKey publicKey) {
        try {
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(publicKey);
            //System.out.println(content.getBytes());
            signature.update(content.getBytes());

            boolean bverify = signature.verify(baseDecode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //Base64解码
    public static byte[] baseDecode(String str) {
        return Base64.decodeBase64(str.getBytes());
    }

    //Base64编码
    public static String baseEncode(final byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    //从字符串加载公钥
    public static PublicKey loadPublicKeyByStr(String pubKey) throws Exception {
        try {
            String publicKeyStr = "";

            int count = 0;
            for (int i = 0; i < pubKey.length(); ++i) {
                if (count < 64) {
                    publicKeyStr += pubKey.charAt(i);
                    count++;
                } else {
                    publicKeyStr += pubKey.charAt(i) + "\r\n";
                    count = 0;
                }
            }
            byte[] buffer = baseDecode(publicKeyStr);
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
    public static byte[] publicKeyDecrypt(PublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

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

    private static Map<String, String> decodeHttpQueryNoDecode(String httpQuery) throws UnsupportedEncodingException {
        Map<String, String> map = new TreeMap<String, String>();

        for (String s : httpQuery.split("&")) {
            String pair[] = s.split("=");
            map.put(pair[0], pair[1]);
        }
        return map;
    }
}
