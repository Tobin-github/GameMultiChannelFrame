package com.u8.server.sdk.cloudpoint;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 友游
 * Created by ant on 2015/4/28.
 */
public class CloudPointSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            final String username = json.getString("nickname");


            Map<String,String> params = new HashMap<String, String>();
            params.put("appid", channel.getCpAppID());
            params.put("uid", uid);

            StringBuilder sb = new StringBuilder();
            sb.append(channel.getCpAppID()).append(channel.getCpAppKey()).append(uid);
            String sign = EncryptUtils.sha1(sb.toString());

            params.put("sign", sign.toUpperCase());

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.e("The auth result is " + result);

                        if("SUCCESS".equals(result)){

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
            callback.onSuccess(order.getChannel().getPayCallbackUrl());
        }

    }
}
