package com.u8.server.sdk.vivo;

/**
 * Created by ant on 2015/4/24.
 */
public class SingleVivoOrderResult {

    private int respCode;
    private String respMsg;

    private String signMethod;
    private String signature;
    private String vivoSignature;
    private String vivoOrder;
    private int orderAmount;

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getVivoSignature() {
        return vivoSignature;
    }

    public void setVivoSignature(String vivoSignature) {
        this.vivoSignature = vivoSignature;
    }

    public String getVivoOrder() {
        return vivoOrder;
    }

    public void setVivoOrder(String vivoOrder) {
        this.vivoOrder = vivoOrder;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }
}
