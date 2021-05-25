package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.huawei.RSA;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.URLDecoder;
import java.util.Date;

/**
 * 华为支付回调通知接口
 * Created by ant on 2015/4/27.
 */
@Controller
@Namespace("/pay/singlehuawei")
public class SingleHuaWeiPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleHuaWeiPayCallbackAction.class.getName());


    private String result  			;		//string(3)  支付结果：“0”：支付成功“1”：退款成功（暂未启用）
    private String userName  		;		//string  开发者社区用户名或联盟用户编号；终端sdk上报了联盟用户编号
    private String productName  	;		//string(100)  商品名称；对应APK填写的productName
    private String payType  		;		//int
    private String amount  			;		//string(10)  商品支付金额 (格式为：元.角分，最小精确到分，例如：20.01)
    private String orderId  		;		//string(50)  华为订单号，为华为支付平台生成
    private String notifyTime  		;		//string(15)  通知时间，自1970年1月1日0时起的毫秒数
    private String requestId  		;		//string(30)  开发者支付请求ID
    private String bankId  			;		//string(50)  银行编码-支付通道信息，传递条件如下：只有在有具体取值时才传递
    private String orderTime  		;		//string  下单时间 yyyy-MM-dd hh:mm:ss；仅在sdk中指定了urlver为2 时有效。
    private String tradeTime  		;		//string  交易/退款时间 yyyy-MM-dd hh:mm:ss；仅在sdk中指定了urlver为2时有效。
    private String accessMode  		;		//string  接入方式：仅在sdk 中指定了urlver为2时有效。
    private String spending  		;		//string  渠道开销，保留两位小数，单位元。仅在sdk中指定了urlver为2时有效。
    private String extReserved  	;		//string  商户侧保留信息，原样返回商户调用支付sdk
    private String sysReserved      ;       //string 系统保留信息
    private String sign  			;		//string  RSA签名。

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            long localOrderID = Long.parseLong(requestId);

            UOrder order = orderManager.getOrder(localOrderID);

            if(order == null || order.getChannel() == null){
                log.error("------------>The order is null or the channel is null.");
                this.renderState(3, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("----------->The state of the order is complete. The state is " + order.getState());
                this.renderState(0, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if(!"0".equals(result)){
                log.error("----------->the order returned is failed. " + this.result);
                this.renderState(3, "支付平台返回的是失败订单");
                return;
            }

            int realMoney = (int) (Float.valueOf(amount) * 100);

            if (realMoney != order.getMoney()) {
                log.error("----------->the order money error,money:" + order.getMoney()+",channelMoney:"+realMoney);
                this.renderState(3, "订单金额不对");
                return;
            }

            if(isValid(order.getChannel())){
                order.setChannelOrderID(orderId);
                order.setRealMoney(realMoney);
                order.setSdkOrderTime(orderTime);
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                log.info("------------>send to game,order:" + order.toJSON());
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(0, "");
            }else{
                log.error("------------>sign 错误,order:" + order.toJSON());
                order.setChannelOrderID(orderId);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(1, "sign 错误");
            }

        }catch (Exception e){
            log.error("------------>exception,msg:" + e.getMessage());
            e.printStackTrace();
            try {
                this.renderState(94, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();

        if(accessMode != null){
            sb.append("accessMode=").append(accessMode).append("&");
        }

        if(amount != null){
            sb.append("amount=").append(amount).append("&");
        }


        if(bankId != null){
            sb.append("bankId=").append(bankId).append("&");
        }

        if(extReserved != null){
            sb.append("extReserved=").append(URLDecoder.decode(extReserved, "utf-8")).append("&");
        }

        if(notifyTime != null){
            sb.append("notifyTime=").append(notifyTime).append("&");
        }

        if(orderId != null){
            sb.append("orderId=").append(orderId).append("&");
        }

        if(orderTime != null){
            sb.append("orderTime=").append(orderTime).append("&");
        }

        if(payType != null){
            sb.append("payType=").append(payType).append("&");
        }


        if(productName != null){
            sb.append("productName=").append(productName).append("&");
        }

        if(requestId != null){
            sb.append("requestId=").append(requestId).append("&");
        }

        if(result != null){
            sb.append("result=").append(result).append("&");
        }


        if(spending != null){
            sb.append("spending=").append(spending).append("&");
        }

        if(sysReserved != null){
            sb.append("sysReserved=").append(URLDecoder.decode(sysReserved, "utf-8")).append("&");
        }

        if(tradeTime != null){
            sb.append("tradeTime=").append(tradeTime).append("&");
        }

        if(userName != null){
            sb.append("userName=").append(userName);
        }

        log.debug("------------>the unSignStr:"+sb.toString()+", sign:"+sign+", pubKey:"+channel.getCpPayKey());
        //return RSAUtils.verify(sb.toString(), this.sign, channel.getCpPayKey(), "UTF-8", RSAUtils.SIGNATURE_ALGORITHM_SHA);
        return  RSA.doCheck(sb.toString(), this.sign, channel.getCpPayKey());

    }

    private void renderState(int code, String msg) throws IOException {

        JSONObject json = new JSONObject();
        json.put("result", code);
        this.renderJson(json.toString());

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getSpending() {
        return spending;
    }

    public void setSpending(String spending) {
        this.spending = spending;
    }

    public String getExtReserved() {
        return extReserved;
    }

    public void setExtReserved(String extReserved) {
        this.extReserved = extReserved;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSysReserved() {
        return sysReserved;
    }

    public void setSysReserved(String sysReserved) {
        this.sysReserved = sysReserved;
    }
}
