package com.u8.server.sdk.vivo;

import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * VIVO SDK
 * Created by ant on 2015/4/23.
 */
public class SingleVivoSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(SingleVivoSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        JSONObject json = JSONObject.fromObject(extension);
        final String accesstoken = json.getString("accesstoken");
        log.debug("-------->verify ,The result of yile verify is :" + accesstoken);
        if (!TextUtils.isEmpty(accesstoken)){
            callback.onSuccess(new SDKVerifyResult(true, accesstoken, "", ""));
            return;
        }else{
            callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){

            try{

                UChannel channel = order.getChannel();
                if(channel == null){
                    log.error("------> onGetOrderID  , The channel is not exists of order " + order.getOrderID());
                    return;
                }
                String orderUrl = channel.getMaster().getOrderUrl();

                log.debug("------> onGetOrderID ,the order url is " + orderUrl);
                log.debug("------> onGetOrderID ,CPID:" + order.getChannel().getCpID());
                log.debug("------> onGetOrderID ,appID:" + order.getChannel().getCpAppID());
                log.debug("------> onGetOrderID ,appKey:" + order.getChannel().getCpAppKey());

                String version = "1.0.0";
                String signMethod = "MD5";
                String signature = "";
                String storeId = order.getChannel().getCpID();
                String appId = order.getChannel().getCpAppID();
                String storeOrder = ""+order.getOrderID();
                //String notifyUrl = UHttpAgent.ServerHost + "/pay/singlevivo/payCallback";
                String notifyUrl = channel.getPayCallbackUrl();
                String orderTime = TimeFormater.format_yyyyMMddHHmmss(order.getCreatedTime());
                DecimalFormat df=new DecimalFormat("0.00");
                String orderAmount =df.format(order.getMoney()/100.00);
                //String orderAmount ="0.01";
                String orderTitle = order.getProductName();
                String orderDesc = StringUtils.isEmpty(order.getProductDesc())?order.getProductName():order.getProductDesc();

                StringBuilder sb = new StringBuilder();

                sb.append("appId=").append(appId).append("&")
                        .append("notifyUrl=").append(notifyUrl).append("&")
                        .append("orderAmount=").append(orderAmount).append("&")
                        .append("orderDesc=").append(orderDesc).append("&")
                        .append("orderTime=").append(orderTime).append("&")
                        .append("orderTitle=").append(orderTitle).append("&")
                        .append("storeId=").append(storeId).append("&")
                        .append("storeOrder=").append(storeOrder).append("&")
                        .append("version=").append(version).append("&")
                        .append(EncryptUtils.md5(channel.getCpAppKey()).toLowerCase());

                log.debug("------> onGetOrderID  , The unSigned string: " + sb.toString());
                signature = EncryptUtils.md5(sb.toString()).toLowerCase();
                log.debug("------> onGetOrderID  , The Signed string: " + signature);

                Map<String,String> params = new HashMap<String, String>();
                params.put("version", version);
                params.put("signMethod", signMethod);
                params.put("signature", signature);
                params.put("storeId", storeId);
                params.put("appId", appId);
                params.put("storeOrder", storeOrder);
                params.put("notifyUrl", notifyUrl);
                params.put("orderTime", orderTime);
                params.put("orderAmount", orderAmount);
                params.put("orderTitle", orderTitle);
                params.put("orderDesc", orderDesc);

                String result = UHttpAgent.getInstance().post(orderUrl, params);

                log.debug("------> onGetOrderID , The vivo order result is " + result);
                JSONObject jsonResult = JSONObject.fromObject(result);
                int respCode = jsonResult.getInt("respCode");

                if(respCode == 200) {
                    log.debug("------> onGetOrderID respCode == 200, The vivo order result is " + result);
                    String jsonSignMethod = jsonResult.getString("signMethod");
                    String jsonSignature = jsonResult.getString("signature");
                    String jsonVivoSignature = jsonResult.getString("vivoSignature");
                    String jsonVivoOrder = jsonResult.getString("vivoOrder");
                    String jsonOrderAmount = jsonResult.getString("orderAmount");

                    JSONObject ext = new JSONObject();
                    ext.put("signMethod", jsonSignMethod);
                    ext.put("signature", jsonSignature);
                    ext.put("vivoSignature", jsonVivoSignature);
                    ext.put("vivoOrder", jsonVivoOrder);
                    ext.put("orderAmount", jsonOrderAmount);
                    callback.onSuccess(ext.toString());
                }else{
                    log.error("------> onGetOrderID respCode is fail, The vivo order result is " + result);
                    callback.onFailed("the vivo order result is "+result);

                }

            }catch (Exception e){
                log.error("------> exception:" + e.getMessage());
                e.printStackTrace();
                callback.onFailed(e.getMessage());
            }


        }
    }
}
