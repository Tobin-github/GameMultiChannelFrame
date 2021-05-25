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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VIVO SDK
 * Created by ant on 2015/4/23.
 */
public class VivoSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(VivoSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{
            UChannelMaster channelMaster = channel.getMaster();
            if (null == channelMaster) {
                log.error("-------------->verify 渠道不存在");
                return;
            }
            String authUrl = channelMaster.getAuthUrl();
            JSONObject jsonObject = JSONObject.fromObject(extension);
            String token = jsonObject.getString("token");

            Map<String, String> params = new HashMap<String, String>();
            params.put("authtoken", token);

            log.info("--------->verify ,The auth request data :" + params.toString()+", url:"+authUrl+", extension:"+extension);

            UHttpAgent.getInstance().post(authUrl, params, new UHttpFutureCallback() {
                @Override
                public void completed(String content) {

                    log.info("--------->verify ,The auth result is " + content);
                    try{

                        JSONObject jsonObject = JSONObject.fromObject(content);
                        String retcode = jsonObject.getString("retcode");
                        String data = jsonObject.getString("data");
                        JSONObject json = JSONObject.fromObject(data);
                        final String openid = json.getString("openid");

                        if("0".equals(retcode)){
                            log.info("--------->verify sucessful ");
                            callback.onSuccess(new SDKVerifyResult(true, openid, "", ""));
                            return;
                        }

                    }catch (Exception e){
                        log.error("--------->verify exception:" + e.getMessage());
                        e.printStackTrace();
                    }

                    log.error("--------->verify fail,sdk's name:" + channel.getMaster().getSdkName() + ", verify failed. the post result is " + content);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + content);

                }

                @Override
                public void failed(String e) {
                    log.error("--------->verify fail,sdk's name:" + channel.getMaster().getSdkName() + ", verify failed");
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        }catch (Exception e){
            log.error("--------->verify exception2:" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){

            try{

                UChannel channel = order.getChannel();
                if(channel == null){
                    log.error("------------>onGetOrderID,The channel is not exists of order " + order.getOrderID());
                    return;
                }

                String orderUrl = channel.getMaster().getOrderUrl();

                log.debug("--------->onGetOrderID,the order url is "+orderUrl);
                log.debug("--------->onGetOrderID,CPID:" + order.getChannel().getCpID());
                log.debug("--------->onGetOrderID,appID:" + order.getChannel().getCpAppID());
                log.debug("--------->onGetOrderID,appKey:" + order.getChannel().getCpAppKey());

                String version = "1.0.0";
                String signMethod = "MD5";
                String signature = "";
                String cpId = order.getChannel().getCpID();
                String appId = order.getChannel().getCpAppID();
                String cpOrderNumber = ""+order.getOrderID();
                String notifyUrl = channel.getMaster().getPayCallbackUrl();
                String orderTime = TimeFormater.format_yyyyMMddHHmmss(order.getCreatedTime());
                //String orderAmount = order.getMoney() + "";
                String orderAmount = "1";
                String orderTitle = order.getProductName();
                String orderDesc = StringUtils.isEmpty(order.getProductDesc())?order.getProductName():order.getProductDesc();
                String extInfo = order.getOrderID()+"";        //空字符串不参与签名

                StringBuilder sb = new StringBuilder();
                sb.append("appId=").append(appId).append("&")
                .append("cpId=").append(cpId).append("&")
                .append("cpOrderNumber=").append(cpOrderNumber).append("&")
                .append("extInfo=").append(extInfo).append("&")
                .append("notifyUrl=").append(notifyUrl).append("&")
                .append("orderAmount=").append(orderAmount).append("&")
                .append("orderDesc=").append(orderDesc).append("&")
                .append("orderTime=").append(orderTime).append("&")
                .append("orderTitle=").append(orderTitle).append("&")
                .append("version=").append(version).append("&")
                .append(EncryptUtils.md5(channel.getCpAppKey()).toLowerCase());

                log.info("------------>onGetOrderID,unSignStr:" + sb.toString());
                signature = EncryptUtils.md5(sb.toString()).toLowerCase();
                log.info("------------>onGetOrderID,sign:" + signature);

                Map<String,String> params = new HashMap<String, String>();
                params.put("version", version);
                params.put("signMethod", signMethod);
                params.put("signature", signature);
                params.put("cpId", cpId);
                params.put("appId", appId);
                params.put("cpOrderNumber", cpOrderNumber);
                params.put("notifyUrl", notifyUrl);
                params.put("orderTime", orderTime);
                params.put("orderAmount", orderAmount);
                params.put("orderTitle", orderTitle);
                params.put("orderDesc", orderDesc);
                params.put("extInfo", extInfo);

                log.info("-------->onGetOrderID, The vivo order request data:" + params.toString()+",orderUrl:"+orderUrl);
                String result = UHttpAgent.getInstance().post(orderUrl, params);
                log.info("-------->onGetOrderID, The vivo order response data:" + result);

                VivoOrderResult orderResult = (VivoOrderResult)JsonUtils.decodeJson(result, VivoOrderResult.class);
                if(orderResult != null && orderResult.getRespCode() == 200){

                    JSONObject ext = new JSONObject();
                    ext.put("transNo", orderResult.getOrderNumber());
                    ext.put("accessKey", orderResult.getAccessKey());
                    ext.put("version", version);
                    ext.put("signMethod", signMethod);
                    ext.put("signature", signature);
                    ext.put("cpId", cpId);
                    ext.put("appId", appId);
                    ext.put("cpId", cpId);
                    ext.put("cpOrderNumber", cpOrderNumber);
                    ext.put("notifyUrl", notifyUrl);
                    ext.put("orderTime", orderTime);
                    ext.put("orderAmount", orderAmount);
                    ext.put("orderTitle", orderTitle);
                    ext.put("orderDesc", orderDesc);
                    ext.put("extInfo", extInfo);
                    String extStr = ext.toString();
                    log.info("-------->onGetOrderID callBack data:" + extStr);
                    callback.onSuccess(extStr);

                }else{
                    log.info("-------->onGetOrderID fail,the vivo order result is "+result);
                    callback.onFailed("the vivo order result is " + result);

                }

            }catch (Exception e){
                log.info("-------->onGetOrderID Exception,msg:"+e.getMessage());
                e.printStackTrace();
                callback.onFailed(e.getMessage());
            }



        }
    }
}
