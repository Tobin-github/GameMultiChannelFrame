package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据立方参数配置对象
 */
@Entity
@Table(name = "uadmin_statistics")
public class UStatistics implements Serializable{

    @Id
    private Integer Id;         //主键ID，自增
    private String Name;        //分析后台名称
    private String UserName;    //后台用户名
    private String Password;    //后台用户密码
    private String Cid;         //游戏编号
    private Integer GameId;     //游戏appId
    private Date CreateDate;    //创建时间
    private Date UpdateDate;    //更新时间

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("Name", Name);
        json.put("UserName", UserName);
        json.put("Password", Password);
        json.put("Cid", Cid);
        json.put("GameId", GameId);
        json.put("CreateDate", CreateDate);
        json.put("UpdateDate", UpdateDate);

        return json;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public Integer getGameId() {
        return GameId;
    }

    public void setGameId(Integer gameId) {
        GameId = gameId;
    }

    public Date getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public Date getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(Date updateDate) {
        UpdateDate = updateDate;
    }
}
