package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import net.sf.json.JSONObject;

import javax.persistence.*;

/**
 * 与cp的渠道映射表
 */

@Entity
@Table(name = "UChannelpayType")
public class UChannelpayType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cpChannelID;
    private int UChannelID;
    private int payType;
    private String PayScriptClass;

    @Override
    public String toString() {
        return "UChannelExchange{" +
                "id=" + id +
                ", cpChannelID=" + cpChannelID +
                ", loginType=" + payType +
                ", UChannelID=" + UChannelID +
                ", PayScriptClass=" + PayScriptClass +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("cpChannelID", cpChannelID);
        json.put("PayScriptClass", PayScriptClass);
        UGame game = getChannel().getGame();
        json.put("appName", game == null? "": game.getName());
        json.put("payType", payType);
        UChannel channel = getChannel();
        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());
        json.put("platID", (channel == null || channel.getPlatID()==null) ? 0 : channel.getPlatID());
        return json;
    }

    public String getCpChannelID() {
        return cpChannelID;
    }

    public void setCpChannelID(String cpChannelID) {
        this.cpChannelID = cpChannelID;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public UChannel getChannel(){
        return CacheManager.getInstance().getChannel(this.UChannelID);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayScriptClass() {
        return PayScriptClass;
    }

    public void setLoginScriptClass(String loginScriptClass) {
        PayScriptClass = loginScriptClass;
    }

    public int getUChannelID() {
        return UChannelID;
    }

    public void setUChannelID(int UChannelID) {
        this.UChannelID = UChannelID;
    }
}
