package com.u8.server.sdk.liebao;

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
 * 猎宝游戏
 * Created by xiaohei on 15/12/21.
 */
public class LiebaoSDK implements ISDKScript{
    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

        try{

            Log.d("the ext is "+extension);

            JSONObject json = JSONObject.fromObject(extension);

            final String username = json.getString("username");
            String loginTime = json.getString("logintime");
            String sign = json.getString("sign");

            StringBuilder sb = new StringBuilder();
            sb.append("username=").append(username).append("&appkey=")
                    .append(channel.getCpAppKey()).append("&logintime=").append(loginTime);

            String signLocal = EncryptUtils.md5(sb.toString()).toLowerCase();

            if(signLocal.equals(sign)){
                callback.onSuccess(new SDKVerifyResult(true, username, "", ""));
            }else{
                callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the extension is "+extension);
            }


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
