package com.u8.server.sdk.dianyou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.sdk.vivo.VivoOrderResult;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * DianYou SDK
 * Created by ant on 2015/4/23.
 */
public class DianYouSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(DianYouSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            log.info("--------->verify ,The auth request data :" + extension);
            JSONObject jsonObject = JSONObject.fromObject(extension);
            String userId = jsonObject.getString("userid");

            callback.onSuccess(new SDKVerifyResult(true, userId, "", ""));
        } catch (Exception e) {
            log.error("--------->verify exception2:" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (callback != null) {

            try {
                DecimalFormat df=new DecimalFormat("0.00");
                String orderMoney =df.format(order.getMoney()/100.00);

                JSONObject ext = new JSONObject();
                ext.put("orderId", order.getOrderID()+"");
                ext.put("payCallBackUrl", user.getChannel().getMaster().getPayCallbackUrl());
                ext.put("productName", order.getProductName());
                ext.put("productPrice", orderMoney);
                ext.put("productDesc", StringUtils.isEmpty(order.getProductDesc())?"小黄文":order.getProductDesc());
                String extStr = ext.toString();
                log.info("-------->onGetOrderID callBack data:" + extStr);
                callback.onSuccess(extStr);

            } catch (Exception e) {
                log.info("-------->onGetOrderID Exception,msg:" + e.getMessage());
                e.printStackTrace();
                callback.onFailed(e.getMessage());
            }


        }
    }
}
