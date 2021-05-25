package com.u8.server.sdk.sougou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜狗SDK
 * Created by ant on 2016/5/10.
 */
public class SingleSouGouSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SingleSouGouSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String sessionKey = json.getString("sessionKey");
            final String userID = json.getString("userID");

            StringBuilder sb = new StringBuilder();
            sb.append("gid=").append(URLEncoder.encode(channel.getCpAppID(), "UTF-8"))
                    .append("&session_key=").append(URLEncoder.encode(sessionKey, "UTF-8"))
                    .append("&user_id=").append(URLEncoder.encode(userID, "UTF-8"))
                    .append("&"+channel.getCpAppSecret());
            Log.d("SingleSouGouSDK sign str:" + sb.toString());
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            log.info("--------------->SingleSouGouSDK signed str:" + sign);
            Map<String,String> params = new HashMap<String, String>();
            params.put("gid", channel.getCpAppID());
            params.put("session_key", sessionKey);
            params.put("user_id", userID);
            params.put("auth", sign);


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("--------------->SingleSouGouSDK verify result:" + result);
                        JSONObject json = JSONObject.fromObject(result);

                        if (json.containsKey("result") && json.getBoolean("result")) {
                            SDKVerifyResult vResult = new SDKVerifyResult(true, userID, "", "");

                            callback.onSuccess(vResult);
                            log.info("--------------->SingleSouGouSDK verify successful:" + result);
                            return;
                        }else{

                            JSONObject error = json.getJSONObject("error");
                            int code = error.getInt("code");
                            String msg = error.getString("message");
                            log.error("--------------->sougou verify error.code:"+code+";msg:"+msg);
                        }


                    } catch (Exception e) {
                        log.error("--------------->SingleSouGouSDK verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("--------------->SingleSouGouSDK verify failed,the result is " + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);

                }

                @Override
                public void failed(String e) {
                    log.error("--------------->SingleSouGouSDK verify callback failed,the result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        }catch(Exception e){
            log.error("--------------->SingleSouGouSDK verify total exception,the result is " + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        JSONObject json = new JSONObject();
        json.put("product_name",order.getProductName());
        json.put("amount",order.getMoney()/100+"");
        json.put("orderNo",order.getOrderID()+"");
        json.put("currency","金币");

        if (callback != null) {
            log.info("--------------->SingleSouGouSDK onGetOrderID,the json is " + json.toString());
            callback.onSuccess(json.toString());
        }
    }
}
