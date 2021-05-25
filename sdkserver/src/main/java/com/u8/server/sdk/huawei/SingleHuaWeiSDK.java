package com.u8.server.sdk.huawei;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * 华为SDK
 * Created by ant on 2015/4/27.
 */
public class SingleHuaWeiSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(SingleHuaWeiSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            log.info("----------->verify extension:" + extension);

            JSONObject json = JSONObject.fromObject(extension);
            String rtnCode = json.getString("retCode");
            String displayName = json.getString("displayName");
            String gameAuthSign = json.getString("gameAuthSign");
            String playerId = json.getString("playerId");
            String isAuth = json.getString("isAuth");
            String playerLevel = json.getString("playerLevel");
            String ts = json.getString("ts");

            String appId = channel.getCpAppID();
            String cpId = channel.getCpID();
            String priKey = channel.getCpPayPriKey();

            Map<String, String> params = new TreeMap<>();
            params.put("method", "external.hms.gs.checkPlayerSign");
            params.put("appId", appId);
            params.put("cpId", cpId);
            params.put("ts", ts);
            params.put("playerId", playerId);
            params.put("playerLevel", playerLevel);
            params.put("playerSSign", gameAuthSign);

            String sign = generateSign(appId, cpId, priKey, params);

            params.put("cpSign", sign);

            String url = channel.getMaster().getAuthUrl();
            log.info("-------->verify ,request params:" + params.toString() + ", url:" + url);

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("--------->verify ,The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("rtnCode");


                        if (code == 0) {

                            callback.onSuccess(new SDKVerifyResult(true, playerId, displayName, ""));
                            return;
                        }

                    } catch (Exception e) {
                        log.error("-------->verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("-------->verify fail,sdk's name:" + channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                }

                @Override
                public void failed(String e) {
                    log.error("-------->verify fail2,sdk's name:" + channel.getMaster().getSdkName() + " verify failed. the post result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });

        } catch (Exception e) {
            log.error("-------->verify exception,msg :" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }


    }

    private String generateSign(String appId, String cpId, String priKey, Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> param :
                    params.entrySet()) {
                sb.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "UTF-8")).append("&");

            }

            String subStr = sb.substring(0, sb.length() - 1);

            String rsaSign = RSAUtil.sign(subStr, priKey);
            //rsaSign=Base64.encode(rsaSign.getBytes("UTF-8"));
            //rsaSign = URLEncoder.encode(rsaSign, "UTF-8");
            return rsaSign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        JSONObject json = new JSONObject();
        json.put("orderId", order.getOrderID());

        callback.onSuccess(json.toString());
    }

}
