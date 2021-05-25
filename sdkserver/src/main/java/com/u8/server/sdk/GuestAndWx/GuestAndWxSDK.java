package com.u8.server.sdk.GuestAndWx;

import com.thoughtworks.xstream.XStream;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.sdk.wx.WXOrderInfo;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 官方包
 * 游客登录、微信支付
 * Created by ant on 2016/9/1.
 */
public class GuestAndWxSDK implements ISDKScript {
    private static Logger log = Logger.getLogger(GuestAndWxSDK.class.getName());

    public static final String callBackURL = "http://120.25.243.133:8080/pay/wxpay/payCallback";
    //public static final String callBackURL = "http://www.wgb.cn:9088/pay/wxpay/payCallback";
    //public static final String overseaCallBackURL = "http://u8s.wgb.cn:9080/pay/wxpay/payCallback";

    public static final String createOrderURL="https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        JSONObject json = JSONObject.fromObject(extension);
        final String accesstoken = json.getString("accesstoken");

        log.info("------> The result of GuestAndWxSDK verify is :" + accesstoken);
        if (!TextUtils.isEmpty(accesstoken)){
            callback.onSuccess(new SDKVerifyResult(true, accesstoken, "",""));
            return;
        }else{
            callback.onFailed(channel.getMaster().getSdkName() + " guest verify failed. the get result is " + accesstoken);
        }
    }

    public static void WxGetOrder(UUser user, UOrder order,boolean isMultiPay, final ISDKOrderListener callback){
        try{
             String appid = order.getChannel().getCpAppID();
             String appKey = order.getChannel().getCpAppKey();
            if(order.getChannel().getPlatType()==1){
                String a  = "{"+appid+"}";
                JSONObject appIDObj = JSONObject.fromObject(a);
                JSONObject appKeyObj = JSONObject.fromObject("{"+appKey+"}");
                appid = appIDObj.getString("WX");
                appKey = appKeyObj.getString("WX");
                log.info("---------------->WxGetOrder,order.getChannel().getPlatType()==1");
            }
            log.info("--------------->WxGetOrder,appId:"+appid+", appKey:"+appKey);

            final String finAppid = appid;
            final String finappKey = appKey;
            String attach = "bsfb";
            String body = order.getChannel().getCpConfig();
            //body = "宝石风暴-连环夺宝2";
            final String mch_id = order.getChannel().getCpPayID();
            final String nonce_str = UUID.randomUUID().toString().replace("-","");
            String notify_url = callBackURL;
            String out_trade_no = order.getOrderID().toString();
            String[] ipArr = UHttpAgent.ServerHost.split("://");
//            String spbill_create_ip = ipArr[1];
            String spbill_create_ip = "120.76.99.32";
            //String spbill_create_ip = UHttpAgent.ServerHost;
            String total_fee = order.getMoney().toString();
            String trade_type = "APP";

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("appid", appid);
            parameters.put("mch_id", mch_id);
            parameters.put("nonce_str", nonce_str);
            parameters.put("body", body);//购买支付信息
            parameters.put("out_trade_no", out_trade_no);//订单号
            parameters.put("total_fee", total_fee);// 总金额单位为分
            //parameters.put("total_fee", "1");// 总金额单位为分
            parameters.put("spbill_create_ip", spbill_create_ip);
            parameters.put("notify_url", notify_url);
            parameters.put("trade_type", trade_type);
            log.info("--------------->WxGetOrder,signParameters:"+parameters.toString()+", appKey:"+appKey);

            String sign = createSign("UTF-8", parameters,appKey);

            parameters.put("sign", sign);
            log.info("--------------->WxGetOrder,parameters:"+parameters.toString());

            String requestXML = getRequestXml(parameters);
            UHttpAgent.getInstance().post(createOrderURL,null,new ByteArrayEntity(requestXML.getBytes(Charset.forName("UTF-8"))),new UHttpFutureCallback(){
                @Override
                public void completed(String result) {

                    try {

                        log.info("---------------->WxGetOrder,the result is "+result);

                        XStream xStream = new XStream();
                        xStream.alias("xml", WXOrderInfo.class);
                        WXOrderInfo authInfo = (WXOrderInfo)xStream.fromXML(result);
                        if(authInfo.getReturn_code().equals("FAIL")){
                            log.info("---------------->WxGetOrder,the result is FAIL,msg:"+authInfo.getReturn_msg());
                            callback.onFailed(authInfo.getReturn_msg());
                        }else{
                            log.info("---------------->WxGetOrder,the result is Success");
                            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
                            params.put("appid", finAppid);
                            params.put("partnerid",mch_id);
                            params.put("prepayid",authInfo.getPrepay_id());
                            params.put("package", "Sign=WXPay");
                            params.put("noncestr", authInfo.getNonce_str());
                            params.put("timestamp", System.currentTimeMillis()/1000);
                            String paySign = createSign("UTF-8", params,finappKey);
                            params.put("paySign", paySign.toLowerCase()); // paySign的生成规则和Sign的生成规则一致
                            String json = JSONObject.fromObject(params).toString();
                            log.info("--------------->WxGetOrder,the json is "+json);
                            callback.onSuccess(json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failed(String e) {
                    callback.onFailed(e);
                }
            });
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        GuestAndWxSDK.WxGetOrder(user,order,false,callback);
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
     * @author steven
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
