package com.u8.server.sdk.yunwa;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 云娃SDK
 * Created by ant on 2018/01/11.
 */
public class YunWaSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(YunWaSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String accessToken = json.getString("token");

            String appId = channel.getCpAppID();
            String appKey = channel.getCpAppKey();
            String secret = EncryptUtils.md5(appId + "&" + appKey);

            JSONObject params = new JSONObject();
            params.put("accessToken", accessToken);
            params.put("secret", secret);
            params.put("appId", channel.getCpAppID());

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");

            log.info("------------------>The yunwa auth requestParam:"+params.toString()+", appId:"+appId+", appKey:"+appKey+", authUrl:"+channel.getChannelAuthUrl());

            UHttpAgent.getInstance().post(channel.getChannelAuthUrl(), headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))), new UHttpFutureCallback() {
                @Override
                public void completed(String content) {

                    try {
                        log.info("------------------>The yunwa auth result is "+content);
                        try{
                            JSONObject authJson = JSONObject.fromObject(content);
                            String userId = authJson.getString("account");
                            String result = authJson.getString("result");
                            if(StringUtils.isNotBlank(result) && "0".equals(result)){
                                SDKVerifyResult vResult = new SDKVerifyResult(true, userId, "", "");
                                callback.onSuccess(vResult);
                                return;
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the auth result is " + content);

                    } catch (Exception e) {
                        log.error("yunwa verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("yunwa verify failed,the result is " + content);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + content);
                }

                @Override
                public void failed(String e) {
                    log.error("yunwa verify callback failed,the result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        }catch (Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        JSONObject json = new JSONObject();

        json.put("orderId", order.getOrderID()+"");
        json.put("notifyUrl", order.getChannel().getPayCallbackUrl());

        if (callback != null) {
            callback.onSuccess(json.toString());
        }
    }
}
