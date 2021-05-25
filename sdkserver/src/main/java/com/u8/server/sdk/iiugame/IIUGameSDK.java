package com.u8.server.sdk.iiugame;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.data.IAPGoodsInfos;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.service.IAPGoodsInfoManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class IIUGameSDK implements ISDKScript {
    private static String CHARSET = "utf-8";

    private static Logger log = Logger.getLogger(IIUGameSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try {
            JSONObject json = JSONObject.fromObject(extension);
            String userId = json.getString("userID");
            String gameId = json.getString("Ugameid");
            log.info("---------->IIUGameSDK verify, json:"+json);
            callback.onSuccess(new SDKVerifyResult(true, userId, "", ""));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("---------->IIUGameSDK verifyexception");
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try {
            int pID = Integer.parseInt(order.getProductID());
            String notify_url = order.getChannel().getMaster().getPayCallbackUrl();
            if (1 == order.getChannel().getPlatID()) {
                callback.onSuccess("");
                Log.i("iiugameSDK Android getOrder success");
            } else {
                log.info("---------->IIUGameSDK onGetOrderID, pID:"+pID+", notify_url:"+notify_url+", channelId:"+order.getChannelID());
                ApplicationContext applicationContext = UApplicationContext.getApplicationContext();
                IAPGoodsInfoManager infoService= (IAPGoodsInfoManager) applicationContext.getBean("iAPGoodsInfoManager");
                IAPGoodsInfos info =infoService.queryInfo(pID, order.getChannelID());
                log.info("---------->IIUGameSDK onGetOrderID, info:"+info.toJSON());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("productId",info.getIAPProductID());
                jsonObject.put("notify_url",notify_url);
                jsonObject.put("orderId",order.getOrderID().toString());

                Log.i("iiugameSDK getOrder success,msg:"+jsonObject.toString());
                if(callback != null){
                    callback.onSuccess(jsonObject.toString());
                }
            }



        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            Log.e("get order extension fail:" + e.getMessage());
            e.printStackTrace();
        }
    }


}
