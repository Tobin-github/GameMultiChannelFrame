package com.u8.server.sdk.nubia;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2015/2/27.
 */
public class NubiaSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(NubiaSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            JSONObject loginData = JSONObject.fromObject(extension);

            String uid = loginData.getString("uid");
            String gameId = loginData.getString("gameId");
            String sessionId = loginData.getString("sessionId");

            String appId = channel.getCpAppID();
            String secretKey = channel.getCpAppSecret();

            Map<String, String> requestMap = new HashMap<String, String>();
            requestMap.put("uid", uid);
            requestMap.put("data_timestamp", System.currentTimeMillis() + "");
            requestMap.put("game_id", gameId);
            requestMap.put("session_id", sessionId);

            String sign = generateSign(requestMap, appId, secretKey);
            requestMap.put("sign", sign);

            String url = channel.getChannelAuthUrl();
            log.info("------------->verify request's data:" + requestMap.toString() + ",auth url:" + url);

            UHttpAgent.getInstance().post(url, requestMap, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("------------->verify request result:" + result);
                        JSONObject loginResult = JSONObject.fromObject(result);

                        int code = loginResult.getInt("code");
                        if (0 == code) {
                            log.info("------------->verify request result is success:" + result);
                            SDKVerifyResult vResult = new SDKVerifyResult(true, uid, gameId, "");
                            callback.onSuccess(vResult);
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("------------->verify request result exception-1:" + e.getMessage());
                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. msg:" + e.getMessage());
                        return;

                    }

                    log.error("------------->verify request result fail-1:" + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
                }

                @Override
                public void failed(String e) {
                    log.error("------------->verify request result fail-2:" + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });


        } catch (Exception e) {
            log.error("------------->verify request result exception-2:" + e.getMessage());
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
            e.printStackTrace();
        }

    }

    private String generateSign(Map<String, String> requestMap, String appId, String secretKey) {
        String unSignData = ParameterUtil.getSignData(requestMap);
        String param = ":" + appId + ":" + secretKey;
        String sign = "";
        log.info("------------->unSignParams:" + unSignData + ",appId:" + appId+",secretKey:"+secretKey);
        try {
            sign = MD5Util.sign(unSignData, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        String appId = order.getChannel().getCpAppID();
        String uid = user.getChannelUserID();
        String cpOrderId = order.getOrderID()+"";

        DecimalFormat df=new DecimalFormat("0.00");
        String amount =df.format(order.getMoney()/100.00);
        String productName = order.getProductName();
        String productDes = order.getProductDesc();
        String number = "1";
        String dataTimestamp = System.currentTimeMillis()+"";
        String gameId = user.getChannelUserName();

        String u8AppId = order.getChannel().getCpAppID();
        String u8SecretKey = order.getChannel().getCpAppSecret();

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("app_id", appId);
        requestMap.put("uid", uid);
        requestMap.put("cp_order_id", cpOrderId);
        requestMap.put("amount", amount);
        requestMap.put("product_name", productName);
        requestMap.put("product_des", productDes);
        requestMap.put("number", number);
        requestMap.put("data_timestamp", dataTimestamp);

        String cpOrderSign = generateSign(requestMap, u8AppId, u8SecretKey);

        JSONObject json = new JSONObject();

        try {
            json.put("appId", appId);
            json.put("uid", uid);
            json.put("cpOrderId", cpOrderId);
            json.put("amount", amount);
            json.put("productName", productName);
            json.put("productDes", productDes);
            json.put("number", number);
            json.put("dataTimestamp", dataTimestamp);
            json.put("gameId", gameId);
            json.put("cpOrderSign", cpOrderSign);

            log.info("------------->order response result:" + json.toString());
        } catch (Exception e) {
            log.error("------------->order response exception:" + e.getMessage());
            e.printStackTrace();
        }

        if (callback != null) {
            log.info("------------->order response success:" + json.toString());
            callback.onSuccess(json.toString());
        }
    }
}
