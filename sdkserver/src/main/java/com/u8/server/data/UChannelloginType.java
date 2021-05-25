package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import net.sf.json.JSONObject;

import javax.persistence.*;

/**
 * 与cp的渠道映射表
 */

@Entity
@Table(name = "uChannelloginType")
public class UChannelloginType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cpChannelID;
    private int UChannelID;
    private int loginType;
    private String LoginScriptClass;

    @Override
    public String toString() {
        return "UChannelExchange{" +
                "id=" + id +
                ", cpChannelID=" + cpChannelID +
                ", loginType=" + loginType +
                ", UChannelID=" + UChannelID +
                ", LoginScriptClass=" + LoginScriptClass +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("cpChannelID", cpChannelID);
        json.put("LoginScriptClass", LoginScriptClass);
        UGame game = getChannel().getGame();
        json.put("appName", game == null? "": game.getName());
        json.put("loginType", loginType);
        UChannel channel = getChannel();
        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());
        json.put("platID", (channel == null || channel.getPlatID() == null) ? 0 : channel.getPlatID());
        return json;
    }

    public String getCpChannelID() {
        return cpChannelID;
    }

    public void setCpChannelID(String cpChannelID) {
        this.cpChannelID = cpChannelID;
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

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getLoginScriptClass() {
        return LoginScriptClass;
    }

    public void setLoginScriptClass(String loginScriptClass) {
        LoginScriptClass = loginScriptClass;
    }

    public int getUChannelID() {
        return UChannelID;
    }

    public void setUChannelID(int UChannelID) {
        this.UChannelID = UChannelID;
    }
}
