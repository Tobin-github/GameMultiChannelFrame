package com.u8.server.data;

import com.u8.server.cache.CacheManager;

import javax.persistence.*;
import java.util.Date;

/**
 * 腾讯应用宝 单独订单表
 * 应用宝没有订单的概念，这张表，仅仅记录支付成功的数据，用作数据统计
 * Created by ant on 2015/10/15.
 */

@Entity
@Table(name = "umsdkorder")
public class UMsdkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int appID;
    private int channelID;
    private int userID;
    private String username;
    private int coinNum;            //游戏币数量
    private int firstPay;           //是否为首次充值
    private int allMoney;           //累计充值金额
    private String channelOrderID;  //渠道订单号
    private int state;

    private Date createdTime;

    public UGame getGame(){

        return CacheManager.getInstance().getGame(this.appID);
    }

    public UMsdkOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoinNum() {
        return coinNum;
    }

    public void setCoinNum(int coinNum) {
        this.coinNum = coinNum;
    }

    public int getFirstPay() {
        return firstPay;
    }

    public void setFirstPay(int firstPay) {
        this.firstPay = firstPay;
    }

    public int getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(int allMoney) {
        this.allMoney = allMoney;
    }

    public String getChannelOrderID() {
        return channelOrderID;
    }

    public void setChannelOrderID(String channelOrderID) {
        this.channelOrderID = channelOrderID;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
