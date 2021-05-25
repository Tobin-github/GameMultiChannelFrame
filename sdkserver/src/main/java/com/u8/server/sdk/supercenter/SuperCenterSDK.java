package com.u8.server.sdk.supercenter;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * 中超SDK
 * Created by ant on 2016/5/10.
 */
public class SuperCenterSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SuperCenterSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String userName = json.getString("username");
            String loginTime = json.getString("logintime");
            String sign = json.getString("sign");

            StringBuilder sb = new StringBuilder();
            sb.append("username=").append(userName)
                    .append("&appkey=").append(channel.getCpAppKey())
                    .append("&logintime=").append(loginTime);

            log.info("--------------->SuperCenterSDK channelSign:"+sign+",sign str:" + sb.toString());
            String MySign = EncryptUtils.md5(sb.toString()).toLowerCase();

            log.info("--------------->SuperCenterSDK signed str:" + MySign);

            if (!MySign.equals(sign)) {
                log.error("--------------->SuperCenterSDK  verify execute failed. the sign error,sign:"+MySign+",channelSign:"+sign);
                callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the sign error,sign:"+MySign+",channelSign:"+sign);
            }

            callback.onSuccess(new SDKVerifyResult(true,userName,userName,""));

        }catch(Exception e){
            log.error("--------------->SuperCenterSDK verify total exception,the result is " + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        JSONObject json = new JSONObject();
        json.put("product_name",order.getProductName());
        json.put("amount",order.getMoney()/100+"");
        json.put("orderNo",order.getOrderID()+"");
        json.put("currency","金币");

        if (callback != null) {
            log.info("--------------->SuperCenterSDK onGetOrderID,the json is " + json.toString());
            callback.onSuccess(json.toString());
        }
    }
}
