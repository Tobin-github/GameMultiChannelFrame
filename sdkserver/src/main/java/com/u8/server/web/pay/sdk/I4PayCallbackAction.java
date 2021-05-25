package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.i4.PayService;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * iOS渠道i4支付回调类
 * Created by ant on 2016/2/24.
 */

@Controller
@Namespace("/pay/i4")
public class I4PayCallbackAction extends UActionSupport{

    private String order_id;
    private String billno;
    private String account;
    private String amount;
    private String status;
    private String app_id;
    private String role;
    private String zone;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{


            long orderID = Long.parseLong(billno);

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

            if(!order.getChannel().getCpAppID().equals(this.app_id)){
                Log.d("The app_id of the order is invalid. The app_id is "+this.app_id+"; the valid appId is "+order.getChannel().getCpAppID());
                this.renderState("fail");
                return;
            }

            if(!this.status.equals("0")){
                Log.d("The state of the order return from sdk server is error. The status is " + order.getState());
                this.renderState("fail");
                return;
            }

            if(isValid(order.getChannel())){
                order.setState(PayState.STATE_SUC);
                order.setCompleteTime(new Date());
                order.setRealMoney((int)(Float.valueOf(amount) * 100));
                order.setChannelOrderID(order_id);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState("success");
            }else{
                order.setState(PayState.STATE_FAILED);
                order.setChannelOrderID(order_id);
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

        Map<String, String> params = new HashMap<String, String>();
        params.put("account", account);
        params.put("amount", amount);
        params.put("app_id", app_id);
        params.put("billno", billno);
        params.put("order_id", order_id);
        params.put("role", role);
        params.put("status", status);
        params.put("zone", zone);
        params.put("sign", sign);

        return PayService.verifySignature(params, channel.getCpPayKey());
    }

    private void renderState(String resultMsg) throws IOException {

        renderText(resultMsg);

    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
