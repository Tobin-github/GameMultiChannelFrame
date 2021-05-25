package com.u8.server.sdk.ali;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.RSAUtils;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class AliSDKOld implements ISDKScript {
    private static String CHARSET = "utf-8";
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);

            final String account = json.getString("account");
            final String token = json.getString("token");


            Map<String,String> params = new HashMap<String, String>();
            params.put("account", account);
            params.put("token", token);
            params.put("appid", channel.getCpAppID());


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of yile verify is :" + result);
                        if (!TextUtils.isEmpty(result)){
                            JSONObject jr = JSONObject.fromObject(result);
                            int code = jr.getInt("errorcode");
                            if(code == 1){
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


        }catch (Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    public static void AliGetOrder(UUser user, UOrder order,boolean isMultiPay, ISDKOrderListener callback) {
        try{

            String appID = order.getChannel().getCpAppID();
            String privateKey =  order.getChannel().getCpPayPriKey();
            String publicKey = order.getChannel().getCpPayKey();
            if(isMultiPay){
                JSONObject appIDObj = JSONObject.fromObject("{"+appID+"}");
                JSONObject priKeyObj = JSONObject.fromObject("{"+privateKey+"}");
                appID = appIDObj.getString("Ali");
                privateKey = priKeyObj.getString("Ali");
            }
            String body = order.getProductDesc();
            String notify_url = UHttpAgent.ServerHost+"/pay/alipay/payCallback";
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appID, privateKey , "json", CHARSET, publicKey, "RSA2");
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setPassbackParams(URLEncoder.encode(body.toString()));  //描述信息  添加附加数据
            model.setSubject(order.getProductName()); //商品标题
            model.setOutTradeNo(order.getOrderID()+""); //商家订单编号
            model.setTimeoutExpress("30m"); //超时关闭该订单时间
            model.setTotalAmount(order.getMoney().toString());  //订单总金额
            model.setProductCode("QUICK_MSECURITY_PAY"); //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
            request.setBizModel(model);
            request.setNotifyUrl(notify_url);  //回调地址
            String orderStr = "";
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            orderStr = response.getBody();
            System.out.println(orderStr);//就是orderString 可以直接给客户端请求，无需再做处理。
            if(callback != null){
                callback.onSuccess(orderStr);
            }

        }
        catch (Exception e){
            callback.onFailed(order.getChannel().getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }


    }

    private static String encode(String value){
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        //AliSDK.AliGetOrder(user,order,false,callback);


        try {
            JSONObject jsonObj = new JSONObject();

            Map <String, String> message = new HashMap <String, String>();
            message.put("UserID", user.getChannelUserID());
            message.put("RechargeType", "1");
            message.put("PropId", "1");
            message.put("ChargeWay", "支付宝支付");
            message.put("IsApp", "1");
            message.put("UniqueSerial", "000102-001-158");
            message.put("PostUrl", "http://120.25.243.133/Charge/RechargeByZfb");

            jsonObj.put("Message", message);
            jsonObj.put("Value", null);

            callback.onSuccess(jsonObj.toString());
            Log.i("get order extension success:"+jsonObj.toString());
        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            Log.e("get order extension fail:"+e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 支付宝签名
     * @param array
     * @return
     */
    private String signAllString(String [] array,final String key){
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < array.length; i++) {
            if(i==(array.length-1)){
                sb.append(array[i]);
            }else{
                sb.append(array[i]+"&");
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
        sb.append("&sign=\""+sign+"\"&");
        sb.append("sign_type=\"RSA\"");
        return sb.toString();
    }
}
