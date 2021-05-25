package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.nubia.MD5Util;
import com.u8.server.sdk.nubia.ParameterUtil;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 努比亚支付回调通知接口
 * Created by ant on 2015/4/22.
 */
@Controller
@Namespace("/pay/nubia")
public class NubiaPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(NubiaPayCallbackAction.class.getName());

    private String order_no  	    ;				//游戏订单号
    private String data_timestamp ; 				//支付通知时间
    private String pay_success      ;				//支付成功与否(成功为1)
    private String sign             ;				//支付通知签名（包含参数order_no、data_timestamp，具体规则见2.4，该签名为旧版使用，新接入的应用统一使用order_sign来校验）
    private String app_id  		    ;				//应用id
    private String uid  		    ;				//登录时获取的uid
    private String amount  		    ;				//支付金额（精确到分）
    private String product_name  	;				//商品名称
    private String product_des  	;				//商品描述
    private String number  		    ;				//商品数量
    private String order_serial  	;				//Nubia支付的订单号
    private String order_sign  		;				//支付通知签名（包含参数order_no、data_timestamp、pay_success、app_id、uid、amount、product_name、product_des、number

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("NubiaPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long orderID = Long.parseLong(order_no);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                log.error("------------------------>The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("------------------------>The state of the order is complete. The state is "+order.getState());
                this.renderState(false, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            log.debug("order:" + order.toJSON());

            DecimalFormat df=new DecimalFormat("0.00");
            String orderAmount =df.format(order.getMoney()/100.00);

            if (!amount.equals(orderAmount)) {
                log.error("----------------------->The order money error,money:"+order.getMoney()+",channelMoney:"+amount);
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(isValid(order.getChannel())){
                log.debug("ready send to game");
                order.setRealMoney((int)Double.parseDouble(amount)*100);
                order.setSdkOrderTime(data_timestamp);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(order_serial);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.debug("sign fail");
                order.setChannelOrderID(order_serial);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign   错误");
            }


        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel){

        try{

            String u8AppId = channel.getCpAppID();
            String u8SecretKey = channel.getCpAppSecret();

            Map<String, String> requestMap = new HashMap<String, String>();
            requestMap.put("order_no", order_no);
            requestMap.put("data_timestamp", data_timestamp);
            requestMap.put("pay_success", pay_success);
            requestMap.put("app_id", app_id);
            requestMap.put("uid", uid);
            requestMap.put("amount", amount);
            requestMap.put("product_name", product_name);
            requestMap.put("product_des", product_des);
            requestMap.put("number", number);

            log.debug("signParamMap:" + requestMap.toString());

            String callBackSign = generateSign(requestMap, u8AppId, u8SecretKey);

            if (order_sign.equals(callBackSign)) {
                return true;
            }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return false;

    }

    private String generateSign(Map<String, String> requestMap, String appId, String secretKey) {
        String unSignData = ParameterUtil.getSignData(requestMap);
        String param = ":" + appId + ":" + secretKey;
        String sign = "";
        try {
            sign = MD5Util.sign(unSignData, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    private void renderState(boolean suc, String msg) throws IOException {

        StringBuilder sb = new StringBuilder();


        sb.append("result=").append(suc ? "OK" : "FAIL");
        sb.append("&").append("resultMsg=").append(msg);

        Log.d("The result to sdk is "+sb.toString());

        PrintWriter out = this.response.getWriter();
        out.write(sb.toString());
        out.flush();


    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getData_timestamp() {
        return data_timestamp;
    }

    public void setData_timestamp(String data_timestamp) {
        this.data_timestamp = data_timestamp;
    }

    public String getPay_success() {
        return pay_success;
    }

    public void setPay_success(String pay_success) {
        this.pay_success = pay_success;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_des() {
        return product_des;
    }

    public void setProduct_des(String product_des) {
        this.product_des = product_des;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrder_serial() {
        return order_serial;
    }

    public void setOrder_serial(String order_serial) {
        this.order_serial = order_serial;
    }

    public String getOrder_sign() {
        return order_sign;
    }

    public void setOrder_sign(String order_sign) {
        this.order_sign = order_sign;
    }
}
