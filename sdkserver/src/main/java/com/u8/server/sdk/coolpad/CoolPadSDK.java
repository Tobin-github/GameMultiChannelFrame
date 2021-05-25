package com.u8.server.sdk.coolpad;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoolPadSDK暂时只支持消费型_应用传入价格类型的网游应用
 * waresid默认就一个，配置在UChannel的cpConfig字段中
 * Created by ant on 2015/4/8.
 */
public class CoolPadSDK implements ISDKScript{

    private static final String USER_INFO_URL = "https://openapi.coolyun.com/oauth2/api/get_user_info";


    private String getUserInfo(UChannel channel, CoolPadTokenInfo tokenInfo){

        Map<String, String> data = new HashMap<String, String>();
        data.put("access_token", tokenInfo.getAccess_token());
        data.put("oauth_consumer_key", channel.getCpAppID());
        data.put("openid", tokenInfo.getOpenid());

        return UHttpAgent.getInstance().get(USER_INFO_URL, data);

    }

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{
            JSONObject json = JSONObject.fromObject(extension);
            String code =json.getString("code");
            Map<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "authorization_code");
            params.put("client_id", channel.getCpAppID());
            params.put("client_secret", channel.getCpAppKey());
            params.put("code", code);
            params.put("redirect_uri", channel.getCpAppKey());

            UHttpAgent.getInstance().get(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {

                @Override
                public void completed(String content) {

                    try{

                        CoolPadTokenInfo token = (CoolPadTokenInfo)JsonUtils.decodeJson(content, CoolPadTokenInfo.class);

                        String userInfoString = getUserInfo(channel, token);

                        Log.d("The userInfo String is "+userInfoString);

                        CoolPadUserInfo user = (CoolPadUserInfo)JsonUtils.decodeJson(userInfoString, CoolPadUserInfo.class);

                        if(user != null && "0".equals(user.getRtn_code())){

                            JSONObject json = new JSONObject();
                            json.put("access_token", token.getAccess_token());
                            json.put("openid", token.getOpenid());
                            json.put("expiredIn", token.getExpires_in());
                            json.put("refreshToken", token.getRefresh_token());
                            String ext = json.toString();

                            SDKVerifyResult verifyResult = new SDKVerifyResult(true, token.getOpenid(), user.getNickname(), user.getNickname(), ext);
                            callback.onSuccess(verifyResult);
                            return;
                        }

                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + content);



                    }catch (Exception e){
                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + content+";"+e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void failed(String e) {

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);                }

            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onGetOrderID(UUser user, final UOrder order, final ISDKOrderListener callback) {
        String notifyUrl=UHttpAgent.ServerHost + "/pay/coolpad/payCallback";

        JSONObject json = new JSONObject();
        json.put("orderNo", order.getOrderID().toString());
        json.put("OrderMoney", order.getMoney().toString());
        json.put("ProID", order.getProductID());
        json.put("NotifyUrl", notifyUrl);

        if (callback != null) {
            callback.onSuccess(json.toString());
        }

    }
}
