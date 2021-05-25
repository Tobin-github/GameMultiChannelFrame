package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import net.sf.json.JSONObject;

import javax.persistence.*;

/**
 * 与cp的IAP商品映射表，cp提供商品ID，查询出对应的苹果IAP商品信息
 */

@Entity
@Table(name = "iAPGoodsInfos")
public class IAPGoodsInfos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String proName;//商品名称
    private int proID;//商品ID
    private int UChannelID;//属于哪个渠道
    private String IAPProductID;//苹果的商品ID
    private String IAPProductKEy;//苹果的商品Key,支付回调时验证用



    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("proName", proName);
        json.put("proID", proID);
        json.put("IAPProductID", IAPProductID);
        json.put("IAPProductKEy", IAPProductKEy);
        json.put("UChannelID", UChannelID);
        UChannel channel = getChannel();
        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());
        json.put("platID", (channel == null || channel.getPlatID() == null) ? 0 : channel.getPlatID());
        UGame game = channel.getGame();
        json.put("appName", game == null? "": game.getName());

        return json;
    }

    @Override
    public String toString() {
        return "IAPGoodsInfos{" +
                "id=" + id +
                ", proName='" + proName + '\'' +
                ", proID=" + proID +
                ", UChannelID=" + UChannelID +
                ", IAPProductID='" + IAPProductID + '\'' +
                ", IAPProductKEy='" + IAPProductKEy + '\'' +
                '}';
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

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getProID() {
        return proID;
    }

    public void setProID(int proID) {
        this.proID = proID;
    }

    public int getUChannelID() {
        return UChannelID;
    }

    public void setUChannelID(int UChannelID) {
        this.UChannelID = UChannelID;
    }

    public String getIAPProductID() {
        return IAPProductID;
    }

    public void setIAPProductID(String IAPProductID) {
        this.IAPProductID = IAPProductID;
    }

    public String getIAPProductKEy() {
        return IAPProductKEy;
    }

    public void setIAPProductKEy(String IAPProductKEy) {
        this.IAPProductKEy = IAPProductKEy;
    }
}
