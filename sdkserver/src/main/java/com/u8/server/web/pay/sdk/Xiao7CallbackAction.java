package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.x7sy.SignUtils;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UUserManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 小7支付回调通知接口
 * Created by ant on 2018/01/22.
 */
@Controller
@Namespace("/pay/x7sy")
public class Xiao7CallbackAction extends UActionSupport {

    private static Logger log = Logger.getLogger(Xiao7CallbackAction.class.getName());

    @Autowired
    private UUserManager mUserManager;
    @Autowired
    private UOrderManager orderManager;

    private String encryp_data;
    private String extends_info_data;
    private String game_area;
    private String game_level;
    private String game_orderid;
    private String game_role_id;
    private String game_role_name;
    private String sdk_version;
    private String subject;
    private String xiao7_goid;
    private String sign_data;


    //签名算法
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    @Action("payCallback")
    public void payCallback() {
        try {



            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("=========参数======== params'sname:" + name + ", value:" + value);
            }

            long orderID = Long.parseLong(game_orderid);

            UOrder order = orderManager.getOrder(orderID);

            if (order == null || order.getChannel() == null) {
                log.error("--------------->The order is null or the channel is null.");
                this.renderState("failed");
                return;
            }
            log.info("--------------->order:" + order.toJSON());

            if (order.getState() > PayState.STATE_PAYING) {
                log.error("--------------->The state of the order is complete. The state is " + order.getState());
                this.renderState("success");
                return;
            }

            String cpPayKey = order.getChannel().getCpPayKey();

            String queryString = getQueryString();
            log.info("--------------->cpPayKey:" + cpPayKey + ", queryString:" + queryString);
            Map<String, String> channelParams = SignUtils.generSign(queryString, cpPayKey);
            //Map<String, String> channelParams = getChannelParams(queryString, cpPayKey);

            if (null == channelParams) {
                log.error("--------------->sign error");
                this.renderState("failed");
                return;
            }

            String pay_price = channelParams.get("pay_price");
            String guid = channelParams.get("guid");
            log.info("--------------->pay_price:" + pay_price + ", guid:" + guid);

            int moneyInt = (int) (Math.round(Double.parseDouble(pay_price) * 100));  //以分为单位

            if (order.getMoney() != moneyInt) {
                log.error("--------------->order money:" + order.getMoney() + ", channel money:" + moneyInt);
                this.renderState("failed");
                return;
            }

            UUser user = mUserManager.getUser(order.getUserID());

            if (user == null || user.getChannel() == null) {
                log.error("--------------->The user is null or the channel is null.");
                this.renderState("failed");
                return;
            }

            if (!user.getChannelUserID().equals(guid)) {
                log.error("--------------->The user is not right");
                this.renderState("failed");
                return;
            }

            log.info("--------------->user:" + user.toJSON());

            //这里是比较解密后的订单号与我们通过POST传递过来的订单号是否一致
            if (channelParams.containsKey("game_orderid") && channelParams.get("game_orderid").equals(game_orderid)) {
                order.setState(PayState.STATE_SUC);
                order.setCompleteTime(new Date());
                order.setRealMoney(moneyInt);
                order.setChannelOrderID(xiao7_goid);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState("success");

            } else {
                order.setState(PayState.STATE_FAILED);
                order.setChannelOrderID(xiao7_goid);
                orderManager.saveOrder(order);
                this.renderState("failed");
            }

        } catch (Exception e) {
            log.error("--------------->The payBack exception:" + e.getMessage());
            e.printStackTrace();
            try {
                this.renderState("failed");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private String getQueryString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("encryp_data=").append(URLEncoder.encode(encryp_data, "utf-8")).append("&")
                    .append("extends_info_data=").append(URLEncoder.encode(extends_info_data, "utf-8")).append("&")
                    .append("game_area=").append(URLEncoder.encode(game_area, "utf-8")).append("&")
                    .append("game_level=").append(URLEncoder.encode(game_level, "utf-8")).append("&")
                    .append("game_orderid=").append(URLEncoder.encode(game_orderid, "utf-8")).append("&")
                    .append("game_role_id=").append(URLEncoder.encode(game_role_id, "utf-8")).append("&")
                    .append("game_role_name=").append(URLEncoder.encode(game_role_name, "utf-8")).append("&")
                    .append("sdk_version=").append(URLEncoder.encode(sdk_version, "utf-8")).append("&")
                    .append("subject=").append(URLEncoder.encode(subject, "utf-8")).append("&")
                    .append("xiao7_goid=").append(URLEncoder.encode(xiao7_goid, "utf-8")).append("&")
                    .append("sign_data=").append(URLEncoder.encode(sign_data, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        log.debug("--------------->The getQueryString =:" + sb.toString());
        return sb.toString();
    }


    private Map<String, String> getChannelParams(String queryString, String pubKey) {
        try {

            String[] queryStringArr = queryString.split("&");
            String[] queryItemArr = new String[2];
            String[] queryKeyArr = {"encryp_data", "extends_info_data", "game_area", "game_level", "game_orderid", "game_role_id", "game_role_name", "sdk_version", "subject", "xiao7_goid", "sign_data"};
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
                }
            }
            String sign = map.get("sign_data");
            map.remove("sign_data");

            String sourceStr = buildHttpQueryNoEncode(map);
            //验签
            if (!verifySign(sourceStr, sign, loadPublicKeyByStr(pubKey))) {
                log.error("-------------->failed:sign_data_verify_failed");
                this.renderState("failed");
                return null;
            }
            //解密
            String decryptData = new String(publicKeyDecrypt(loadPublicKeyByStr(pubKey), baseDecode(map.get("encryp_data"))));
            Map<String, String> decryptMap = decodeHttpQueryNoDecode(decryptData);
            if (!decryptMap.containsKey("game_orderid") || !decryptMap.containsKey("pay_price") || !decryptMap.containsKey("guid")) {
                log.error("-------------->failed:encryp_data_decrypt_failed");
            }
            if (!decryptMap.get("game_orderid").equals(map.get("game_orderid"))) {
                log.error("-------------->failed:game_orderid error");
            }
            log.info("=====参数======  :" + decryptMap);
            return decryptMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void renderState(String msg) throws IOException {
        renderText(msg);
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



    public String getEncryp_data() {
        return encryp_data;
    }

    public void setEncryp_data(String encryp_data) {
        this.encryp_data = encryp_data;
    }

    public String getExtends_info_data() {
        return extends_info_data;
    }

    public void setExtends_info_data(String extends_info_data) {
        this.extends_info_data = extends_info_data;
    }

    public String getGame_area() {
        return game_area;
    }

    public void setGame_area(String game_area) {
        this.game_area = game_area;
    }

    public String getGame_level() {
        return game_level;
    }

    public void setGame_level(String game_level) {
        this.game_level = game_level;
    }

    public String getGame_orderid() {
        return game_orderid;
    }

    public void setGame_orderid(String game_orderid) {
        this.game_orderid = game_orderid;
    }

    public String getGame_role_id() {
        return game_role_id;
    }

    public void setGame_role_id(String game_role_id) {
        this.game_role_id = game_role_id;
    }

    public String getGame_role_name() {
        return game_role_name;
    }

    public void setGame_role_name(String game_role_name) {
        this.game_role_name = game_role_name;
    }

    public String getSdk_version() {
        return sdk_version;
    }

    public void setSdk_version(String sdk_version) {
        this.sdk_version = sdk_version;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getXiao7_goid() {
        return xiao7_goid;
    }

    public void setXiao7_goid(String xiao7_goid) {
        this.xiao7_goid = xiao7_goid;
    }

    public String getSign_data() {
        return sign_data;
    }

    public void setSign_data(String sign_data) {
        this.sign_data = sign_data;
    }
}
