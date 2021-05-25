package com.u8.server.sdk.APPStoeAndWXAndAl;

import com.thoughtworks.xstream.XStream;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.sdk.appstore.AppStoreSDK;
import com.u8.server.sdk.wx.WXOrderInfo;
import com.u8.server.sdk.wx.WXSDK;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.TextUtils;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 * 已废弃
 */
public class APPStoreWXAliSDK implements ISDKScriptExt {
    public static final String createOrderURL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String callBackURL = "/pay/wxpay/payCallback";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        JSONObject json = JSONObject.fromObject(extension);
        final String loginType = json.getString("loginType");
        if(loginType.equals("1")){
            WXSDK.WxVerivy(channel,extension,true,callback);
        }
        if(loginType.equals("2")){//游客登录
            final String accesstoken = json.getString("accesstoken");
            Log.d("The result of yile verify is :" + accesstoken);
            if (!TextUtils.isEmpty(accesstoken)){
                callback.onSuccess(new SDKVerifyResult(true, accesstoken, "", ""));
                return;
            }else{
                callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
            }
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try{

        }
        catch (Exception e){

        }
        if(callback != null){
            callback.onSuccess("");
        }
    }

    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback){

    }

    //根据宝石风暴的参数 payType = 3 是为微信支付  payType = 2 是阿里支付
    public void onGetOrderIDByType(UUser user, UOrder order, int payType, UChannelPayTypeManager manager, ISDKOrderListener callback){
        if(payType==1){
            AppstoreGetOrder(user,order,callback);
        }else  if(payType==2){
            //AliSDK.AliGetOrder(user, order,true, callback);
        }else if(payType==3){
            WXSDK.WxGetOrder(user, order,true, callback);
        }
    }

    public void AppstoreGetOrder(UUser user, UOrder order, ISDKOrderListener callback) {
        AppStoreSDK.AppStoreGetOrder(user,order,callback);
    }



    public void WXGetOrder(UUser user, UOrder order, final ISDKOrderListener callback) {
        try{
            final String appid = order.getChannel().getCpAppID();
            final String appKey = order.getChannel().getCpAppKey();
            String attach = "bsfb";
            String body = order.getChannel().getCpConfig();
            final String mch_id = order.getChannel().getCpPayID();
            final String nonce_str = UUID.randomUUID().toString().replace("-","");
            String notify_url = UHttpAgent.ServerHost+callBackURL;
            String out_trade_no = order.getOrderID().toString();
            String spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
            String total_fee = order.getMoney().toString();
            String trade_type = "APP";

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("appid", appid);
            parameters.put("mch_id", mch_id);
            parameters.put("nonce_str", nonce_str);
            parameters.put("body", body);//购买支付信息
            parameters.put("out_trade_no", out_trade_no);//订单号
            parameters.put("total_fee", total_fee);// 总金额单位为分
            parameters.put("spbill_create_ip", spbill_create_ip);
            parameters.put("notify_url", notify_url);
            parameters.put("trade_type", trade_type);
            String sign = createSign("UTF-8", parameters,appKey);
            parameters.put("sign", sign);
            String requestXML = getRequestXml(parameters);
            UHttpAgent.getInstance().post(createOrderURL,null,new ByteArrayEntity(requestXML.getBytes(Charset.forName("UTF-8"))),new UHttpFutureCallback(){
                @Override
                public void completed(String result) {

                    try {

                        Log.d("the result is "+result);

                        XStream xStream = new XStream();
                        xStream.alias("xml", WXOrderInfo.class);
                        WXOrderInfo authInfo = (WXOrderInfo)xStream.fromXML(result);
                        if(authInfo.getReturn_code().equals("FAIL")){
                            callback.onFailed(authInfo.getReturn_msg());
                        }else{
                            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
                            params.put("appid", appid);
                            params.put("partnerid",mch_id);
                            params.put("prepayid",authInfo.getPrepay_id());
                            params.put("package", "Sign=WXPay");
                            params.put("nonceStr", nonce_str);
                            params.put("timeStamp", "\""+new Date().getTime()+"\"");
                            String paySign = createSign("UTF-8", params,appKey);
                            params.put("paySign", paySign); // paySign的生成规则和Sign的生成规则一致
                            String json = JSONObject.fromObject(params).toString();
                            Log.d("the json is "+json);
                            callback.onSuccess(json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                public void failed(String e) {
                    callback.onFailed(e);
                }
            });
        }
        catch(Exception e){

        }
    }

    /**
     * @author lwz
     * @date 2014-12-8
     * @Description：sign签名
     * @param characterEncoding
     *            编码格式
     * @param parameters
     *            请求参数
     * @return
     */
    public static String createSign(String characterEncoding,SortedMap<Object, Object> parameters,String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = EncryptUtils.md5(sb.toString()).toUpperCase();

        return sign;
    }

    /**
     * @author 老妖
     * @date 2014-12-5下午2:32:05
     * @Description：将请求参数转换为xml格式的string
     * @param parameters
     *            请求参数
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k)|| "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
