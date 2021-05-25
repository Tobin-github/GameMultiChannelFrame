package com.u8.server.web.pay.sdk;

import com.alipay.api.internal.util.AlipaySignature;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.RSAUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 游龙SDK支付回调处理类
 * Created by ant on 2016/1/19.
 */

@Controller
@Namespace("/pay/alipay")
public class AliPayCallbackAction extends UActionSupport {

    private static Logger log = Logger.getLogger(AliPayCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback() {
        try {

            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            log.info("--------------->ali params:" + params.toString());

            String out_trade_no = request.getParameter("out_trade_no");// 商户订单号
            String total_amount = request.getParameter("total_fee");// 订单金额


            long orderID = Long.parseLong(out_trade_no);
            UOrder order = orderManager.getOrder(orderID);
            String pubKey = order.getChannel().getCpPayKey();
            if (order == null) {
                log.error("--------------->The order is null, orderId:" + orderID);
                this.renderState(false);
                return;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            String orderMoney = df.format(order.getMoney() / 100.00);

            if (!orderMoney.equals(total_amount)) {
                log.error("--------------->The order money error,money" + orderMoney + ",channelMoney:" + total_amount);
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if (channel == null) {
                log.error("--------------->the channel is null.");
                this.renderState(false);
                return;
            }

            String trade_status = request.getParameter("trade_status");
            if (trade_status.equals("TRADE_CLOSED")) {
                log.error("--------------->trade_status error:" + trade_status);
                this.renderState(false);
                return;
            }

            if (trade_status.equals("WAIT_BUYER_PAY")) {
                log.error("--------------->trade_status error:" + trade_status);
                this.renderState(false);
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                log.error("--------------->The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if (!rsaCheckV1(params, pubKey, "UTF-8", "SHA1withRSA")) {
                log.error("--------------->The sign verify failed;appKey:" + channel.getCpPayKey() + ",orderID:" + orderID);
                this.renderState(false);
                return;
            }

            int moneyInt = (int) (Float.parseFloat(total_amount) * 100);  //以分为单位
            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID("");
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);

            renderState(true);
        } catch (Exception e) {
            log.error("--------------->ali payback exception,msg:" + e.getMessage());
            try {
                renderState(false);
            } catch (IOException e1) {
                log.error("--------------->ali payback exception-1,msg:" + e.getMessage());
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Action("newpayCallback")
    public void newpayCallback() {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> paramsMap = request.getParameterMap();
            Map<String, String[]> requestParams = request.getParameterMap();

            Map<String, String> params = new HashMap<>();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                log.info("====日志==== key:" + name+",value:"+valueStr);
                params.put(name, valueStr);
            }
            String out_trade_no = request.getParameter("out_trade_no");// 商户订单号
            String total_amount = request.getParameter("total_amount");// 订单金额
            String sign = request.getParameter("sign");

            log.info("====日志==== total_amount:" + total_amount);

            long orderID = Long.parseLong(out_trade_no);
            UOrder order = orderManager.getOrder(orderID);
            String pubKey = order.getChannel().getCpPayKey();
            if (order == null) {
                log.error("--------------->The order is null, orderId:" + orderID);
                this.renderState(false);
                return;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            String orderMoney = df.format(order.getMoney() / 100.00);

            log.info("====日志2==== orderMoney:" + orderMoney);

            if (!orderMoney.equals(total_amount)) {
                log.error("--------------->The order money error,money" + orderMoney + ",channelMoney:" + total_amount);
                this.renderState(false);
                return;
            }
            log.info("====日志3==== ");

            UChannel channel = order.getChannel();
            if (channel == null) {
                log.error("--------------->the channel is null.");
                this.renderState(false);
                return;
            }
            log.info("====日志4==== ");

            String trade_status = request.getParameter("trade_status");
            if (trade_status.equals("TRADE_CLOSED")) {
                log.error("--------------->trade_status error:" + trade_status);
                this.renderState(false);
                return;
            }
            log.info("====日志5==== ");

            if (trade_status.equals("WAIT_BUYER_PAY")) {
                log.error("--------------->trade_status error:" + trade_status);
                this.renderState(false);
                return;
            }

            log.info("====日志6==== ");

            if (order.getState() > PayState.STATE_PAYING) {
                log.error("--------------->The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            log.info("====日志7==== paramsMap:" +params.toString());

            log.info("====日志7-2==== pubKey:" + pubKey);

            boolean signVerified = AlipaySignature.rsaCheckV1(params, pubKey, "utf-8", "RSA2"); //调用SDK验证签名
            if (!signVerified) {
                log.error("--------------->The sign verify failed;appKey:" + channel.getCpPayKey() + ",orderID:" + orderID);
                this.renderState(false);
                return;
            }

            log.info("====日志8==== ");

            int moneyInt = (int) (Float.parseFloat(total_amount) * 100);  //以分为单位
            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID("");
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);

            renderState(true);
        } catch (Exception e) {
            log.error("--------------->ali payback exception,msg:" + e.getMessage());
            try {
                renderState(false);
            } catch (IOException e1) {
                log.error("--------------->ali payback exception-1,msg:" + e.getMessage());
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    public static boolean rsaCheckV1(Map<String, String> params, String publicKey, String charset, String signType) {
        String sign = (String) params.get("sign");
        String content = getSignCheckContentV1(params);
        log.info("--------------->content:" + content + ",channelSign:" + sign);
        return RSAUtils.verify(content, sign, publicKey, charset, signType);
    }


    public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign");
        params.remove("sign_type");

        StringBuffer content = new StringBuffer();
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }

    private boolean isSignOK(UChannel channel, String sign, SortedMap<Object, Object> parameters) {

        String signLocal = createSign("UTF-8", parameters, channel.getCpAppKey());

        return signLocal.equals(sign);
    }

    /**
     * @param characterEncoding 编码格式
     * @param parameters        请求参数
     * @return
     * @author lwz
     * @date 2014-12-8
     * @Description：sign签名
     */
    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters, String API_KEY) {
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


    private void renderState(boolean suc) throws IOException {

        String res = "OK";
        if (!suc) {
            res = "NO";
        }

        renderText(res);
    }
}
