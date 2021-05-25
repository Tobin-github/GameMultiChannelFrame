package com.u8.server.sdk.jinli;

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
 * 金立SDK
 * Created by ant on 2015/4/25.
 */
public class SingleJinliSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SingleJinliSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String accesstoken = json.getString("accesstoken");

            log.info("The SingleJinliSDK verify is :" + accesstoken);
            if (!TextUtils.isEmpty(accesstoken)){
                callback.onSuccess(new SDKVerifyResult(true, accesstoken, "",""));
                return;
            }else{
                callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
            }


        }catch (Exception e){
            log.error("------------->The auth exception,msg:"+e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }


    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        if(callback != null){
            log.info("------------->The onGetOrderID success");
            callback.onSuccess("");
        }

    }
}
