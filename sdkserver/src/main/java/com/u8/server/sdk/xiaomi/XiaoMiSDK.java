package com.u8.server.sdk.xiaomi;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.HmacSHA1Encryption;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 小米SDK
 * Created by ant on 2015/4/21.
 */
public class XiaoMiSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(XiaoMiSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            final String session = json.getString("session");

            Map<String, String> params = new HashMap<String, String>();
            params.put("appId", channel.getCpAppID());
            params.put("session", session);
            params.put("uid", uid);

            StringBuilder sb = new StringBuilder();
            sb.append("appId=").append(channel.getCpAppID()).append("&")
                    .append("session=").append(session).append("&")
                    .append("uid=").append(uid);

            String signature = HmacSHA1Encryption.HmacSHA1Encrypt(sb.toString(), channel.getCpAppSecret());

            params.put("signature", signature);

            log.info("The xiaomi auth requestParam:"+params.toString()+", AppSecret:"+channel.getCpAppSecret());

            UHttpAgent.getInstance().get(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {
                @Override
                public void completed(String content) {

                    log.info("The xiaomi auth result is "+content);
                    try{

                        AuthInfo info = (AuthInfo)JsonUtils.decodeJson(content, AuthInfo.class);
                        if(info != null && info.getErrcode() == 200){
                            SDKVerifyResult vResult = new SDKVerifyResult(true, uid+"", "", "");
                            callback.onSuccess(vResult);
                            return;
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the auth result is " + content);
                }

                @Override
                public void failed(String e) {
                    log.error("The xiaomi auth result fail,msg: " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });

        }catch (Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        JSONObject json = new JSONObject();

        json.put("orderId", order.getOrderID()+"");
        json.put("cpUserInfo", order.getExtension());
        json.put("amount", order.getMoney()/100);

        json.put("balance", 1);
        json.put("vipLevel", 1);
        json.put("roleLevel", 1);
        json.put("union", "德玛西亚");
        json.put("roleName", StringUtils.isEmpty(order.getRoleName())?"kris":order.getRoleName());
        json.put("roleId", StringUtils.isEmpty(order.getRoleID())?"1":order.getRoleID());
        json.put("serverName", StringUtils.isEmpty(order.getServerName())?"王者荣耀":order.getServerName());

        if (callback != null) {
            callback.onSuccess(json.toString());
        }
    }
}
