package com.u8.server.data;

import javax.persistence.*;

/**
 * 支付类型
 */

@Entity
@Table(name = "upaytype")
public class UPayType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int masterID;
    private String payTypeName;
    private int payTypeId;

    @Override
    public String toString() {
        return "UPayType{" +
                "id=" + id +
                ", masterID=" + masterID +
                ", payTypeName='" + payTypeName + '\'' +
                ", payTypeId=" + payTypeId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMasterID() {
        return masterID;
    }

    public void setMasterID(int masterID) {
        this.masterID = masterID;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public int getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(int payTypeId) {
        this.payTypeId = payTypeId;
    }
}
