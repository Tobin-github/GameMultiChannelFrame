package com.u8.server.sdk.uc;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

/**
 * Created by ant on 2014/12/12.
 */
public class SingleUCSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(SingleUCSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{
            log.info("---------->The extension:" + extension);
            JSONObject json = JSONObject.fromObject(extension);
            final String accesstoken = json.getString("accesstoken");

            if (!TextUtils.isEmpty(accesstoken)){
                log.info("---------->The verify result success ,accesstoken:" + accesstoken);
                callback.onSuccess(new SDKVerifyResult(true, accesstoken, "",""));
                return;
            }else{
                log.error("---------->The verify result fail ,msg:" + extension);
                callback.onFailed(channel.getMaster().getSdkName() + " guest verify fail. the get result is " + accesstoken);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("---------->The verify result exception ,msg:" + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        try {
            if (callback != null) {
                JSONObject json = new JSONObject();
                json.put("orderID", order.getOrderID());

                callback.onSuccess(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}