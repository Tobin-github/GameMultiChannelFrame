package com.u8.server.sdk.huawei;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.RSAUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 华为SDK
 * Created by ant on 2015/4/27.
 */
public class HuaWeiSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(HuaWeiSDK.class.getName());

    @Override
    public void verify(final UChannel channel,String extension, final ISDKVerifyListener callback) {

        try{
            JSONObject json = JSONObject.fromObject(extension);
            String rtnCode = json.getString("rtnCode");
            log.info("-------->verify ,The result ofrtnCode :" + rtnCode+", extension:"+extension);
            if ("0".equals(rtnCode)) {
                String playerId = json.getString("playerId");
                String displayName = json.getString("displayName");
                Log.e("The auth result is " + extension);
                SDKVerifyResult vResult = new SDKVerifyResult(true, playerId, displayName, "");
                log.info("-------->verify ,The result success,vResult :" + vResult);
                callback.onSuccess(vResult);

            } else {
                log.error("-------->verify ,The result fail,sdk :" + channel.getMaster().getSdkName());
                callback.onFailed(channel.getMaster().getSdkName() + " verify failed. ");
            }

        }catch (Exception e){
            log.error("-------->verify exception,msg :" + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }


    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {

        String userName = order.getChannel().getCpConfig();
        String userID=order.getChannel().getCpPayID();
        String applicationID=order.getChannel().getCpAppID();
        //String amount="0.01";
        //String amount=(int)(order.getMoney() / 100)+".00";
        DecimalFormat df=new DecimalFormat("0.00");
        String amount =df.format(order.getMoney()/100.00);
        String productName=order.getProductName();
        String requestId=order.getOrderID()+"";
        String productDesc=order.getProductName();
        String notifyUrl=UHttpAgent.ServerHost + "/pay/huawei/payCallback";
        String priKey=order.getChannel().getCpPayPriKey();

        Map<String, String> params = new HashMap<String, String>();
        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
        // the pay ID is required and can not be null or ""
        params.put("userID", userID);
        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
        // the APP ID is required and can not be null or ""
        params.put("applicationID", applicationID);
        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
        // the amount (accurate to two decimal places) is required
        params.put("amount", amount);
        // 必填字段，不能为null或者""，道具名称
        // the product name is required and can not be null or ""
        params.put("productName", productName);
        // 必填字段，不能为null或者""，道具描述
        // the product description is required and can not be null or ""
        params.put("productDesc", productDesc);
        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
        // the request ID is required and can not be null or "". Also it must be unique.
        params.put("requestId", requestId);

        String noSign = getSignData(params);
        log.info("-------->onGetOrderID ,unSignStr:" + noSign+", priKey:"+priKey);
        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
        // the CP need to send the params to the server and sign the params on the server ,
        // then the server passes down the sign to client;
        String sign = RSAUtil.sign(noSign, priKey);
        log.info("-------->onGetOrderID ,signStr:" +sign);
        if (callback != null) {
            //String sign = getSign(user.getChannel(), order, user);

            JSONObject json = new JSONObject();
            json.put("amount",amount);
            json.put("productName",productName);
            json.put("requestId",requestId);
            json.put("productDesc",productDesc);
            json.put("applicationID",applicationID);
            json.put("userID",userID);
            json.put("userName",userName);
            json.put("sign",sign);
            json.put("signType","RSA256");
            json.put("notifyUrl",notifyUrl);
            log.info("-------->onGetOrderID ,response data::" +json.toString());

            callback.onSuccess(json.toString());
        }
    }

    public static String getSignData(Map<String, String> params)
    {
        StringBuffer content = new StringBuffer();

        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++)
        {
            String key = (String)keys.get(i);
            if (!"sign".equals(key))
            {
                String value = (String)params.get(key);
                if (value != null) {
                    content.append((i == 0 ? "" : "&") + key + "=" + value);
                }
            }
        }
        return content.toString();
    }
}
