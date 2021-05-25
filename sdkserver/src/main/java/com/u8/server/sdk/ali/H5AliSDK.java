package com.u8.server.sdk.ali;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class H5AliSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(H5AliSDK.class.getName());

    private static final String newAliNotifyUrl = "http://120.25.243.133:8080/pay/alipay/newpayCallback";
    public static final String AliReturnUrl = "http://120.25.243.133:8080/return.html";

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


    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try {

            if (null==order) {
                log.error("----------------------->onGetOrderID,Ali charge fail,order is null");
                return;
            }

            log.info("=======日志======== onGetOrderID order:"+order.toJSON());
            String appID = order.getChannel().getCpAppID();
            String privateKey = order.getChannel().getCpPayPriKey();



            if (order.getChannel().getPlatType() == 1) {
                JSONObject appIDObj = JSONObject.fromObject("{" + appID + "}");
                JSONObject priKeyObj = JSONObject.fromObject("{" + privateKey + "}");
                appID = appIDObj.getString("Ali");
                privateKey = priKeyObj.getString("Ali");
            }

            DecimalFormat df=new DecimalFormat("0.00");
            String orderAmount =df.format(order.getMoney()/100.00);
            log.info("=======日志======== onGetOrderID appId:"+appID+",privateKey:"+privateKey+",orderAmount:"+orderAmount);
            // 公共请求参数

            String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwtyDxIpnSSfrFD4QyjlOhNTIDu8oD9EDtYxh2wNyhuMPdwtQC7SSSGtU9oskU6AKfeX/C/QvRebjVT7qQG1ZBHuBw1Eec15sEVprQ46P4WWN7iNTfK8YrPBnn92dsgVSEtxP2ZJKeog3cmjArJjyRwUNuVRnZ748toJrJ8aZ5FtAiCxhkfc4ytsR3YKnxet6v1I9MIkITVT5Aaw0OkEGsQf2YCtY5q5Mvq67r++280OvMZzZ9AEhFg0rvhIStFuglEFf6YPkwFe7Wfo5poELaJ8CFMCFsc7/f0Psra8x6H11LnFdnftc779E5hsS4muVGTsTtJTTZ4dGKOM8rTGiwQIDAQAB";
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appID, privateKey, "json", "utf-8", ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(AliReturnUrl);
            alipayRequest.setNotifyUrl(newAliNotifyUrl);//在公共参数中设置回跳和通知地址
            alipayRequest.setBizContent("{\"out_trade_no\":\""+order.getOrderID()+"\",\"total_amount\":\""+orderAmount+"\"," +
                    "\"subject\":\""+order.getProductName()+"\",\"product_code\":\"QUICK_WAP_PAY\"" +"}");//填充业务参数
            String form="";
            try {
                form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.info("=======日志======== onGetOrderID form:"+form);
            HttpServletResponse localHttpServletResponse = ServletActionContext.getResponse();
            localHttpServletResponse.setContentType("text/html;charset=" + "utf-8");
            localHttpServletResponse.getWriter().write(form);//直接将完整的表单html输出到页面
            localHttpServletResponse.getWriter().flush();
            localHttpServletResponse.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("---------------------->onGetOrderID,Ali charge Exception:" + e.getMessage());
        }

    }

}
