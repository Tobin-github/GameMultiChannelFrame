package com.u8.server.sdk.xiaoqi;

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
 * 小七SDK
 * Created by ant on 2016/11/19.
 */
public class XiaoQiSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            String tokenKey = extension;

            Map<String,String> params = new HashMap<String, String>();
            params.put("tokenKey", tokenKey);

            String sign = EncryptUtils.md5(channel.getCpAppKey()+tokenKey).toLowerCase();

            params.put("sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of xiaoqi verify is :" + result);
                        if (!TextUtils.isEmpty(result)){
                            JSONObject jr = JSONObject.fromObject(result);

                            if(jr.containsKey("errorno") && jr.getInt("errorno") == 0){

                                JSONObject data = jr.getJSONObject("data");
                                String guid = data.getString("guid");
                                String username = data.getString("username");

                                callback.onSuccess(new SDKVerifyResult(true, guid, username, ""));
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
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }
}
