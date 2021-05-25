package com.u8.server.sdk.kugou;

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
 * 酷狗SDK
 * Created by ant on 2015/12/8.
 */
public class KuGouSDK implements ISDKScript{

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);
            String token = json.getString("token");
            final String uid = json.getString("uid");
            final String username = json.getString("username");

            Map<String,String> params = new HashMap<String, String>();
            params.put("token", token);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of kugou verify is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            JSONObject res = jr.getJSONObject("response");
                            int code = res.getInt("code");

                            if(code == 0){
                                callback.onSuccess(new SDKVerifyResult(true, uid, username, ""));
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
            callback.onSuccess("");
        }
    }

}
