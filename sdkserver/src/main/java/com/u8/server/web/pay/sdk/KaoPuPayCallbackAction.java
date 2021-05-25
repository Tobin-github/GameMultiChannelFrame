package com.u8.server.web.pay.sdk;

import com.alibaba.fastjson.JSONObject;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

/**
 * 靠谱助手支付回调类
 * Created by xiaohei on 16/10/16.
 */
@Controller
@Namespace("/pay/kaopu")
public class KaoPuPayCallbackAction extends UActionSupport {

    private static Logger log = Logger.getLogger(KaoPuPayCallbackAction.class.getName());

    private String username;         //用户名
    private String kpordernum;       //靠谱订单号
    private String ywordernum;       //用户订单号
    private String status;           //订单状态
    private String paytype;           //支付方式
    private String amount;           //金额(分)
    private String gameserver;       //服务器
    private String errdesc;          //错误描述
    private String paytime;          //支付时间
    private String gamename;         //游戏名称
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(this.ywordernum);

            UOrder order = orderManager.getOrder(orderID);
            log.info("--------------->order:"+order.toJSON());

            if(order == null){
                log.error("--------------->The order is null or the channel is null.orderID:"+orderID);
                this.renderState(false, null);
                return;
            }

            UChannel channel = channelManager.queryChannel(order.getChannelID());
            log.info("--------------->channel:"+channel.toJSON());

            if(channel == null){
                log.error("--------------->The channel is not exists of channelID:"+order.getChannelID());
                this.renderState(false, null);
                return;
            }

            if(!"1".equals(status)){
                log.error("--------------->the order is not success from chongchong server. statusCode:"+status+", orderID:"+orderID);
                this.renderState(false,null);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                log.error("--------------->The state of the order is complete. orderID:" + orderID + ", state:" + order.getState());
                this.renderState(true, channel);
                return;
            }

            if(!isSignOK(channel)){
                log.error("--------------->the sign is not valid. sign:" + sign + ", orderID:" + kpordernum);
                this.renderState(false,channel);
                return;
            }

            int money = (int)(1*Float.valueOf(amount));

            order.setRealMoney(money);
            order.setSdkOrderTime(paytime);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(kpordernum);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            this.renderState(true,channel);

            SendAgent.sendCallbackToServer(this.orderManager, order);


        }catch(Exception e){
            try {
                renderState(false, null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel){

        StringBuilder sb = new StringBuilder();
        sb.append(username).append("|")
                .append(kpordernum).append("|")
                .append(ywordernum).append("|")
                .append(status).append("|")
                .append(paytype).append("|")
                .append(amount).append("|")
                .append(gameserver).append("|")
                .append(errdesc).append("|")
                .append(paytime).append("|")
                .append(gamename).append("|")
                .append(channel.getCpAppSecret());

        String md5Local = EncryptUtils.md5(sb.toString()).toLowerCase();

        log.info("--------------->kaopu check sign orig str:" + sb.toString());

        return md5Local.equals(this.sign);

    }

    private void renderState(boolean suc, UChannel channel) throws IOException {

        try{
            String code = suc ? "1000" : "1005";
            String msg = suc ? "处理成功" : "处理失败";
            String sign = channel == null ? "": EncryptUtils.md5(code+"|"+channel.getCpAppSecret());

            JSONObject json = new JSONObject();
            json.put("code", code);
            json.put("msg", msg);
            json.put("sign", sign);
            log.info("--------------->respJson:"+json.toString());

            renderJson(json.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKpordernum() {
        return kpordernum;
    }

    public void setKpordernum(String kpordernum) {
        this.kpordernum = kpordernum;
    }

    public String getYwordernum() {
        return ywordernum;
    }

    public void setYwordernum(String ywordernum) {
        this.ywordernum = ywordernum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getErrdesc() {
        return errdesc;
    }

    public void setErrdesc(String errdesc) {
        this.errdesc = errdesc;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
        this.paytime = paytime;
    }

    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getGameserver() {
        return gameserver;
    }

    public void setGameserver(String gameserver) {
        this.gameserver = gameserver;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
