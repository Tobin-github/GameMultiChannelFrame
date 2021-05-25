package com.u8.server.sdk.meizu;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 魅族SDK
 * Created by ant on 2015/4/30.
 */
public class SingleMeizuSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SingleMeizuSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{
            log.info("--------->verify ,request extension::" +extension);
            JSONObject json = JSONObject.fromObject(extension);

            String app_id = channel.getCpAppID();

            String uid = json.getString("mUID");
            String session_id = json.getString("mSession");
            String name = json.getString("mName");
            String ts = "" + System.currentTimeMillis();
            String sign_type = "md5";

            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(app_id).append("&")
                    .append("session_id=").append(session_id).append("&")
                    .append("ts=").append(ts).append("&")
                    .append("uid=").append(uid).append(":").append(channel.getCpAppSecret());

            log.info("-------->verify ,request unSignStr:" +sb.toString());

            String sign = EncryptUtils.md5(sb.toString());
            log.info("-------->verify ,request signStr:" +sign);

            Map<String,String> params = new HashMap<String, String>();
            params.put("app_id", app_id);
            params.put("session_id", session_id);
            params.put("uid", uid);
            params.put("ts", ts);
            params.put("sign_type",sign_type);
            params.put("sign", sign);

            String url = channel.getMaster().getAuthUrl();
            log.info("-------->verify ,request params:" +params.toString()+", url:"+url);

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("--------->verify ,The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("code");


                        if(code == 200){

                            callback.onSuccess(new SDKVerifyResult(true, uid, name, name));
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

        }catch (Exception e){
            log.error("-------->verify exception2:" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        try {
            if (callback != null) {
                log.info("--------->onGetOrderID successful");
                callback.onSuccess("");
            }
        } catch (Exception e) {
            log.error("-------->onGetOrderID exception:" + e.getMessage());
            e.printStackTrace();
        }

    }
}
