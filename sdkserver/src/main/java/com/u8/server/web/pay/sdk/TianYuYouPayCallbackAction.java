package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.*;

/**
 * SDK支付回调处理类
 * Created by ant on 2016/5/10.
 * http://localhost:8080/pay/sougou/payCallback
 */
@Controller
@Namespace("/pay/tianyuyou")
public class TianYuYouPayCallbackAction extends UActionSupport {

    @Autowired
    private UOrderManager orderManager;

    private static Logger logger = Logger.getLogger(TianYuYouPayCallbackAction.class.getName());

    @Action("payCallback")
    public void payCallback() {
        try {

            InputStream inputStream = request.getInputStream();

            String jsonStr = inputStream2String(inputStream);
            logger.debug("payBack str:"+jsonStr);

            JSONObject json = new JSONObject(jsonStr);
            String order_id = json.getString("order_id");
            String money = json.getString("money");
            String paytime = json.getString("paytime");
            String attach = json.getString("attach");
            String order_status = json.getString("order_status");

            if (order_status.equals("1")) {
                logger.debug("The order_status error,order_status:"+order_status);
                this.renderState(false, "order_status 错误");
                return;
            }

            if (order_status.equals("3")) {
                logger.debug("The order_status-2 error,order_status:"+order_status);
                this.renderState(false, "order_status2 错误");
                return;
            }

            long localOrderID = Long.parseLong(attach);

            UOrder order = orderManager.getOrder(localOrderID);

            if (order == null || order.getChannel() == null) {
                logger.debug("The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                logger.debug("The state of the order is complete. The state is " + order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            String appKey = order.getChannel().getCpAppKey();
            int realMoney = (int) (Double.parseDouble(money) * 100);      //转换为分

            if (order.getMoney() != realMoney) {
                logger.error("订单金额不一致! local orderID:" + localOrderID + "; money returned:" + realMoney + "; order money:" + order.getMoney());
                this.renderState(false, "TianYuYouSDK 订单金额与支付金额不一致");
                return;
            }

            if (isValid(jsonStr, appKey)) {

                order.setRealMoney(realMoney);
                order.setSdkOrderTime(paytime);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(order_id);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                logger.error("TianYuYouSDK callback signed successful");
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            } else {
                order.setChannelOrderID(order_id);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "auth 错误");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    private boolean isValid(String jsonStr, String appKey) throws UnsupportedEncodingException {

        JSONObject json = null;
        String order_id = null, mem_id = null, app_id = null, money = null, order_status = null, paytime = null, attach = null, sign = null;

        try {
            json = new JSONObject(jsonStr);
            order_id = json.getString("order_id");
            mem_id = json.getString("mem_id");
            app_id = json.getString("app_id");
            money = json.getString("money");
            order_status = json.getString("order_status");
            paytime = json.getString("paytime");
            attach = json.getString("attach");
            sign = json.getString("sign");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("order_id=").append(order_id).append("&")
                .append("mem_id=").append(mem_id).append("&")
                .append("app_id=").append(app_id).append("&")
                .append("money=").append(money).append("&")
                .append("order_status=").append(order_status).append("&")
                .append("paytime=").append(paytime).append("&")
                .append("attach=").append(attach).append("&app_key=")
                .append(appKey);

        logger.debug("TianYuYouSDK callback sign str is " + sb.toString());
        String signLocal = EncryptUtils.md5(sb.toString()).toLowerCase();
        logger.debug("TianYuYouSDK callback signed str is " + signLocal);

        return signLocal.equals(sign);
    }

    private void renderState(boolean suc, String msg) throws IOException {

        PrintWriter out = this.response.getWriter();
        if (suc) {
            out.write("SUCCESS");
        } else {
            out.write("FAILURE");
        }
        out.flush();
    }

}
