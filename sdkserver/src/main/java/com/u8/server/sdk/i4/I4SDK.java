package com.u8.server.sdk.i4;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * iOS i4渠道SDK处理类
 * Created by ant on 2016/2/24.
 */
public class I4SDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, final String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String token = json.getString("token");
            final String userId = json.getString("userId");
            final String userName = json.getString("userName");


            Map<String,String> params = new HashMap<String, String>();
            params.put("token", token);

            String url = channel.getMaster().getAuthUrl();

            SDKVerifyResult vResult = new SDKVerifyResult(true, userId, userName, "");

            callback.onSuccess(vResult);
            //TODO
            /*UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.e("The auth result is " + result);

                        JSONObject jsonResult = JSONObject.fromObject(result);

                        if(jsonResult != null && jsonResult.containsKey("status") && jsonResult.getInt("status") == 0){

                            SDKVerifyResult vResult = new SDKVerifyResult(true, jsonResult.getString("userid"), userId, "");

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

            });*/

        }catch(Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        Integer productPrice = order.getMoney()/100;
        Long orderSerial = order.getOrderID();
        String productName = order.getProductName();
        Integer gameServerId = order.getPlatID();
        Long extInfo = order.getOrderID();
        String channelServerId = "0";
        String roleId = user.getChannelUserID();

        JSONObject json = new JSONObject();
        json.put("productPrice", productPrice.toString());
        json.put("orderSerial", orderSerial.toString());
        json.put("productName", productName);
        json.put("gameServerId", gameServerId.toString());
        json.put("extInfo", extInfo.toString());
        json.put("channelServerId", channelServerId);
        json.put("roleId", roleId);

        if (callback != null) {
            callback.onSuccess(json.toString());
        }
    }
}
