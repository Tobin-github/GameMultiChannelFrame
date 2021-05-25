package com.u8.server.sdk.pptv;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * PPTV
 * Created by xiaohei on 15/12/23.
 */
public class PPTVSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String uid = json.getString("uid");
            String sessionId = json.getString("sessionId");
            final String username = json.getString("username");

            Map<String,String> params = new HashMap<String, String>();
            params.put("type", "login");
            params.put("app", "mobgame");
            params.put("sessionid", sessionId);
            params.put("username", username);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        Log.e("The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("status");


                        if (code == 1) {

                            callback.onSuccess(new SDKVerifyResult(true, uid, username, ""));
                            return;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                }

                @Override
                public void failed(String e) {
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
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
