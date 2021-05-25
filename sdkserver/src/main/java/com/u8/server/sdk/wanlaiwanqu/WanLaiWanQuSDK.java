package com.u8.server.sdk.wanlaiwanqu;

import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WanLaiWanQuSDK implements ISDKScript {
    private static String CHARSET = "utf-8";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try {
            String appid = channel.getCpAppID();
            String sdkversion = "1.0.1";
            String sessionid = extension;
            long time = System.currentTimeMillis();

            StringBuilder param = new StringBuilder();
            param.append("appid=").append(appid).
                    append("&sdkversion=").append(sdkversion).
                    append("&sessionid=").append(sessionid).
                    append("&time=").append(time).
                    append(channel.getCpAppKey());

            String sign = EncryptUtils.md5(param.toString());

            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", appid);
            params.put("sdkversion", sdkversion);
            params.put("sessionid", sessionid);
            params.put("time", time + "");
            params.put("sign", sign);


            String url = channel.getMaster().getAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of yile verify is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            int state = jr.getInt("state");

                            if (state == 0) {
                                String rows = jr.getString("rows");
                                JSONObject user = JSONObject.fromObject(rows);
                                String id = user.getString("id");
                                String nickname = user.getString("nickname");
                                callback.onSuccess(new SDKVerifyResult(true, id, nickname, ""));
                                return;
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
                }

                @Override
                public void failed(String err) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try {
            String cporderid = order.getOrderID() + "";
            String appid = order.getChannel().getCpAppID();
            String userid = order.getUserID() + "";
            String time = System.currentTimeMillis()+"";
            String extension = URLEncoder.encode("啊啊啊" + "", "UTF-8");
            String price = order.getMoney() + "";
            //String gamearea = URLEncoder.encode(order.getServerName(), "UTF-8");
            String gamearea = URLEncoder.encode("王者荣耀", "UTF-8");
            //String roleid = order.getRoleID();
            String roleid = "1";
            //String rolename = URLEncoder.encode(order.getRoleName(), "UTF-8");
            String rolename = URLEncoder.encode("嘿嘿", "UTF-8");
            String rolelevel = "999";
            String channel = order.getChannel().getCpConfig();
            String httpurl = UHttpAgent.ServerHost + "/pay/wanlaiwanqu/payCallback";

            StringBuilder param = new StringBuilder();
            param.append("cporderid=").append(cporderid).
                    append("&appid=").append(appid).
                    append("&userid=").append(userid).
                    append("&time=").append(time).
                    append("&extension=").append(extension).
                    append("&price=").append(price).
                    append("&gamearea=").append(gamearea).
                    append("&roleid=").append(roleid).
                    append("&rolename=").append(rolename).
                    append("&rolelevel=").append(rolelevel).
                    append("&channel=").append(channel).
                    append("&httpurl=").append(httpurl).
                    append(order.getChannel().getCpAppSecret());

            String sign = EncryptUtils.md5(param.toString());


            Map<String, String> params = new HashMap<String, String>();
            params.put("cporderid", cporderid);
            params.put("appid", appid);
            params.put("userid", userid);
            params.put("time", time+"");
            params.put("extension", extension);
            params.put("price", price);
            params.put("gamearea", gamearea);
            params.put("roleid", roleid);
            params.put("rolename", rolename);
            params.put("rolelevel", rolelevel);
            params.put("channel", channel);
            params.put("httpurl", httpurl);
            params.put("sign", sign);

            String url = order.getChannel().getMaster().getOrderUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of order is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            int state = jr.getInt("state");

                            if (state == 0) {
                                String rows = jr.getString("rows");
                                JSONObject user = jr.getJSONObject(rows);
                                String pono = user.getString("pono");
                                Log.i("get order extension success:" + pono.toString());
                                callback.onSuccess(pono);
                                return;
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed("get order fail");
                }

                @Override
                public void failed(String err) {
                    callback.onFailed("get order fail");
                }

            });


        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            Log.e("get order extension fail:" + e.getMessage());
            e.printStackTrace();
        }
    }


}
