package com.u8.server.sdk.mianshangdian;

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
 * 免商店（蜗牛）
 * Created by xiaohei on 15/12/22.
 */
public class MianShangDianSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String uin = json.getString("uin");
            String sessionId = json.getString("sessionId");
            String act = "4";

            StringBuilder sb = new StringBuilder();
            sb.append(channel.getCpAppID()).append(act).append(uin).append(sessionId).append(channel.getCpAppKey());


            String sign = EncryptUtils.md5(sb.toString());

            Map<String,String> params = new HashMap<String, String>();
            params.put("AppId", channel.getCpAppID());
            params.put("Act", act);
            params.put("Uin", uin);
            params.put("SessionId", sessionId);
            params.put("Sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        Log.e("The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("ErrorCode");


                        if (code == 1) {

                            callback.onSuccess(new SDKVerifyResult(true, uin, "", ""));
                            return;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);
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
