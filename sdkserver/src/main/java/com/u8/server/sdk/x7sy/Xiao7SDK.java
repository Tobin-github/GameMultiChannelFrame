package com.u8.server.sdk.x7sy;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 小7手游 SDK
 * Created by ant on 2018/01/22.
 */
public class Xiao7SDK implements ISDKScript {

    private static Logger log = Logger.getLogger(Xiao7SDK.class.getName());
    
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {

            JSONObject json = JSONObject.fromObject(extension);
            String tokenkey = json.getString("tokenkey");

            String appKey = channel.getCpAppKey();

            StringBuilder sb = new StringBuilder();
            sb.append(appKey).append(tokenkey);

            log.info("---------> unsign str:" + sb.toString());
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            log.info("---------> signed str:" + sign);

            String authUrl = channel.getMaster().getAuthUrl();

            Map<String,String> params = new HashMap<String, String>();
            params.put("tokenkey", tokenkey);
            params.put("sign", sign);

            UHttpAgent.getInstance().post(authUrl, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("---------------> verify result:" + result);
                        JSONObject json = JSONObject.fromObject(result);

                        if (json.containsKey("errorno") && json.getString("errorno").equals("0")) {

                            String data = json.getString("data");
                            //SONObject error = json.getJSONObject("errormsg");

                            JSONObject dataJson = JSONObject.fromObject(data);
                            String guid = dataJson.getString("guid");
                            String username = dataJson.getString("username");

                            SDKVerifyResult vResult = new SDKVerifyResult(true, guid, username, "");

                            callback.onSuccess(vResult);
                            log.info("---------------> verify successful:" + result);
                            return;
                        }else{

                            String errormsg = json.getString("errormsg");
                            log.error("---------------> verify error.msg:"+errormsg);
                        }


                    } catch (Exception e) {
                        log.error("---------------> verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("---------------> verify failed,the result is " + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);

                }

                @Override
                public void failed(String e) {
                    log.error("--------------->SingleSouGouSDK verify callback failed,the result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        } catch (Exception e) {
            log.error("----------------> verify total exception,the result is " + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        try {
            JSONObject json = new JSONObject();
            json.put("orderId", order.getOrderID());

            String sign = generateSign(user, order);
            json.put("sign", sign);

            log.info("---------------> order extension:" + json.toString());
            callback.onSuccess(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("----------------> getOrder exception,the result is " + e.getMessage());
        }

    }

    private String generateSign(UUser user,UOrder order) {

        DecimalFormat df=new DecimalFormat("0.00");
        String orderMoney =df.format(order.getMoney()/100.00);

        log.info("---------------> order money:" + order.getMoney()+",money:"+orderMoney);

        String rsaPubKey = order.getChannel().getCpPayKey();

        String extends_info_data = (order.getOrderID()+"").trim();
        String game_area = order.getServerID().trim();
        String game_guid = user.getChannelUserID().trim();
        String game_level = "1".trim();
        String game_orderid = (order.getOrderID()+"").trim();
        String game_price = orderMoney.trim();
        String game_role_id = order.getRoleID().trim();
        String game_role_name = order.getRoleName().trim();
        String notify_id = "-1".trim();
        String subject = order.getProductDesc().trim();

        StringBuilder sb = new StringBuilder();
        sb.append("extends_info_data=").append(extends_info_data).append("&")
                .append("game_area=").append(game_area).append("&")
                .append("game_guid=").append(game_guid).append("&")
                .append("game_level=").append(game_level).append("&")
                .append("game_orderid=").append(game_orderid).append("&")
                .append("game_price=").append(game_price).append("&")
                .append("game_role_id=").append(game_role_id).append("&")
                .append("game_role_name=").append(game_role_name).append("&")
                .append("notify_id=").append(notify_id).append("&")
                .append("subject=").append(subject)
                .append(rsaPubKey);

        log.info("---------------> order sign params:" + sb.toString());

        return EncryptUtils.md5(sb.toString());
    }
}
