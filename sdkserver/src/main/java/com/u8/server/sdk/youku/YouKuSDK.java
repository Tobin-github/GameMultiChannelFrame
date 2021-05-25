package com.u8.server.sdk.youku;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 优酷SDK
 * Created by ant on 2015/12/8.
 */
public class YouKuSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            String sessionID = extension;

            Map<String,String> params = new HashMap<String, String>();
            params.put("sessionid", sessionID);
            params.put("appkey", channel.getCpAppKey());

            StringBuilder sb = new StringBuilder();
            sb.append("appkey=").append(channel.getCpAppKey()).append("&sessionid=").append(sessionID);
            String sign = EncryptUtils.hmac(sb.toString(), channel.getCpPayID());

            Log.d("the youku verify->sign is %s", sign);
            params.put("sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of youku verify is :" + result);
                        if (!TextUtils.isEmpty(result)){
                            JSONObject jr = JSONObject.fromObject(result);
                            String uid = jr.getString("uid");
                            String ytid = "";
                            if(jr.containsKey("ytid")){
                                ytid = jr.getString("ytid");
                            }
                            callback.onSuccess(new SDKVerifyResult(true, uid, ytid, ""));
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
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }
}
