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
import java.io.PrintWriter;
import java.util.Date;

/**
 * 偶玩支付回调处理类
 * Created by ant on 2016/1/18.
 */
@Controller
@Namespace("/pay/ouwan")
public class OuWanPayCallbackAction extends UActionSupport{

    private String serverId 	;           //用户登录的游戏服务器ID
    private String callbackInfo ;           //客户端传入的自定义数据
    private String openId 		;           //SDK账号唯一标识
    private String orderId 		;           //SDK订单系统唯一号
    private String orderStatus 	;           //订单状态,1为成功，其他状态均为失败
    private String payType 		;           //充值类型
    private Float amount 		;           //成功充值金额（人民币，单位为元，浮点数）
    private String remark 		;           //备注说明,充值失败时描述失败原因
    private String sign 		;           //充值回调校验签名，参考充值接口的校验


    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(callbackInfo);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null");
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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), callbackInfo);
                this.renderState(false);
                return;
            }

            if("1".equals(this.orderStatus)){

                int moneyInt = (int)(amount * 100);  //以分为单位

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(this.orderId);
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                order.setChannelOrderID(this.orderId);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }

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
        sb.append("amount=").append(this.amount)
                .append("callbackinfo=").append(this.callbackInfo)
                .append("openId=").append(this.openId)
                .append("orderId=").append(this.orderId)
                .append("orderStatus=").append(this.orderStatus)
                .append("payType=").append(this.payType)
                .append("remark=").append(this.remark)
                .append("serverId=").append(this.serverId)
                .append(channel.getCpAppSecret());

        Log.d("sign txt:"+sb.toString());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "fail";
        }

        PrintWriter out = this.response.getWriter();
        out.write(res);
        out.flush();
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getCallbackInfo() {
        return callbackInfo;
    }

    public void setCallbackInfo(String callbackInfo) {
        this.callbackInfo = callbackInfo;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
