package com.u8.server.sdk.tianyuyou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * SDK
 * Created by ant on 2016/5/10.
 */
public class TianYuYouSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(TianYuYouSDK.class.getName());
    
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {

            JSONObject json = JSONObject.fromObject(extension);
            String memId = json.getString("memId");
            String userToken = json.getString("userToken");

            String appId = channel.getCpAppID();
            String appKey = channel.getCpAppKey();

            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(appId)
                    .append("&mem_id=").append(memId)
                    .append("&user_token=").append(userToken)
                    .append("&app_key=").append(appKey);
            log.info("TianYuYouSDK unsign str:" + sb.toString());
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            log.info("TianYuYouSDK signed str:" + sign);

            String url = channel.getChannelAuthUrl();

            JSONObject params = new JSONObject();
            params.put("app_id", appId);
            params.put("mem_id", memId);
            params.put("user_token", userToken);
            params.put("sign", sign);


            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");

            UHttpAgent.getInstance().post(url, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))), new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("TianYuYouSDK verify result:" + result);
                        JSONObject json = JSONObject.fromObject(result);
                        String status = json.getString("status");

                        if ("1".equals(status)) {
                            SDKVerifyResult vResult = new SDKVerifyResult(true, memId, "", "");

                            callback.onSuccess(vResult);
                            log.info("TianYuYouSDK verify successful:" + result);
                            return;
                        }

                    } catch (Exception e) {
                        log.error("TianYuYouSDK verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("TianYuYouSDK verify failed,the result is " + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);
                }

                @Override
                public void failed(String e) {
                    log.error("TianYuYouSDK verify callback failed,the result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        } catch (Exception e) {
            log.error("TianYuYouSDK verify total exception,the result is " + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        callback.onSuccess("");
        
    }
}
