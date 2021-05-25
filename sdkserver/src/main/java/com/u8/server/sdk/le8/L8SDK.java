package com.u8.server.sdk.le8;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 乐8SDK
 * Created by ant on 2016/7/6.
 */
public class L8SDK implements ISDKScript {


    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{


            JSONObject json = JSONObject.fromObject(extension);

            final String uid = json.getString("username");
            final String token = json.getString("token");

            UHttpAgent httpClient = UHttpAgent.getInstance();

            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", channel.getCpAppID());
            params.put("t", token);
            params.put("uid", uid);


            httpClient.post(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    Log.d("The auth result is " + result);

                    if("success".equals(result)){
                        callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
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
            Log.e(e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
