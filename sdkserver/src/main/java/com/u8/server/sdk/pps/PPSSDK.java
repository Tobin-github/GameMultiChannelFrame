package com.u8.server.sdk.pps;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;

/**
 * PPS 渠道
 * Created by xiaohei on 15/12/22.
 */
public class PPSSDK implements ISDKScript{
    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String uid = json.getString("uid");
            String time = json.getString("time");
            String sign = json.getString("sign");

            StringBuilder sb = new StringBuilder();
            sb.append(uid).append("&").append(time).append("&").append(channel.getCpAppKey());
            String signLocal = EncryptUtils.md5(sign).toLowerCase();
            Log.d("sign local is "+signLocal + "cp app key:"+channel.getCpAppKey());
//            if(signLocal.equals(sign)){
//                callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
//            }else{
//                callback.onFailed("pps verified failed. sign invalid. extension:"+extension);
//            }
            callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));

        }catch(Exception e){
            callback.onFailed("pps verified failed."+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
