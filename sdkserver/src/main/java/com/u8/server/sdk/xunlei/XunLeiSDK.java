package com.u8.server.sdk.xunlei;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import net.sf.json.JSONObject;

/**
 * 迅雷SDK 没有登录认证接口
 * Created by ant on 2016/12/7.
 */
public class XunLeiSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String username = json.getString("username");
            final String uid = json.getString("uid");


            callback.onSuccess(new SDKVerifyResult(true, uid, username, ""));


        }catch (Exception e){
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            Log.e(e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
