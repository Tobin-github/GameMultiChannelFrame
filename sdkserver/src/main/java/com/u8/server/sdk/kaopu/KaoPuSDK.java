package com.u8.server.sdk.kaopu;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 靠谱助手SDK
 * Created by xiaohei on 16/10/16.
 */
public class KaoPuSDK implements ISDKScript {


    private static Logger log = Logger.getLogger(KaoPuSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String username = json.getString("username");
            final String openid = json.getString("openid");
            final String iconurl = json.getString("iconurl");
            final String description = json.getString("description");
            final String token = json.getString("token");
            final String tag = json.getString("tag");
            final String tagid = json.getString("tagid");
            final String appid = json.getString("appid");
            final String channelKey = json.getString("channelKey");
            final String r = json.getString("r");
            final String devicetype = json.getString("devicetype");
            final String imei = json.getString("imei");
            final String sign = json.getString("sign");
            final String version = json.getString("version");
            final String verifyurl = json.getString("verifyurl");

            log.info("------------> the kaopu verify url:"+verifyurl);
            String timespans = System.currentTimeMillis()/1000+"";

            UHttpAgent httpClient = UHttpAgent.getInstance();

            Map<String,String> params = new HashMap<String, String>();
            params.put("appid",appid);
            params.put("channelKey",channelKey);
            params.put("devicetype",devicetype);
            params.put("imei",imei);
            params.put("openid",openid);
            params.put("r",r);
            params.put("tag",tag);
            params.put("tagid",tagid);
            params.put("token",token);
            params.put("timespans",timespans);
            params.put("sign",sign);

            log.info("------------> the kaopu params:"+params.toString());

            httpClient.post(verifyurl, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    log.info("The auth result is " + result);

                    JSONObject json = JSONObject.fromObject(result);

                    if(json.containsKey("code") && json.getInt("code") == 1){

                        callback.onSuccess(new SDKVerifyResult(true, openid, username, ""));
                        return;
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);

                }

                @Override
                public void failed(String e) {

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        }catch (Exception e){
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            log.error(e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }

}
