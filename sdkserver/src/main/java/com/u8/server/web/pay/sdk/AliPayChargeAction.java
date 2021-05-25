package com.u8.server.web.pay.sdk;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.sdk.ali.RSA;
import com.u8.server.service.UOrderManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Created by bsfbadmin on 2017/7/25.
 * 阿里下单
 */
@Controller
@Namespace("/pay")
public class AliPayChargeAction extends UActionSupport {
    private static Logger log = Logger.getLogger(AliPayChargeAction.class.getName());

    private long orderId;   //唯一订单号

    private static final String notify_url = "http://120.25.243.133:8080/pay/alipay/payCallback";
    private static final String new_notify_url = "http://120.25.243.133:8080/pay/alipay/newpayCallback";
    private static final String return_url = "http://120.25.243.133:8080/return.html";

    /*private static final String notify_url = "http://www.wgb.cn:9088/pay/alipay/payCallback";
    private static final String return_url = "http://www.wgb.cn:9088/return.html";*/

    @Autowired
    private UOrderManager orderManager;

    @Action("charge")
    public void echargeByZfb() {
        try {

            UOrder order = orderManager.getOrder(orderId);
            if (null==order) {
                log.error("---------------------->echargeByZfb,Ali charge fail,order is null");
                return;
            }
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

            Map<String,String> params = new HashMap<String, String>();
            params.put("service", "alipay.wap.create.direct.pay.by.user");
            params.put("partner", appID);
            params.put("_input_charset", "UTF-8");
            params.put("sign_type", "RSA");
            params.put("return_url", return_url);
            params.put("notify_url", notify_url);
            params.put("out_trade_no", order.getOrderID()+"");
            params.put("subject", order.getProductName());
            params.put("total_fee", orderAmount);
            //params.put("total_fee", "0.01");
            params.put("seller_id", appID);
            params.put("payment_type", "1");
            params.put("show_url", "http://www.baidu.com");

            //拼接签名参数
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("service", "alipay.wap.create.direct.pay.by.user");
            parameters.put("partner", appID);
            parameters.put("_input_charset", "UTF-8");
            parameters.put("return_url", return_url);
            parameters.put("notify_url", notify_url);
            parameters.put("out_trade_no", order.getOrderID()+"");
            parameters.put("subject", order.getProductName());
            parameters.put("total_fee", orderAmount);
            //parameters.put("total_fee", "0.01");
            parameters.put("seller_id", appID);
            parameters.put("payment_type", "1");
            parameters.put("show_url", "http://www.baidu.com");

            String signStr = generateSign(parameters);
            signStr = RSA.sign(signStr, privateKey, "UTF-8");
            params.put("sign", signStr);
            log.info("---------------------->echargeByZfb,The ios alipay params:" + params.toString());

            UHttpAgent.getInstance().get("https://mapi.alipay.com/gateway.do", params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        log.info("---------------------->echargeByZfb,The ios alipay success:" + result);
                        HttpServletResponse localHttpServletResponse = ServletActionContext.getResponse();
                        if (localHttpServletResponse != null) {
                            localHttpServletResponse.setContentType("text/html;charset=" + "utf-8");
                            localHttpServletResponse.getWriter().write(result);//直接将完整的表单html输出到页面
                            localHttpServletResponse.getWriter().flush();
                            localHttpServletResponse.getWriter().close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("---------------------->echargeByZfb,The ios alipay exception:" + e.getMessage());
                    }

                }

                @Override
                public void failed(String e) {
                    log.error("---------------------->echargeByZfb,ios Ali charge fail");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("---------------------->echargeByZfb,Ali charge Exception:" + e.getMessage());
        }
    }

    @Action("newcharge")
    public void newechargeByZfb() {
        try {

            UOrder order = orderManager.getOrder(orderId);
            if (null==order) {
                log.error("---------------------->newechargeByZfb,Ali charge fail,order is null");
                return;
            }

            log.info("=======日志======== newcharge order:"+order.toJSON());
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

            log.info("=======日志======== newcharge appId:"+appID+",privateKey:"+privateKey+",orderAmount:"+orderAmount);
            // 公共请求参数

            String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwtyDxIpnSSfrFD4QyjlOhNTIDu8oD9EDtYxh2wNyhuMPdwtQC7SSSGtU9oskU6AKfeX/C/QvRebjVT7qQG1ZBHuBw1Eec15sEVprQ46P4WWN7iNTfK8YrPBnn92dsgVSEtxP2ZJKeog3cmjArJjyRwUNuVRnZ748toJrJ8aZ5FtAiCxhkfc4ytsR3YKnxet6v1I9MIkITVT5Aaw0OkEGsQf2YCtY5q5Mvq67r++280OvMZzZ9AEhFg0rvhIStFuglEFf6YPkwFe7Wfo5poELaJ8CFMCFsc7/f0Psra8x6H11LnFdnftc779E5hsS4muVGTsTtJTTZ4dGKOM8rTGiwQIDAQAB";
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appID, privateKey, "json", "utf-8", ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(return_url);
            alipayRequest.setNotifyUrl(new_notify_url);//在公共参数中设置回跳和通知地址
            alipayRequest.setBizContent("{\"out_trade_no\":\""+order.getOrderID()+"\",\"total_amount\":\""+orderAmount+"\"," +
                    "\"subject\":\""+order.getProductName()+"\",\"product_code\":\"QUICK_WAP_PAY\"" +"}");//填充业务参数
            String form="";
            try {
                form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.info("=======日志======== newcharge form:"+form);
            HttpServletResponse localHttpServletResponse = ServletActionContext.getResponse();
            localHttpServletResponse.setContentType("text/html;charset=" + "utf-8");
            localHttpServletResponse.getWriter().write(form);//直接将完整的表单html输出到页面
            localHttpServletResponse.getWriter().flush();
            localHttpServletResponse.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("---------------------->echargeByZfb,Ali charge Exception:" + e.getMessage());
        }
    }

    private String generateSign(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k).append("=").append(v).append("&");
        }
        String signStr=sb.substring(0,sb.length()-1);

        return signStr;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
