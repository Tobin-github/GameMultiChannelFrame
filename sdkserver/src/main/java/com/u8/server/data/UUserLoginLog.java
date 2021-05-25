package com.u8.server.data;

import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户流水表
 */

@Entity
@Table(name = "uuser_login_log")
public class UUserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int userID;
    private String userName;
    private String nickName;
    private String extension;
    private Date createDate;

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("userID", userID);
        json.put("userName", userName);
        json.put("nickName", nickName);
        json.put("extension", extension);
        json.put("createDate", TimeFormater.format_default(createDate));
        return json;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
