package com.u8.server.sdk.appstore;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.data.IAPGoodsInfos;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.service.IAPGoodsInfoManager;
import com.u8.server.service.UChannelExchangeManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsfbadmin on 2017/7/7.
 * 已废弃
 */
public class AppStoreSDK  implements ISDKScript {

    //public static final String notify_url = "http://www.wgb.cn:9088/pay/apple/payCallback";
    public static final String notify_url = "http://120.25.243.133:8080/pay/apple/payCallback";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
       //始终返回成功
      callback.onSuccess(new SDKVerifyResult(true, extension,extension,"BSFB_User"));
//        JSONObject json = JSONObject.fromObject(extension);
//        final String uid = json.getString("userid");
//        String sessionid = json.getString("sessionid");
//        final String userName = json.getString("username");
//        callback.onSuccess(new SDKVerifyResult(true, uid,userName,"BSFB_User"));

    }

    public static  void AppStoreGetOrder(UUser user, UOrder order, ISDKOrderListener callback) {
        int pID = Integer.parseInt(order.getProductID());
        Log.i("appStore getOrder goodsInfo's pid:"+pID+", channelID:"+ order.getChannelID());

        ApplicationContext applicationContext = UApplicationContext.getApplicationContext();
        IAPGoodsInfoManager infoService= (IAPGoodsInfoManager) applicationContext.getBean("iAPGoodsInfoManager");
        IAPGoodsInfos info =infoService.queryInfo(pID, order.getChannelID());
        Log.i("appStore getOrder goodsInfo:"+info.toJSON());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid",info.getIAPProductID());
        jsonObject.put("notify_url",notify_url);
        jsonObject.put("orderId",order.getOrderID().toString());

        Log.i("appStore getOrder success,msg:"+jsonObject.toString());
        if(callback != null){
            callback.onSuccess(jsonObject.toString());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        try {
            AppStoreSDK.AppStoreGetOrder(user, order, callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("appStore getOrder exception,msg:"+e.getMessage());
        }

    }
}