package com.u8.server.sdk.oppo;

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
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class SingleOppo implements ISDKScript {

    private static Logger log = Logger.getLogger(SingleOppo.class.getName());

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
                callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("---------->The verify result exception ,msg:" + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try{
            if(callback != null){
                log.info("---------->The onGetOrderID success");

                JSONObject json = new JSONObject();
                json.put("orderID", order.getOrderID());

                callback.onSuccess(json.toString());
            }
        } catch (Exception e){
            log.error("---------->The onGetOrderID result exception ,msg:" + e.getMessage());
            e.printStackTrace();
        }

    }

}
