package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import com.xyzs.signature.GenSafeSign;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * iOS XY 苹果助手 支付回调处理类
 * Created by ant on 2016/2/24.
 */
@Controller
@Namespace("/pay/xy")
public class XYPayCallbackAction extends UActionSupport{

    private String orderid;
    private String uid;
    private String serverid;
    private String amount;
    private String extra;
    private String ts;
    private String sign;
    private String sig;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            long orderID = Long.parseLong(extra);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                Log.d("The order is null or the channel is null.");
                this.renderState("fail");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState("success");
                return;
            }

            if(isValid(order.getChannel())){
                order.setState(PayState.STATE_SUC);
                order.setCompleteTime(new Date());
                order.setRealMoney((int)(Float.valueOf(amount) * 100));
                order.setChannelOrderID(orderid);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState("success");
            }else{
                order.setState(PayState.STATE_FAILED);
                order.setChannelOrderID(orderid);
                orderManager.saveOrder(order);
                this.renderState("fail");
            }


        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState("fail");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private boolean isValid(UChannel channel) throws Exception {

        Map maps = new TreeMap();
        maps.put("uid",uid);
        maps.put("orderid",orderid);
        maps.put("serverid",serverid);
        maps.put("amount",amount);
        maps.put("extra",extra);
        maps.put("ts", ts);

        // 验证App签名串
        String genSafeSign = GenSafeSign.getGenSafeSign(maps, channel.getCpAppKey());
        if(!genSafeSign.equalsIgnoreCase(sign)) {
            return false;
        }

        // 如果支付签名串存在就验证
        if(sig !=null && !sig.isEmpty()) {
            genSafeSign = GenSafeSign.getGenSafeSign(maps, channel.getCpPayKey());
            if(!genSafeSign.equalsIgnoreCase(sig)) {
                return false;
            }
        }


        return true;
    }

    private void renderState(String resultMsg) throws IOException {

        renderText(resultMsg);

    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
