package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.*;

/**
 * Created by ant on 2015/8/29.
 */
@Entity
@Table(name = "uadmin")
public class UAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private int permission;

    public JSONObject toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("username", username);
        json.put("password", password);
        json.put("permission", permission);

        return json;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
