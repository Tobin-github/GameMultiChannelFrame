package com.u8.server.sdk.yile;

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
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class YiLeSDK implements ISDKScript {

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String account = json.getString("account");
            final String token = json.getString("token");



            Map<String,String> params = new HashMap<String, String>();
            params.put("account", account);
            params.put("token", token);
            params.put("appid", channel.getCpAppID());


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of yile verify is :" + result);
                        if (!TextUtils.isEmpty(result)){
                            JSONObject jr = JSONObject.fromObject(result);
                            int code = jr.getInt("errorcode");
                            if(code == 1){
                                callback.onSuccess(new SDKVerifyResult(true, account, "", ""));
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
