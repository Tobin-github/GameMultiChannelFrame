package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * 猎宝SDK支付回调处理类
 * Created by ant on 2016/1/19.
 */
@Controller
@Namespace("/pay/liebao")
public class LieBaoPayCallbackAction extends UActionSupport{

    private String orderid  ;       //猎宝订单号
    private String username ;
    private String gameid  	;
    private String roleid  	;
    private String serverid ;
    private String paytype  ;
    private int amount  	;       //单位为元
    private String paytime  ;
    private String attach  	;
    private String sign  	;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(attach);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null, orderId:" + attach);
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if(channel == null){
                Log.d("the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if(!isSignOK(channel)){
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), orderID);
                this.renderState(false);
                return;
            }

            int moneyInt = amount * 100;  //以分为单位
            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(paytime);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderid);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);

            renderState(true);

        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel){

        StringBuilder sb = new StringBuilder();
        try {
            sb.append("orderid=").append(orderid)
                    .append("&username=").append(username)
                    .append("&gameid=").append(gameid)
                    .append("&roleid=").append(roleid)
                    .append("&serverid=").append(serverid)
                    .append("&paytype=").append(paytype)
                    .append("&amount=").append(amount)
                    .append("&paytime=").append(paytime)
                    .append("&attach=").append(attach)
                    .append("&appkey=").append(URLEncoder.encode(channel.getCpAppKey(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("sign txt:"+sb.toString());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "error";
        }

        renderText(res);
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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
