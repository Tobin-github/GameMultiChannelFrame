package com.u8.server.sdk.coolpad;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * CoolPadSDK暂时只支持消费型_应用传入价格类型的网游应用
 * waresid默认就一个，配置在UChannel的cpConfig字段中
 * Created by ant on 2015/4/8.
 */
public class SingleCoolPadSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SingleCoolPadSDK.class.getName());

    private static final String USER_INFO_URL = "https://openapi.coolyun.com/oauth2/api/get_user_info";


    private String getUserInfo(UChannel channel, CoolPadTokenInfo tokenInfo){

        Map<String, String> data = new HashMap<String, String>();
        data.put("access_token", tokenInfo.getAccess_token());
        data.put("oauth_consumer_key", channel.getCpAppID());
        data.put("openid", tokenInfo.getOpenid());

        log.info("-------->The getUserInfo params:"+data.toString());

        return UHttpAgent.getInstance().get(USER_INFO_URL, data);

    }

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{
            JSONObject json = JSONObject.fromObject(extension);
            String code =json.getString("AuthCode");
            Map<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "authorization_code");
            params.put("client_id", channel.getCpAppID());
            params.put("client_secret", channel.getCpAppKey());
            params.put("code", code);
            params.put("redirect_uri", channel.getCpAppKey());

            log.info("-------->verify json:" + params.toString()+",authUrl:"+channel.getChannelAuthUrl());

            UHttpAgent.getInstance().get(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {

                @Override
                public void completed(String content) {

                    try{

                        CoolPadTokenInfo token = (CoolPadTokenInfo)JsonUtils.decodeJson(content, CoolPadTokenInfo.class);

                        String userInfoString = getUserInfo(channel, token);

                        log.info("-------->The userInfo String is "+userInfoString);

                        CoolPadUserInfo user = (CoolPadUserInfo)JsonUtils.decodeJson(userInfoString, CoolPadUserInfo.class);
                        log.info("-------->The verify code:"+user.getRtn_code());

                        if(user != null && "0".equals(user.getRtn_code())){
                            JSONObject json = new JSONObject();
                            json.put("accessToken", token.getAccess_token());
                            json.put("openId", token.getOpenid());
                            json.put("expiredIn", token.getExpires_in());
                            json.put("refreshToken", token.getRefresh_token());
                            String ext = json.toString();
                            log.info("-------->The verify json:"+ext);

                            SDKVerifyResult verifyResult = new SDKVerifyResult(true, token.getOpenid(), user.getNickname(), user.getNickname(), ext);
                            callback.onSuccess(verifyResult);
                            return;
                        }
                        log.error("-------->The verify fail,msg:"+content);
                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + content);



                    }catch (Exception e){
                        log.error("-------->The verify exception,msg:"+e.getMessage());
                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + content+";"+e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void failed(String e) {
                    log.error("-------->The verify fail-2,msg:"+e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);                }

            });

        }catch (Exception e){
            log.error("-------->The verify exception-2,msg:"+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onGetOrderID(UUser user, final UOrder order, final ISDKOrderListener callback) {
        String notifyUrl = user.getChannel().getMaster().getPayCallbackUrl();

        JSONObject json = new JSONObject();
        json.put("orderNo", order.getOrderID().toString());
        json.put("orderMoney", order.getMoney().toString());
        json.put("proID", order.getProductID());
        json.put("notifyUrl", notifyUrl);

        log.error("-------->The onGetOrderID json"+json.toString());
        if (callback != null) {
            callback.onSuccess(json.toString());
        }

    }
}
