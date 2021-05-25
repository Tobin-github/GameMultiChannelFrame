package com.u8.server.sdk.lehaihai;

/**
 * Created by ant on 2016/1/19.
 */
public class LehaihaiPayResult {

    private String orderid;
    private String gameid;
    private String serverid;
    private String uid;
    private String amount;
    private String time;
    private String sign;
    private String extendsInfo;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getExtendsInfo() {
        return extendsInfo;
    }

    public void setExtendsInfo(String extendsInfo) {
        this.extendsInfo = extendsInfo;
    }
}
