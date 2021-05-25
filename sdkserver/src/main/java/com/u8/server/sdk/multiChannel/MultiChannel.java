package com.u8.server.sdk.multiChannel;

import com.u8.server.data.*;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;
import net.sf.json.JSONObject;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class MultiChannel implements ISDKScriptExt {

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        JSONObject json = JSONObject.fromObject(extension);
        final String loginType = json.getString("loginType");

        if (loginType!=null&&loginType!=""){
            UChannelloginType info = new UChannelLoginTypeManager().queryInfo(channel.getChannelID(), Integer.parseInt(loginType));

            try {
                ISDKScript verifier = (ISDKScript)Class.forName(info.getLoginScriptClass()).newInstance();
                if(verifier == null){
                    Log.e("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    callback.onFailed("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    return;
                }
                verifier.verify(channel, extension,callback);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

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
         JSONObject json = JSONObject.fromObject(extension);
        final String loginType = json.getString("loginType");

        if (loginType!=null&&loginType!=""){
            UChannelloginType info = manager.queryInfo(channel.getChannelID(), Integer.parseInt(loginType));

            try {
                ISDKScript verifier = (ISDKScript)Class.forName(info.getLoginScriptClass()).newInstance();
                if(verifier == null){
                    Log.e("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    callback.onFailed("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    return;
                }
                verifier.verify(channel, extension,callback);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public void onGetOrderIDByType(UUser user, UOrder order,int payType, UChannelPayTypeManager manager, ISDKOrderListener callback){

            UChannelpayType info = manager.queryInfo(order.getChannel().getChannelID(), payType);
             UChannel channel = order.getChannel();
            try {
                ISDKScript script = (ISDKScript)Class.forName(info.getPayScriptClass()).newInstance();
                if(script == null){
                    Log.e("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    callback.onFailed("the ISDKScript is not found . channelID:"+channel.getChannelID());
                    return;
                }
                script.onGetOrderID(user, order,callback);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

    }


}
