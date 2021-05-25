package com.u8.server.sdk.chuyou;

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
 * 武汉楚游 07073
 * Created by xiaohei on 15/12/22.
 */
public class ChuYouSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String username = json.getString("username");
            String token = json.getString("token");



            StringBuilder sb = new StringBuilder();
            sb.append("pid=").append(channel.getCpID()).append("&")
                    .append("token=").append(token).append("&")
                    .append("username=").append(username)
                    .append(channel.getCpAppKey());

            String sign = EncryptUtils.md5(sb.toString());

            Map<String,String> params = new HashMap<String, String>();
            params.put("username", username);
            params.put("token", token);
            params.put("pid", channel.getCpID());
            params.put("sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        Log.e("The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("state");


                        if(code == 1){

                            JSONObject rt = json.getJSONObject("data");
                            callback.onSuccess(new SDKVerifyResult(true, rt.getString("uid"), rt.getString("username"), ""));
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
