package com.u8.sdk.analytics;

/**
 * 设备信息
 * 应用首次启动记录玩家设备信息
 * Created by ant on 2016/8/12.
 */
public class UDevice {

    private String deviceID;        //唯一设备号
    private Integer appID;          //对应游戏ID
    private Integer channelID;		//渠道ID
    private String mac;             //mac地址
    private String deviceType;      //机型
    private Integer deviceOS;       //系统类型， 1:Android;2:iOS
    private String deviceDpi;       //分辨率

    

    public Integer getChannelID() {
		return channelID;
	}

	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}

	public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(Integer deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceDpi() {
        return deviceDpi;
    }

    public void setDeviceDpi(String deviceDpi) {
        this.deviceDpi = deviceDpi;
    }

	public Integer getAppID() {
		return appID;
	}

	public void setAppID(Integer appID) {
		this.appID = appID;
	}
    
    
    
}
