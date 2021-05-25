package com.u8.server.sdk.IOSGuest;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class IOSGuest implements ISDKScriptExt {

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        JSONObject json = JSONObject.fromObject(extension);
        final String accesstoken = json.getString("accesstoken");

        //为宝石平台做适配
        if (1729==channel.getChannelID()) {

            final String cpCode = json.getString("CPCode");
            Log.d("The result of yile verify is :" + accesstoken);

            /*JSONObject responseJson = new JSONObject();
            responseJson.put("accesstoken", accesstoken);
            responseJson.put("cpCode", cpCode);*/

            if (!TextUtils.isEmpty(accesstoken)) {
                callback.onSuccess(new SDKVerifyResult(true, accesstoken, "", "",cpCode));
                return;
            } else {
                callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
            }
        }

        Log.d("The result of yile verify is :" + accesstoken);
        if (!TextUtils.isEmpty(accesstoken)){
            callback.onSuccess(new SDKVerifyResult(true, accesstoken, "",""));
            return;
        }else{
            callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try{

        }
        catch (Exception e){

        }
        if(callback != null){
            callback.onSuccess("");
        }
    }

    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback){

    }

    //根据宝石风暴的参数 payType = 2 是为微信支付  payType = 1 是阿里支付
    public void onGetOrderIDByType(UUser user, UOrder order,int payType, UChannelPayTypeManager manager, ISDKOrderListener callback){
        this.onGetOrderID(user,order,callback);
    }

}
