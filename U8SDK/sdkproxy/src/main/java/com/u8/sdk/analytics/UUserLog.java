package com.u8.sdk.analytics;

import java.util.Date;

/**
 * 用户日志
 */
public class UUserLog {

	public static final int OP_CREATE_ROLE = 1;
	public static final int OP_ENTER_GAME = 2;
	public static final int OP_LEVEL_UP = 3;
	public static final int OP_EXIT = 4;
	
    private Integer userID; 	//用户ID
    private Integer appID;  	//游戏ID
    private Integer channelID;  //渠道ID
    private String serverID; 	//服务器ID
    private String serverName;  //服务器名称
    private String roleID;      //角色ID
    private String roleName;    //角色名称
    private String roleLevel;   //角色等级
    private String deviceID;    //设备号
    private Integer opType;     //操作类型（1：创建角色;2:进入游戏;3:等级提升;4:登出游戏）


    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }



	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }


    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }
}
