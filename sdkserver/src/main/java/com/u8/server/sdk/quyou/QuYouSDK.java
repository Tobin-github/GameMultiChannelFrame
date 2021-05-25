package com.u8.server.sdk.quyou;

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
 * 趣游
 * Created by xiaohei on 15/12/22.
 */
public class QuYouSDK implements ISDKScript {
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String uid = json.getString("uid");
            String sessionId = json.getString("session");
            String category = "user";
            String outcome = "verify";
            String subset = "10";

            String sign_type = "md5";

            StringBuilder sb = new StringBuilder();
            sb.append("appkey=").append(channel.getCpAppKey())
                    .append("&category=").append(category)
                    .append("&outcome=").append(outcome)
                    .append("&session=").append(sessionId)
                    .append("&subset=").append(subset)
                    .append("&uid=").append(uid)
                    .append("&key=").append(channel.getCpAppSecret());


            String sign = EncryptUtils.md5(sb.toString());

            Map<String,String> params = new HashMap<String, String>();
            params.put("category", category);
            params.put("outcome", outcome);
            params.put("subset", subset);
            params.put("uid", uid);
            params.put("session", sessionId);
            params.put("appkey", channel.getCpAppKey());
            params.put("signtype", sign_type);
            params.put("sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        Log.e("The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("code");


                        if (code == 1) {

                            callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
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
