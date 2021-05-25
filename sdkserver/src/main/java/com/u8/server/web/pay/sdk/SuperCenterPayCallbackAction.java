package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 中超中心
 * Created by ant on 2015/9/15.
 */
@Controller
@Namespace("/pay/supercenter")
public class SuperCenterPayCallbackAction extends UActionSupport {

    private static Logger log = Logger.getLogger(SuperCenterPayCallbackAction.class.getName());

    private String orderid;            //Wancms订单号
    private String username;            //Wancms 登录账号
    private String gameid;            //游戏 ID
    private String roleid;            //游戏角色 ID
    private String serverid;            //服务器 ID
    private String paytype;            //支付类型
    private String amount;            //成功充值金额，单位(元)
    private String paytime;            //玩家充值时间，时间戳形式，如 1394087000
    private String attach;            //商户拓展参数
    private String sign;            //参数签名（用于验签对比）

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback() {
        try {

            Enumeration pNames = request.getParameterNames();
            Map<String, String> params = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                params.put(name, value);
                log.debug("SuperCenterPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long orderID = Long.parseLong(this.attach);

            UOrder order = orderManager.getOrder(orderID);

            if (order == null || order.getChannel() == null) {
                log.error("--------->payCallback ,order is null and channel is null,orderId:" + orderID);
                this.renderState(false);
                return;
            }
            log.info("--------->order:" + order.toJSON());

            UChannel channel = order.getChannel();

            if (order.getState() > PayState.STATE_PAYING) {
                log.error("-------->payCallback ,order state is successed,order state:" + order.getState());
                this.renderState(true);
                return;
            }

            if (!isSignOK(channel)) {
                log.error("-------->payCallback ,The sign verify failed.sign:" + sign);
                this.renderState(false);
                return;
            }

            float money = Float.parseFloat(this.amount);
            int moneyInt = (int) (money * 100);  //以分为单位

            if (moneyInt != order.getMoney()) {
                log.error("------------->order money error,money:" + order.getMoney() + ", channelMoney:" + moneyInt);
                this.renderState(false);
                return;
            }

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(paytime);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderid);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);
            this.renderState(true);
            log.info("--------->payCallback ,send to game:" + order.toJSON());
            SendAgent.sendCallbackToServer(this.orderManager, order);


        } catch (Exception e) {
            log.error("-------->payCallback exception,msg:" + e.getMessage());
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    private boolean isSignOK(UChannel channel) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("orderid=").append(URLEncoder.encode(orderid,"UTF-8")).append("&")
                .append("username=").append(URLEncoder.encode(username,"UTF-8")).append("&")
                .append("gameid=").append(gameid).append("&")
                .append("roleid=").append(URLEncoder.encode(roleid,"UTF-8")).append("&")
                .append("serverid=").append(serverid).append("&")
                .append("paytype=").append(URLEncoder.encode(paytype,"UTF-8")).append("&")
                .append("amount=").append(amount).append("&")
                .append("paytime=").append(paytime).append("&")
                .append("attach=").append(URLEncoder.encode(attach,"UTF-8")).append("&")
                .append("appkey=").append(channel.getCpAppKey());
        log.info("--------->payCallback ,The unSignStr:" + sb.toString());
        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();
        log.info("-------->payCallback ,The signStr:" + md5 + ",channelSign:" + sign);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String respStr = "";
        if (suc) {
            respStr = "success";
        } else {
            respStr = "error";
        }

        PrintWriter out = this.response.getWriter();
        out.write(respStr);
        out.flush();
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
        this.paytime = paytime;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
