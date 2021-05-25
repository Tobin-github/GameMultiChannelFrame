package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * 与cp的渠道映射表
 */

@Entity
@Table(name = "uChanelExChange")
public class UChannelExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cpChannelID;
    private int gameID;
    private int UChannelID;

    @Override
    public String toString() {
        return "UChannelExchange{" +
                "id=" + id +
                ", cpChannelID=" + cpChannelID +
                ", gameID=" + gameID +
                ", UChannelID=" + UChannelID +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("cpChannelID", cpChannelID);
        json.put("gameID", gameID);
        UGame game = getGame();
        json.put("appName", game == null? "": game.getName());
        json.put("UChannelID", UChannelID);
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

    public UGame getGame(){
        return CacheManager.getInstance().getGame(this.gameID);
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



    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getUChannelID() {
        return UChannelID;
    }

    public void setUChannelID(int UChannelID) {
        this.UChannelID = UChannelID;
    }
}
