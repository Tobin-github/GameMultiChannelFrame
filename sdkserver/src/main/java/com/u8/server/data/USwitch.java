package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ant on 2015/8/29.
 */
@Entity
@Table(name = "uswitch")
public class USwitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int status;
    private int switchType;
    private int begDate;
    private int endDate;
    private int gameID;
    private int masterID;
    private int channelID;
    private String operater;
    private Date operaterDate;
    private int payNumChange;

    public String toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("status", status);
        json.put("switchType", switchType);
        json.put("begDate", begDate);
        json.put("endDate", endDate);
        json.put("gameID", gameID);
        json.put("masterID", masterID);
        json.put("channelID", channelID);
        json.put("operater", operater);
        json.put("operaterDate", operaterDate);
        json.put("payNumChange", payNumChange);

        return json.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSwitchType() {
        return switchType;
    }

    public void setSwitchType(int switchType) {
        this.switchType = switchType;
    }

    public int getBegDate() {
        return begDate;
    }

    public void setBegDate(int begDate) {
        this.begDate = begDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getMasterID() {
        return masterID;
    }

    public void setMasterID(int masterID) {
        this.masterID = masterID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    public Date getOperaterDate() {
        return operaterDate;
    }

    public void setOperaterDate(Date operaterDate) {
        this.operaterDate = operaterDate;
    }

    public int getPayNumChange() {
        return payNumChange;
    }

    public void setPayNumChange(int payNumChange) {
        this.payNumChange = payNumChange;
    }
}
