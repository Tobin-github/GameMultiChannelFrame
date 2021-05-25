package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.pp.RSAEncrypt;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * iOS PP助手支付回调处理类
 * Created by ant on 2016/2/25.
 */
@Controller
@Namespace("/pay/pp")
public class PPPayCallbackAction extends UActionSupport{

    private String order_id;
    private String billno  ;
    private String account ;
    private String amount  ;
    private String status  ;
    private String app_id  ;
    private String uuid    ;
    private String roleid  ;
    private String zone    ;
    private String sign    ;

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

    public static void main(String[] args) throws Exception {


        String data = "ZMSizwCdeSeeeOhUT4tI1qnVseni6MV+jiUwC7uWctD40GIKXK0r3JPphHdmLRzmo1/VSoce+d9OPhleqTY7hTgGwIvWG9C5MABziSzwxOn8GGsYsXLpCRLuQsw2XwR6n6Jt0k3kQzx3PM0VG6V0BV3FB34qxxpUcBopircLKZuKaHbyO+PpZWdio6FEpq6DszD8XEBJ0/daTFC0jNCcF1wPZROhMDOAbCqQebOXjNUowl6wMWpZZi/624aYvFU21pnRyEteZupmBIrqnj6Jsn5qdG3ucf7ahD2pi6bCMW146l7hJWiEfMq3K5Oqks0LUh5LdS1BLAhGTZA8khVZrg==";
        Map<String, String > params = new HashMap<String, String>();
        params.put("sign", data);
        params.put("billno", "654898395705507841");
        UHttpAgent.newInstance().post("http://localhost:8080/pay/pp/payCallback", params);

    }

    private boolean isValid(UChannel channel) throws Exception {

        RSAEncrypt rsaEncrypt= new RSAEncrypt();
        //rsaEncrypt.genKeyPair();

        //加载公钥
        try {
            rsaEncrypt.loadPublicKey(channel.getCpPayKey());
            Log.d("加载公钥成功");
        } catch (Exception e) {
            Log.e(e.getMessage());
            Log.e("加载公钥失败");
        }


        try {

            BASE64Decoder base64Decoder = new BASE64Decoder();

            byte[] dcDataStr = base64Decoder.decodeBuffer(this.sign);
            byte[] plainData = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), dcDataStr);
            Log.d("文档测试数据明文长度:" + plainData.length);
            Log.d(RSAEncrypt.byteArrayToString(plainData));
            Log.d(new String(plainData));

            JSONObject json = JSONObject.fromObject(new String(plainData));
            String cBillno = "";
            String cAmount = "";
            String cStatus = "";
            if(json.containsKey("billno")){
                cBillno = json.getString("billno");

            }

            if(json.containsKey("amount")){
                cAmount = json.getString("amount");
            }

            if(json.containsKey("status")){
                cStatus = json.getString("status");
            }

            return cBillno.equals(this.billno) && cAmount.equals(this.amount) && cStatus.equals(this.status);

        } catch (Exception e) {
            Log.e(e.getMessage());
        }

        return  false;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
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
