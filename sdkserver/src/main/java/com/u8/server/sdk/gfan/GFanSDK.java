package com.u8.server.sdk.gfan;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 机锋渠道SDK类
 * Created by ant on 2015/12/2.
 */
public class GFanSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            String token = json.getString("token");
            final String name = json.getString("name");
            String t = (System.currentTimeMillis() / 1000) + "";
            Map<String,String> params = new HashMap<String, String>();
            params.put("token", token);


            String url = channel.getChannelAuthUrl();

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", "packageName=,appName=,channelID="+channel.getCpAppKey());

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of gfan verify is :"+result);

                        JSONObject jr = JSONObject.fromObject(result);
                        int resultCode = jr.getInt("resultCode");

                        if(resultCode == 1){

                            callback.onSuccess(new SDKVerifyResult(true, uid, name, ""));
                            return;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
                }

                @Override
                public void failed(String err) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
                }

            });


        }catch (Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
