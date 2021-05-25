package com.u8.server.sdk.ali;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.sdk.wx.WXSDK;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;
import com.u8.server.utils.RSAUtils;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class AliSDKOrigin implements ISDKScriptExt {
    private static String CHARSET = "utf-8";

    private static Logger log = Logger.getLogger(AliSDKOrigin.class.getName());

    private static final String NOTIFY_URL = "http://120.25.243.133:8080/pay/alipay/payCallback";
    //private static final String NOTIFY_URL = "http://www.wgb.cn:9088/pay/alipay/payCallback";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try {

            JSONObject json = JSONObject.fromObject(extension);

            final String account = json.getString("account");
            final String token = json.getString("token");


            Map<String, String> params = new HashMap<String, String>();
            params.put("account", account);
            params.put("token", token);
            params.put("appid", channel.getCpAppID());


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of yile verify is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            int code = jr.getInt("errorcode");
                            if (code == 1) {
                                callback.onSuccess(new SDKVerifyResult(true, account, "", ""));
                                return;
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
                }

                @Override
                public void failed(String err) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try {
            //Android平台Id为1,IOS为2
            if (1 == order.getChannel().getPlatID()) {
                boolean isMutipay = order.getChannel().getPlatType() == 1 ? true : false;
                AliGetOrder(user, order, isMutipay, callback);
            } else {
                UChannel channel = order.getChannel();
                String PostUrl=UHttpAgent.ServerHost + "/pay/charge";
                JSONObject jsonObj = new JSONObject();

                Map<String, String> message = new HashMap<String, String>();
                message.put("UserID", user.getChannelUserID());
                message.put("RechargeType", "1");
                message.put("PropId", "1");
                message.put("ChargeWay", "支付宝支付");
                message.put("IsApp", "1");
                message.put("UniqueSerial", "000102-001-158");
                message.put("PostUrl", PostUrl);
                message.put("orderId", order.getOrderID() + "");

                jsonObj.put("Message", message);
                jsonObj.put("Value", null);

                callback.onSuccess(jsonObj.toString());
                Log.i("get order extension success:" + jsonObj.toString());
            }


        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            Log.e("get order extension fail:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void AliGetOrder(UUser user, UOrder order, boolean isMultiPay, ISDKOrderListener callback) {
        try {

            String appID = order.getChannel().getCpAppID();
            String sellerId = order.getChannel().getCpID();
            String privateKey = order.getChannel().getCpPayPriKey();
            DecimalFormat df=new DecimalFormat("0.00");
            String orderMoney =df.format(order.getMoney()/100.00);
            //String orderMoney="0.01";

            if (order.getChannel().getPlatType() == 1) {
                JSONObject appIDObj = JSONObject.fromObject("{" + appID + "}");
                JSONObject priKeyObj = JSONObject.fromObject("{" + privateKey + "}");
                appID = appIDObj.getString("Ali");
                privateKey = priKeyObj.getString("Ali");
            }

            log.info("------------------->AliGetOrder,android alipay appID:" + appID+", privateKey:"+privateKey+", cpID:"+sellerId);

            String unSign="partner=\""+appID+"\"&seller_id=\""+sellerId+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+NOTIFY_URL+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            log.debug("android alipay unSignContent:"+unSign);
            String unEncode=RSAUtils.sign(unSign, privateKey, "UTF-8", "SHA1withRSA");
            log.debug("android alipay unEncodeContent:" + unEncode);
            String encodeStr=URLEncoder.encode(unEncode, "UTF-8");
            log.debug("android alipay encodeContent:" + encodeStr);
            String resultStr=unSign+"&sign=\""+encodeStr+"\"&sign_type=\"RSA\"";
            log.debug("android alipay ClitentResultStr:" + resultStr);
            //resultStr="partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"AO2018011218344351685321829\"&subject=\"120000点数\"&body=\"120000点数\"&total_fee=\"1.20\"&notify_url=\"http://120.25.171.126:8099/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"RzzEBrX0EHXFIMStuBe87igKPT1KxYRbVdcGrVWRlXQiu0pTe%2fdgZQlYSXlucqhyxvdHHPcoRbcCLjAwhlE7GeRwscoEjcqnDvTLVdvTPYpyv00y3m85NaSjjasDpU1bL%2fj%2bieoSij6x1IzA22wKwf31MKkCCsE4LOLOKCNMz%2fU%3d\"&sign_type=\"RSA\"";
            if (callback != null) {
                callback.onSuccess(resultStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
            callback.onSuccess(e.getMessage());
        }

    }

    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback) {

    }

    //根据宝石风暴的参数 payType = 2 是为微信支付  payType = 1 是阿里支付
    public void onGetOrderIDByType(UUser user, UOrder order, int payType, UChannelPayTypeManager manager, ISDKOrderListener callback) {
        if (payType == 2) {
            WXSDK.WxGetOrder(user, order, true, callback);
        } else if (payType == 1) {
            //AliSDK.AliGetOrder(user,order,true,callback);
        }
    }

    /**
     * 支付宝签名
     *
     * @param array
     * @return
     */
    private String signAllString(String[] array, final String key) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < array.length; i++) {
            if (i == (array.length - 1)) {
                sb.append(array[i]);
            } else {
                sb.append(array[i] + "&");
            }
        }
        System.out.println(sb.toString());
        String sign = "";
        try {
            String rsaStirng = RSAUtils.sign(sb.toString(), key, "UTF-8", "SHA1withRSA");
            sign = URLEncoder.encode(rsaStirng, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("&sign=\"" + sign + "\"&");
        sb.append("sign_type=\"RSA\"");
        return sb.toString();
    }


}
