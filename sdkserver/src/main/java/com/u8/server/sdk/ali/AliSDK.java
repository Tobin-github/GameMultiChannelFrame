package com.u8.server.sdk.ali;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
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

public class AliSDK implements ISDKScriptExt {

    private static Logger log = Logger.getLogger(AliSDK.class.getName());

    private static String CHARSET = "utf-8";

    private static final String notify_url = "http://120.25.243.133:8080/pay/alipay/payCallback";// 增加支付异步通知回调,记住上下notify_url的位置,全在sign_type之前,很重要,同样放在最后都不行
    private static final String PostUrl="http://120.25.243.133:8080/pay/charge";

    /*private static final String notify_url = "http://www.wgb.cn:9088/pay/alipay/payCallback";// 增加支付异步通知回调,记住上下notify_url的位置,全在sign_type之前,很重要,同样放在最后都不行
    private static final String PostUrl="http://www.wgb.cn:9080/pay/charge";*/

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
                        log.debug("------------------->verify,The result of yile verify is :" + result);
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
                log.info("------------------->onGetOrderID,get order extension success:" + jsonObj.toString());
            }


        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            log.error("------------------->onGetOrderID,get order extension fail:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void AliGetOrder(UUser user, UOrder order, boolean isMultiPay, ISDKOrderListener callback) {
        try {

            String appID = order.getChannel().getCpAppID();
            String privateKey = order.getChannel().getCpPayPriKey();
            String cpID = order.getChannel().getCpID();

            DecimalFormat df=new DecimalFormat("0.00");
            String orderMoney =df.format(order.getMoney()/100.00);
            //String orderMoney="0.01";

            if (order.getChannel().getPlatType() == 1) {
                JSONObject appIDObj = JSONObject.fromObject("{" + appID + "}");
                JSONObject priKeyObj = JSONObject.fromObject("{" + privateKey + "}");
                appID = appIDObj.getString("Ali");
                privateKey = priKeyObj.getString("Ali");
            }
            log.info("------------------->AliGetOrder,android alipay appID:" + appID+", privateKey:"+privateKey+", cpID:"+cpID);

            //String unSign="partner=\""+appID+"\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String unSign="partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"AO2017111314214382884042505391\"&subject=\"120000点数\"&body=\"120000点数\"&total_fee=\"1.20\"&notify_url=\"http://120.25.171.126:8090/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String unSign="partner=\""+appID+"\"&seller_id=\""+cpID+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String unSign="partner=\""+"2088421514012457"+"\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&out_trade_no=\""+"AO2017121917215969850356998473"+"\"&subject=\""+"120000点数"+"\"&body=\""+"120000点数"+"\"&total_fee=\""+"12.00"+"\"&notify_url=\""+"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1 "+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&";

            //String unSign="partner=\""+"2088421514012457"+"\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&out_trade_no=\""+"AO2017121917215969850356998473"+"\"&subject=\""+"120000点数"+"\"&body=\""+"120000点数"+"\"&total_fee=\""+"12.00"+"\"&notify_url=\""+"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1 "+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&";
            //String unSign="partner=\""+"2088421514012457"+"\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&out_trade_no=\""+"AO2017121917215969850356998473"+"\"&subject=\""+"120000点数"+"\"&body=\""+"120000点数"+"\"&total_fee=\""+"12.00"+"\"&notify_url=\""+"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1 "+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&";

            //String unSign="partner=\""+appID+"\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&";

            //String paramStr="_input_charset=\"utf-8\"&body=\""+"120000点数"+"\"&notify_url=\""+"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1 "+"\"&out_trade_no=\""+"AO2017121917215969850356998473"+"\"&partner=\""+"2088421514012457"+"\"&payment_type=\"1\"&it_b_pay=\"30m\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&service=\"mobile.securitypay.pay\"&subject=\""+"120000点数"+"\"&total_fee=\""+"12.00"+"\"";

            /*Map params=new LinkedMap();
            params.put("partner", "2088421514012457");
            params.put("seller_id", "zhaofei@bzw.cn");
            params.put("out_trade_no", "AO2017121917215969850356998473");
            params.put("subject", "120000点数");
            params.put("total_fee", "12.00");
            params.put("notify_url", "http://218.60.113.182:8014/Third/Notify/AlipayNotify/1");
            params.put("service", "mobile.securitypay.pay");
            params.put("payment_type", "1");
            params.put("_input_charset", "utf-8");*/

            //DefaultAlipayClient defaultAlipayClient = new DefaultAlipayClient();

            //String s = generateSignStr(params);
            //log.info("------------------->unsingParam:" + s);

            //String qiangStr = "partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"BO2017121518173403840314574597\"&subject=\"VIP会员(周)\"&body=\"VIP会员(周)\"&total_fee=\"6.00\"&notify_url=\"http://120.25.171.126:8099/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String qiangStr = "partner=\""+"2088421514012457"+"\"&seller_id=\""+"zhaofei@bzw.cn"+"\"&out_trade_no=\""+"BO2017121518173403840314574597"+"\"&subject=\""+"VIP会员(周)"+"\"&body=\""+"VIP会员(周)"+"\"&total_fee=\""+"6.00"+"\"&notify_url=\""+"http://120.25.171.126:8099/Third/Notify/AlipayNotify/1"+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String qiangStr = "partner=\""+appID+"\"&seller_id=\""+cpID+"\"&out_trade_no=\""+"BO2017121518173403840314574597"+"\"&subject=\""+"VIP会员(周)"+"\"&body=\""+"VIP会员(周)"+"\"&total_fee=\""+"6.00"+"\"&notify_url=\""+"http://120.25.171.126:8099/Third/Notify/AlipayNotify/1"+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            String qiangStr = "partner=\""+appID+"\"&seller_id=\""+cpID+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
            //String qiangStr = "partner=\""+appID+"\"&seller_id=\""+cpID+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";

            log.info("------------------->AliGetOrder,android alipay unSignContent:" + qiangStr);


            String unEncode=RSAUtils.sign(qiangStr, privateKey, "UTF-8", "SHA1withRSA");
            log.info("------------------->AliGetOrder,android alipay unEncodeContent:" + unEncode);
            String encodeStr=URLEncoder.encode(unEncode, "UTF-8");
            //encodeStr = encodeStr.toLowerCase();
            log.info("------------------->AliGetOrder,android alipay encodeContent:" + encodeStr);
            //String resultStr=unSign+"&sign=\""+encodeStr+"\"&sign_type=\"RSA\"";
            //log.info("------------------->AliGetOrder,android alipay ClitentResultStr:" + resultStr);
            //String resultStr="partner=2088421514012457&seller_id=zhaofei@bzw.cn&out_trade_no=AO2017111314214382884042505399&subject=120000点数&body=120000点数&total_fee=1.20&notify_url=http://120.25.171.126:8090/Third/Notify/AlipayNotify/1&service=mobile.securitypay.pay&payment_type=1&_input_charset=utf-8&it_b_pay=30m&sign=Mk%2f%2befaxx7cPUo0YQUgUg07C5UGpse010EKx7en87WLMsQqO2RTOiedRDUNZXZtPbooK7%2bG3VfiXPndaTbZ2eJZ4KHA5h88lZnHZhXhd04h577VN%2f6ah2cCv9E%2bog6FRkImRfegTi1srJ0c08nbCzAmOyFGctN6iHPKooT600Fw%3d&sign_type=RSA";
            //String resultStr="partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"AO2017111314214382884042505399\"&subject=\"120000点数\"&body=\"120000点数\"&total_fee=\"1.20\"&notify_url=\"http://120.25.171.126:8090/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&sign=\"Mk%2f%2befaxx7cPUo0YQUgUg07C5UGpse010EKx7en87WLMsQqO2RTOiedRDUNZXZtPbooK7%2bG3VfiXPndaTbZ2eJZ4KHA5h88lZnHZhXhd04h577VN%2f6ah2cCv9E%2bog6FRkImRfegTi1srJ0c08nbCzAmOyFGctN6iHPKooT600Fw%3d\"&sign_type=\"RSA\"";

            /*JSONObject json = new JSONObject();

            json.put("unsignstr", "partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"AO2017121917215969850356998473\"&subject=\"120000点数\"&body=\"120000点数\"&total_fee=\"12.00\"&notify_url=\"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&");
            json.put("sign","QxzWRtuPkfROQpbzRPGm9QHy%2fb9aJLsVY%2fHJS6c65W1II4OwphGBR9H6ETGedIpNfJHnpj5lpbGAn94CDwCaTkCwkgGmhnGk4FdkOfNY3fs4iRT4pw%2fPqSzWEIUWCfbKYMVktJ8VwpsNaNIgkkhNA9JGuywlhvG7hx94Dtwndes%3d");
            json.put("signtype"," \"RSA\"");
            log.info("------------------->result:" + json.toString());*/

            JSONObject json2 = new JSONObject();
            //json2.put("unsignstr", "partner=\"2088421514012457\"&seller_id=\"zhaofei@bzw.cn\"&out_trade_no=\"BO2017121518173403840314574597\"&subject=\"VIP会员(周)\"&body=\"VIP会员(周)\"&total_fee=\"6.00\"&notify_url=\"http://120.25.171.126:8099/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&");
            json2.put("unsignstr", "partner=\""+appID+"\"&seller_id=\""+cpID+"\"&out_trade_no=\""+order.getOrderID()+"\"&subject=\""+order.getProductName()+"\"&body=\""+order.getProductName()+"\"&total_fee=\""+orderMoney+"\"&notify_url=\""+notify_url+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&");
            //json2.put("sign","QxzWRtuPkfROQpbzRPGm9QHy%2fb9aJLsVY%2fHJS6c65W1II4OwphGBR9H6ETGedIpNfJHnpj5lpbGAn94CDwCaTkCwkgGmhnGk4FdkOfNY3fs4iRT4pw%2fPqSzWEIUWCfbKYMVktJ8VwpsNaNIgkkhNA9JGuywlhvG7hx94Dtwndes%3d");
            json2.put("sign",encodeStr);
            json2.put("signtype"," \"RSA\"");
            log.info("------------------->result2:" + json2.toString());

            if (callback != null) {
                callback.onSuccess(json2.toString());
            }



        } catch (Exception e) {
            e.printStackTrace();
            callback.onSuccess(e.getMessage());
        }

    }

    private String generateSignStr(Map<String,String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry:
        map.entrySet()) {
            sb.append(entry.getKey()+"=\""+entry.getValue()+"\"&");
        }

        return sb.substring(0, sb.length() - 1);
    }

    @Override
    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback) {

    }

    //根据宝石风暴的参数 payType = 2 是为微信支付  payType = 1 是阿里支付
    @Override
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
