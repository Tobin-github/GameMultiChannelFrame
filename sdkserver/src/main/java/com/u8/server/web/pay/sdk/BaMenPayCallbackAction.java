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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 八门 SDK支付回调处理类
 * Created by ant on 2018/01/22.
 */
@Controller
@Namespace("/pay/bamen")
public class BaMenPayCallbackAction extends UActionSupport {

    @Autowired
    private UOrderManager orderManager;

    private static Logger logger = Logger.getLogger(BaMenPayCallbackAction.class.getName());

    @Action("payCallback")
    public void payCallback() {
        try {

            InputStream inputStream = request.getInputStream();

            String jsonStr = inputStream2String(inputStream);
            logger.debug("=========日志=========    payBack str:" + jsonStr);

            JSONObject json = new JSONObject(jsonStr);
            String order_no = json.getString("order_no");
            String game_order_no = json.getString("game_order_no");
            String uid = json.getString("uid");
            String pay_money = json.getString("pay_money");
            String service_id = json.getString("service_id");
            String pid = json.getString("pid");
            String paystatus = json.getString("paystatus");
            String time = json.getString("time");
            String sign = json.getString("sign");

            if (!"1".equals(paystatus)) {
                logger.error("The paystatus error,paystatus:" + paystatus);
                this.renderState(false, "paystatus 错误");
                return;
            }

            long localOrderID = Long.parseLong(game_order_no);

            UOrder order = orderManager.getOrder(localOrderID);

            if (order == null || order.getChannel() == null) {
                logger.error("The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            logger.debug("------------------> order:" + order.toJSON());

            if (order.getState() > PayState.STATE_PAYING) {
                logger.debug("The state of the order is complete. The state is " + order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            String appKey = order.getChannel().getCpAppSecret();
            int moneyInt = (int) (Math.round(Double.parseDouble(pay_money) * 100));  //以分为单位

            /*if (order.getMoney() != moneyInt) {
                logger.error("订单金额不一致! local orderID:" + localOrderID + "; money returned:" + moneyInt + "; order money:" + order.getMoney());
                this.renderState(false, "订单金额与支付金额不一致");
                return;
            }*/

            if (isValid(jsonStr, appKey)) {
                logger.debug("-------> callback signed successful");

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(time);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(order_no);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            } else {
                logger.error("-------> callback signed fail");
                order.setChannelOrderID(order_no);
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
        String order_no = null, game_order_no = null, uid = null, pay_money = null, service_id = null, pid = null, time = null, paystatus = null, sign = null;

        try {
            json = new JSONObject(jsonStr);
            order_no = json.getString("order_no");
            game_order_no = json.getString("game_order_no");
            uid = json.getString("uid");
            pay_money = json.getString("pay_money");
            service_id = json.getString("service_id");
            pid = json.getString("pid");
            time = json.getString("time");
            paystatus = json.getString("paystatus");
            sign = json.getString("sign");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(order_no)
                .append(game_order_no)
                .append(uid)
                .append(pay_money)
                .append(service_id)
                .append(pid)
                .append(time)
                .append(paystatus)
                .append(appKey);

        logger.debug("----------> callback sign str is " + sb.toString());
        String signLocal = EncryptUtils.md5(sb.toString()).toLowerCase();
        logger.debug("----------> callback signed str is " + signLocal);

        return signLocal.equals(sign);
    }

    private void renderState(boolean suc, String msg) throws IOException {

        JSONObject json = new JSONObject();

        try {

            if (suc) {
                json.put("code", "1");
                json.put("msg", msg);
            } else {
                json.put("code", "0");
                json.put("msg", msg);
            }

            renderJson(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
