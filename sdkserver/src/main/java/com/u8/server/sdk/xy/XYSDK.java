package com.u8.server.sdk.xy;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * iOS XY苹果助手
 * Created by ant on 2016/2/24.
 */
public class XYSDK implements ISDKScript {
    @Override
    public void verify(final UChannel channel, final String extension, final ISDKVerifyListener callback) {
        try {

            JSONObject json = JSONObject.fromObject(extension);
            String token = json.getString("token");
            final String userid = json.getString("openuid");


            Map<String, String> params = new HashMap<String, String>();
            params.put("token", token);
            params.put("uid", userid);
            params.put("appid", channel.getCpAppID());

            String url = channel.getMaster().getAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.e("The auth result is " + result);

                        JSONObject jsonResult = JSONObject.fromObject(result);

                        if (jsonResult != null && jsonResult.containsKey("ret") && jsonResult.getInt("ret") == 0) {

                            SDKVerifyResult vResult = new SDKVerifyResult(true, userid, "", "");

                            callback.onSuccess(vResult);

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

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (order != null) {
            JSONObject json = new JSONObject();
            //json.put("amount", order.getMoney());
            json.put("amount", "0.01");
            json.put("serverId", StringUtils.isEmpty(order.getServerID())?"1":order.getServerID().toString());
            json.put("extra", order.getOrderID().toString());

            if (callback != null) {
                callback.onSuccess(json.toString());
            }
        }

    }
}
