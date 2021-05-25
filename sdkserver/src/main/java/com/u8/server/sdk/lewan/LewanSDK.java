package com.u8.server.sdk.lewan;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 乐玩SDK
 * Created by ant on 2015/12/8.
 */
public class LewanSDK implements ISDKScript{

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);
            String token = json.getString("token");
            final String code = json.getString("code");
            final String channelID = json.getString("channelId");
            final String password = json.getString("password");

            JSONObject notifyData = new JSONObject();
            notifyData.put("app_id", channel.getCpAppID());
            notifyData.put("code", code);
            notifyData.put("password", password);
            notifyData.put("token", token);
            notifyData.put("channelId", channelID);

            Map<String,String> params = new HashMap<String, String>();
            params.put("notifyData", notifyData.toString());

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of lewan verify is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            boolean suc = jr.getBoolean("success");

                            if(suc){
                                callback.onSuccess(new SDKVerifyResult(true, jr.getString("userId"), code, ""));
                                return;
                            }


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
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }
}
