package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.anfeng.AnFengSDK;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 安峰(56Game)渠道支付回调处理类
 * Created by ant on 2015/12/3.
 */
@Controller
@Namespace("/pay/anfeng")
public class AnFengPayCallbackAction extends UActionSupport{

    private String uid;
    private String ucid;
    private String body;
    private String fee;
    private String subject;
    private String vid;
    private String sn;
    private String vorderid;
    private String createTime;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(this.vorderid);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null or the channel is null.orderID:%s", orderID);
                this.renderState(false);
                return;
            }

            UChannel channel = channelManager.queryChannel(order.getChannelID());
            if(channel == null){
                Log.d("The channel is not exists of channelID:"+order.getChannelID());
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. orderID:%s;state:%s" , orderID, order.getState());
                this.renderState(true);
                return;
            }

            if(!isSignOK(channel)){
                Log.d("the sign is not valid. sign:%s;orderID:%s", sign, vorderid);
                this.renderState(false);
                return;
            }

            int money = (int)(Float.valueOf(fee) * 100);
            order.setRealMoney(money);
            order.setSdkOrderTime(createTime);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(vid);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            this.renderState(true);

            SendAgent.sendCallbackToServer(this.orderManager, order);


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
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("ucid", ucid);
        params.put("body", body);
        params.put("fee", fee);
        params.put("subject", subject);
        params.put("vid", vid);
        params.put("sn", sn);
        params.put("vorderid", vorderid);
        params.put("createTime", createTime);

        String signLocal = AnFengSDK.generateSign(params, channel.getCpAppKey());
        Log.d("the sign local is :"+signLocal);

        return signLocal.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        PrintWriter out = this.response.getWriter();

        if(suc){
            out.write("SUCCESS");
        }else{
            out.write("FAILED");
        }
        out.flush();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVorderid() {
        return vorderid;
    }

    public void setVorderid(String vorderid) {
        this.vorderid = vorderid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
