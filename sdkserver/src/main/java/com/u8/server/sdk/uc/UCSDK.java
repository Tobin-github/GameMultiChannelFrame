package com.u8.server.sdk.uc;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by ant on 2014/12/12.
 */
public class UCSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(UCSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            log.info("--------------->UCSDK verify params's channel:" + channel.toJSON() + ", extension:" + extension);

            UHttpAgent httpClient = UHttpAgent.getInstance();

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("sid", extension);
            String url = channel.getMaster().getAuthUrl();
            Map<String, Object> proData = assemblyParameters(channel, ConfigHelper.VERIFYSESSION, data);

            //String jsonData = JsonUtils.encodeJson(proData);
            String jsonData = "{\"game\":{\"gameId\":862644},\"data\":{\"sid\":\"sst1game0c1510c538794446a6aa61f0ea844667146992\"},\"sign\":\"562ae09b41830abf9949a6b35f15ac90\",\"id\":1510801529071}";
            log.info("--------------->UCSDK verify requestJsonData:" + jsonData);

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");

            httpClient.post(url, headers, new ByteArrayEntity(jsonData.getBytes(Charset.forName("UTF-8"))), new UHttpFutureCallback() {
                @Override
                public void completed(String result) {
                    log.info("--------------->UCSDK verify result:" + result);
                    VerifySessionResponse rsp = (VerifySessionResponse) JsonUtils.decodeJson(result, VerifySessionResponse.class);

                    if (rsp != null) {
                        int code = rsp.getState().getCode();
                        if (code == 1) {
                            SDKVerifyResult verifyResult = new SDKVerifyResult(true, rsp.getData().getAccountId(), rsp.getData().getAccountId(), rsp.getData().getNickName());
                            callback.onSuccess(verifyResult);
                            return;
                        }
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);

                }

                @Override
                public void failed(String e) {
                    log.info("--------------->UCSDK verify fail:" + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }
            });


        } catch (Exception e) {
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
            log.error("--------------->UCSDK verify exception:" + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        log.info("--------------->UCSDK onGetOrderID params's user:" + user.toJSON() + ", order:" + order.toJSON());

        String appKey = order.getChannel().getCpAppKey();

        //可选参数
        String callbackInfo = order.getOrderID() + "";
        String cpOrderId = order.getOrderID() + "";

        DecimalFormat df = new DecimalFormat("0.00");
        String amount = df.format(order.getMoney() / 100.00);
        String accountId = user.getChannelUserID();
        String signType = "MD5";

        HashMap<String, String> map = new HashMap<>();
        map.put("amount", amount);
        map.put("accountId", accountId);
        map.put("callbackInfo", callbackInfo);
        map.put("cpOrderId", cpOrderId);

        String sign = sign(map, appKey);


        JSONObject jsonResp = new JSONObject();
        try {
            jsonResp.put("callbackInfo", callbackInfo);
            jsonResp.put("amount", amount);
            jsonResp.put("cpOrderId", cpOrderId);
            jsonResp.put("accountId", accountId);
            jsonResp.put("signType", signType);
            jsonResp.put("sign", sign);
            log.info("--------------->UCSDK onGetOrderID jsonStr:" + jsonResp.toString());
        } catch (JSONException e) {
            log.error("--------------->UCSDK onGetOrderID json parse exception:" + e.getMessage());
            e.printStackTrace();
        }

        if (callback != null) {
            callback.onSuccess(jsonResp.toString());
        }
    }

    private Map<String, Object> assemblyParameters(UChannel channel, String service, Map<String, Object> data) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", System.currentTimeMillis());// 当前系统时间
        //params.put("service", service);

        Map<String, Object> game = new HashMap<String, Object>();
        game.put("gameId", Integer.parseInt(channel.getCpAppID()));

        params.put("game", game);
        params.put("data", data);
        //params.put("encrypt", "md5");
		/*
		 * 签名规则=签名内容+apiKey 假定apiKey=202cb962234w4ers2aaa,sid=abcdefg123456 那么签名原文sid=abcdefg123456202cb962234w4ers2aaa
		 * 签名结果6e9c3c1e7d99293dfc0c81442f9a9984
		 */
        String signSource = getSignData(data) + channel.getCpAppKey();
        //String signSource = "sid=sst1game0c1510c538794446a6aa61f0ea844667146992d8d748db443a98bdcffd2568f283b3a8";
        String sign = getMD5Str(signSource);// MD5加密签名
        log.info("--------------->UCSDK verify [签名原文]" + signSource);
        log.info("--------------->UCSDK verify [签名结果]" + sign);
        params.put("sign", sign);
        String body = JsonUtils.encodeJson(params);// 把参数序列化成一个json字符串
        log.info("--------------->UCSDK verify [请求参数]" + body);
        return params;
    }

    private static String getSignData(Map params) {
        StringBuffer content = new StringBuffer();

        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key) == null ? "" : params.get(key).toString();
            if (value != null) {
                content.append(key + "=" + value);
            } else {
                content.append(key + "=");
            }
        }

        return content.toString();
    }

    private static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            Log.e("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            Log.e(e.toString());
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    /**
     * 验证支付
     *
     * @param channel
     * @param rsp
     * @return
     */
    public boolean verifyPay(UChannel channel, PayCallbackResponse rsp) {

        String signSource = "accountId=" + rsp.getData().getAccountId() + "amount=" + rsp.getData().getAmount() + "callbackInfo=" + rsp.getData().getCallbackInfo();
        if (rsp.getData().getCpOrderId() != null && rsp.getData().getCpOrderId().length() > 0) {
            signSource += "cpOrderId=" + rsp.getData().getCpOrderId();
        }
        signSource = signSource + "creator=" + rsp.getData().getCreator() + "failedDesc=" + rsp.getData().getFailedDesc() + "gameId=" + rsp.getData().getGameId()
                + "orderId=" + rsp.getData().getOrderId() + "orderStatus=" + rsp.getData().getOrderStatus()
                + "payWay=" + rsp.getData().getPayWay()
                + channel.getCpAppKey();

        String sign = getMD5Str(signSource);

        return sign.equals(rsp.getSign());

    }

    /**
     * 验证支付
     *
     * @param channel
     * @param rsp
     * @return
     */
    public String generatePaySign(UChannel channel, PayCallbackResponse rsp) {

        String signSource = "accountId=" + rsp.getData().getAccountId()
                + "amount=" + rsp.getData().getAmount()
                + "callbackInfo=" + rsp.getData().getCallbackInfo()
                + "cpOrderId=" + rsp.getData().getCpOrderId() + "creator=" + rsp.getData().getCreator() + "failedDesc=" + rsp.getData().getFailedDesc() + "gameId=" + rsp.getData().getGameId()
                + "orderId=" + rsp.getData().getOrderId() + "orderStatus=" + rsp.getData().getOrderStatus()
                + "payWay=" + rsp.getData().getPayWay()
                + channel.getCpAppKey();

        String sign = getMD5Str(signSource);

        return sign;

    }

    /* 签名工具方法
    * @param reqMap
    * @return
            */
    public static String sign(Map<String, String> reqMap, String signKey) {

        log.info("--------------->UCSDK onGetOrderID unsign's original map:" + reqMap.toString()+", signKey:"+signKey);

        //将所有key按照字典顺序排序
        TreeMap<String, String> signMap = new TreeMap<String, String>(reqMap);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            // sign和signType不参与签名
            if ("sign".equals(entry.getKey()) || "signType".equals(entry.getKey())) {
                continue;
            }
            //值为null的参数不参与签名
            if (entry.getValue() != null) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        //拼接签名秘钥
        stringBuilder.append(signKey);
        //剔除参数中含有的'&'符号
        String signSrc = stringBuilder.toString().replaceAll("&", "");
        log.info("--------------->UCSDK onGetOrderID unsign's str:" + signSrc);
        return EncryptUtils.md5(signSrc).toLowerCase();

    }

}