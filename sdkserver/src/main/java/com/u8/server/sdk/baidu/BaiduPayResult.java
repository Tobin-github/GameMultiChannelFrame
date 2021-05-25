package com.u8.server.sdk.baidu;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * Created by ant on 2015/2/28.
 */
public class BaiduPayResult {

    @JsonProperty("UID")
    private long UID;

    @JsonProperty("MerchandiseName")
    private String MerchandiseName;

    @JsonProperty("OrderMoney")
    private double OrderMoney;

    @JsonProperty("StartDateTime")
    private Date StartDateTime;

    @JsonProperty("BankDateTime")
    private Date BankDateTime;

    @JsonProperty("OrderStatus")
    private int OrderStatus;

    @JsonProperty("StatusMsg")
    private String StatusMsg;

    @JsonProperty("ExtInfo")
    private String ExtInfo;

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public String getMerchandiseName() {
        return MerchandiseName;
    }

    public void setMerchandiseName(String merchandiseName) {
        MerchandiseName = merchandiseName;
    }

    public double getOrderMoney() {
        return OrderMoney;
    }

    public void setOrderMoney(double orderMoney) {
        OrderMoney = orderMoney;
    }

    public Date getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        StartDateTime = startDateTime;
    }

    public Date getBankDateTime() {
        return BankDateTime;
    }

    public void setBankDateTime(Date bankDateTime) {
        BankDateTime = bankDateTime;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getStatusMsg() {
        return StatusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        StatusMsg = statusMsg;
    }

    public String getExtInfo() {
        return ExtInfo;
    }

    public void setExtInfo(String extInfo) {
        ExtInfo = extInfo;
    }
}
