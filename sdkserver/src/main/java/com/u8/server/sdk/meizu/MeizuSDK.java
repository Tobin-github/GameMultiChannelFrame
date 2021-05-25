package com.u8.server.sdk.meizu;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 魅族SDK
 * Created by ant on 2015/4/30.
 */
public class MeizuSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(MeizuSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{
            log.info("-------->verify ,request extension::" +extension);
            JSONObject json = JSONObject.fromObject(extension);

            String app_id = channel.getCpAppID();

            final String uid = json.getString("openid");
            String session_id = json.getString("token");
            String name = json.getString("name");
            String ts = "" + System.currentTimeMillis();
            String sign_type = "md5";

            StringBuilder sb = new StringBuilder();
            sb.append("app_id=").append(app_id).append("&")
                    .append("session_id=").append(session_id).append("&")
                    .append("ts=").append(ts).append("&")
                    .append("uid=").append(uid).append(":").append(channel.getCpAppSecret());

            log.info("-------->verify ,request unSignStr:" +sb.toString());

            String sign = EncryptUtils.md5(sb.toString());
            log.info("-------->verify ,request signStr:" +sign);

            Map<String,String> params = new HashMap<String, String>();
            params.put("app_id", app_id);
            params.put("session_id", session_id);
            params.put("uid", uid);
            params.put("ts", ts);
            params.put("sign_type",sign_type);
            params.put("sign", sign);

            String url = channel.getMaster().getAuthUrl();
            log.info("-------->verify ,request params:" +params.toString()+", url:"+url);

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        log.info("-------->verify ,The auth result is " + result);

                        JSONObject json = JSONObject.fromObject(result);
                        int code = json.getInt("code");


                        if(code == 200){

                            callback.onSuccess(new SDKVerifyResult(true, uid, name, name));
                            return;
                        }

                    } catch (Exception e) {
                        log.error("-------->verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    log.error("-------->verify fail,sdk's name:" + channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                }

                @Override
                public void failed(String e) {
                    log.error("-------->verify fail2,sdk's name:" + channel.getMaster().getSdkName() + " verify failed. the post result is " + e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });


        }catch (Exception e){
            log.error("-------->verify exception2:" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        String cp_order_id = order.getOrderID()+"";
        int buy_amount = 1; // buy_amount
        String user_info = user.getName(); // user_info

        DecimalFormat df=new DecimalFormat("0.00");
        String total_price =df.format(order.getMoney()/100.00);
        String product_id = order.getProductID(); // product_id
        String product_subject = order.getProductName(); // product_subject
        String product_unit = "个"; // product_unit
        String product_body = "购买"+buy_amount+product_unit+order.getProductName(); // product_body
        String app_id = order.getChannel().getCpAppID()+""; // app_id (不能为空)
        String uid = user.getChannelUserID(); // uid (不能为空)
        String product_per_price = total_price; // product_per_price
        //String product_per_price = "12"; // product_per_price
        long create_time = System.currentTimeMillis(); // create_time
        int pay_type = 0  ; // pay_type

        String sign_type = "md5";

        StringBuilder sb = new StringBuilder();

        sb.append("app_id=").append(app_id).append("&").append("buy_amount=").append(buy_amount).append("&")
                .append("cp_order_id=").append(cp_order_id).append("&").append("create_time=").append(create_time)
                .append("&").append("pay_type=").append(pay_type).append("&").append("product_body=")
                .append(product_body).append("&").append("product_id=").append(product_id).append("&")
                .append("product_per_price=").append(product_per_price).append("&").append("product_subject=")
                .append(product_subject).append("&").append("product_unit=").append(product_unit).append("&")
                .append("total_price=").append(total_price).append("&").append("uid=").append(uid).append("&")
                .append("user_info=").append(user_info).append(":").append(order.getChannel().getCpAppSecret());

        log.info("-------->onGetOrderID , unSignStr:" + sb.toString());
        String sign = EncryptUtils.md5(sb.toString());
        log.info("-------->onGetOrderID , signStr:" + sign);

        JSONObject json = new JSONObject();
        json.put("cp_order_id", cp_order_id);//3
        json.put("buy_amount", buy_amount);//2
        json.put("user_info", user_info);//13
        json.put("total_price", total_price);//11
        json.put("product_id", product_id);//7
        json.put("product_subject", product_subject);//9
        json.put("product_body", product_body);//6
        json.put("product_unit", product_unit);//10
        json.put("app_id", app_id);//1
        json.put("uid", uid);//12
        json.put("product_per_price", product_per_price);//8
        json.put("create_time", create_time);//4
        json.put("pay_type", pay_type);//5
        json.put("sign_type", "md5");
        json.put("sign", sign);
        if (callback != null) {
            log.info("-------->onGetOrderID successful, response data:" + json.toString());
            callback.onSuccess(json.toString());
        }
    }
}
