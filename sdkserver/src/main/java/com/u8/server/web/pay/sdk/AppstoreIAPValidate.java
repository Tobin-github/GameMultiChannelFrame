package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.Base64;
import com.u8.server.utils.StringUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xzy on 15/12/21.
 */
@Controller
@Namespace("/pay/apple")
public class AppstoreIAPValidate extends UActionSupport {

    private static Logger log = Logger.getLogger(AppstoreIAPValidate.class.getName());

    private String orderID;
    private String transactionIdentifier;
    private String productId;
    private String transactionReceipt;

    private String receipt;//apple返回的支付收据

    final String urlSandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
    final String urlProduct = "https://buy.itunes.apple.com/verifyReceipt";

    private UOrder order;

    @Autowired
    private UOrderManager orderManager;

    /**
     * 废弃
     */
    @Action("validate")
    public void validate() {
        try {
            JSONObject params = new JSONObject();

            order = orderManager.getOrder(Long.parseLong(orderID));

            if (transactionReceipt.startsWith("{")) {
                params.put("receipt-data", Base64.encode(transactionReceipt, "UTF-8"));
            } else {
                params.put("receipt-data", transactionReceipt);
            }

            log.debug("------------------------>validate,apple iap validate " + transactionReceipt);

            StringEntity entity = new StringEntity(params.toString(), "UTF-8");
            entity.setContentType("application/json");

            final StringEntity httpParams = entity;

            // TODO: 保存receipt记录

            //首先尝试生产环境请求验证
            //如果返回21007状态，转到sandbox环境验证
            UHttpAgent.getInstance().post(urlProduct, null, httpParams, new UHttpFutureCallback() {
                public void completed(String content) {
                    log.debug("------------------------>validate,apple iap validate suc:" + content);
                    JSONObject json = JSONObject.fromObject(content);

                    if (json.getInt("status") == 21007) {
                        UHttpAgent.getInstance().post(urlSandbox, null, httpParams, new UHttpFutureCallback() {
                            public void completed(String content) {

                                log.debug("apple iap validate suc:" + content);
                                JSONObject json = JSONObject.fromObject(content);

                                if (json.getInt("status") == 0) {
                                    //沙盒环境验证成功
                                    OnValidatedSuccess();
                                } else {
                                    OnValidateFail();
                                }
                            }

                            public void failed(String err) {
                                log.debug("------------------------>validate,apple iap validate error: " + err);
                                //TODO: 更新receipt记录状态
                            }
                        });
                    } else if (json.getInt("status") == 0) {
                        //验证成功
                        OnValidatedSuccess();
                    } else {
                        OnValidateFail();
                    }
                }

                public void failed(String err) {
                    log.debug("------------------------>validate,apple iap validate error: " + err);
                    //TODO: 更新receipt记录状态
                }
            });

            this.renderResponse(null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderError("未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Action("payCallback")
    public void payCallBack() {



        Map<String, String[]> requestParams = request.getParameterMap();
        String jsonStr = "";
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            jsonStr = name + valueStr;
        }

        //IOS传的购买凭据与订单号
        JSONObject json = JSONObject.fromObject(jsonStr);
        String receipt = json.getString("receipt");
        String orderNO = json.getString("orderNO");
        log.info("------------------------>payCallBack,reqJson:" + json.toString());

        if (StringUtils.isEmpty(orderNO) || StringUtils.isEmpty(receipt)) {
            log.error("------------------------>payCallBack,ios 内购客户端回传userId或receipt为空");
            return;
        }

        String receiptJson = sendHttpsCoon(urlSandbox, receipt);
        JSONObject jsonObject = JSONObject.fromObject(receiptJson);
        String status = jsonObject.getString("status");

        if (jsonObject.getInt("status") == 21007) {
            log.info("------------------------>payCallBack,SandboxState:" + status+", now to Product ~~~~~");
            receiptJson = sendHttpsCoon(urlProduct, receipt);
            jsonObject = JSONObject.fromObject(receiptJson);
            status = jsonObject.getString("status");
        }

        log.info("------------------------>payCallBack,receiptState:" + status);

        if ("0".equals(status)) {
            receipt = jsonObject.getString("receipt");
            JSONObject receiptObject = JSONObject.fromObject(receipt);
            long channelOrderId = receiptObject.getLong("transaction_id");
            UOrder payOrder = orderManager.getOrder(Long.parseLong(orderNO));

            String channelOrderID = payOrder.getChannelOrderID();
            log.info("------------------------>payCallBack,receiptOrderId:" + channelOrderId+",order:"+payOrder.toJSON());

            if ("3".equals(payOrder.getState())) {
                try {
                    response.getWriter().write("fail");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ;
            }

            if (StringUtils.isEmpty(channelOrderID)) {
                payOrder.setChannelOrderID(channelOrderId + "");
                payOrder.setCompleteTime(new Date());
                payOrder.setState(PayState.STATE_SUC);
                orderManager.saveOrder(payOrder);

                boolean isSendSuccess = SendAgent.sendCallbackToServer(this.orderManager, payOrder);
                log.info("------------------------>payCallBack,send to game result:" + isSendSuccess);
                try {
                    if (isSendSuccess) {

                        response.getWriter().write("success");
                    } else {
                        response.getWriter().write("fail");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 发送请求
     *
     * @param url
     * @param
     * @return
     */
    private String sendHttpsCoon(String url, String code) {
        if (url.isEmpty()) {
            return null;
        }
        try {
            //设置SSLContext
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, new TrustManager[]{myX509TrustManager}, null);

            //打开连接
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            //设置套接工厂
            conn.setSSLSocketFactory(ssl.getSocketFactory());
            //加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("receipt-data", code);
            log.info("------------------------>sendHttpsCoon,send receipt's data:" + obj.toString());

            BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(obj.toString().getBytes());
            buffOutStr.flush();
            buffOutStr.close();

            //获取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            log.info("------------------------>sendHttpsCoon,response receipt's data:" + sb.toString());
            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 重写X509TrustManager
     */
    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }
    };

    private void OnValidatedSuccess() {
        //TODO: 更新receipt记录状态

        if (order == null || order.getChannel() == null) {
            log.debug("------------------------>OnValidatedSuccess,The order is null or the channel is null.");
            return;
        }

        if (order.getState() > PayState.STATE_PAYING) {
            log.debug("------------------------>OnValidatedSuccess,The state of the order is not paying. The state is " + order.getState());
            return;
        }

        if (order.getExtension() == productId) {
            order.setCompleteTime(new Date());
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);
            SendAgent.sendCallbackToServer(orderManager, order);
        }
    }

    private void OnValidateFail() {
        //TODO: 更新receipt记录状态
        if (order == null || order.getChannel() == null) {
            log.debug("------------------------>OnValidateFail,The order is null or the channel is null.");
            return;
        }

        if (order.getState() > PayState.STATE_PAYING) {
            log.debug("------------------------>OnValidateFail,The state of the order is complete. The state is " + order.getState());
            return;
        }

        if (order.getExtension() == productId) {
            order.setCompleteTime(new Date());
            order.setState(PayState.STATE_FAILED);
            orderManager.saveOrder(order);
        }
    }

    private void renderResponse(JSONObject data) throws IOException {
        try {

            JSONObject json = new JSONObject();
            json.put("state", 1);
            json.put("data", data);

            super.renderJson(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private void renderError(String msg) throws IOException {
        try {

            JSONObject json = new JSONObject();
            json.put("state", -1);
            json.put("message", msg);

            super.renderJson(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void setTransactionReceipt(String transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    public String getTransactionReceipt() {
        return this.transactionReceipt;
    }

    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    public String getTransactionIdentifier() {
        return this.transactionIdentifier;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderID() {
        return this.orderID;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return this.productId;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

}
