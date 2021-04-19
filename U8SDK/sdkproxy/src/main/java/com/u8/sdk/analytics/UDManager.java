package com.u8.sdk.analytics;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.log.Log;
import com.u8.sdk.utils.EncryptUtils;
import com.u8.sdk.utils.GUtils;
import com.u8.sdk.utils.U8HttpUtils;

public class UDManager {

	private static UDManager instance;
	
	private UDManager(){
		
	}
	
	public static UDManager getInstance(){
		if(instance == null){
			instance = new UDManager();
		}
		return instance;
	}
	
	public void submitUserInfo(Activity context, String url, String appKey, UUserLog log){
		
		try{
			
			Log.d("U8SDK", "begin submit user info to u8server:"+log.getOpType());
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("userID", log.getUserID()+"");
			params.put("appID", log.getAppID()+"");
			params.put("channelID", log.getChannelID()+"");
			params.put("serverID", log.getServerID());
			params.put("serverName", log.getServerName());
			params.put("roleID", log.getRoleID());
			params.put("roleName", log.getRoleName());
			params.put("roleLevel", log.getRoleLevel());
			params.put("deviceID", log.getDeviceID());
			params.put("opType", log.getOpType()+"");
			
            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(log.getAppID())
              .append("channelID=").append(log.getChannelID())
              .append("deviceID=").append(log.getDeviceID())
              .append("opType=").append(log.getOpType())
              .append("roleID=").append(log.getRoleID())
              .append("roleLevel=").append(log.getRoleLevel())
              .append("roleName=").append(log.getRoleName())
              .append("serverID=").append(log.getServerID())
              .append("serverName=").append(log.getServerName())
              .append("userID=").append(log.getUserID())
              .append(appKey);			
            
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();
            
            params.put("sign", sign);
            
            url = url + "/addUserLog";
            
			String result = U8HttpUtils.httpGet(url, params);
			
			Log.d("U8SDK", "The sign is " + sign + " The result is "+result);
			
			if(result != null && result.trim().length() > 0){
				
				JSONObject jsonObj = new JSONObject(result);
				int state = jsonObj.getInt("state");
				
				if(state != 1){
					Log.d("U8SDK", "submit user info failed. the state is "+ state);
				}else{
					Log.d("U8SDK", "submit user info success");
				}
			}			
			
		}catch(Exception e){
			Log.e("U8SDK", "submit user info failed.\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void submitDeviceInfo(Activity context, String url, String appKey, UDevice device){
		
		try{
			
			Log.d("U8SDK", "begin submit device info to u8server");
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("appID", device.getAppID()+"");
			params.put("deviceID", device.getDeviceID());
			params.put("mac", device.getMac());
			params.put("deviceType", device.getDeviceType());
			params.put("deviceOS", device.getDeviceOS()+"");
			params.put("deviceDpi", device.getDeviceDpi());
			params.put("channelID", device.getChannelID()+"");
			
            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(device.getAppID()+"")
              .append("channelID=").append(device.getChannelID())
              .append("deviceDpi=").append(device.getDeviceDpi())
              .append("deviceID=").append(device.getDeviceID())
              .append("deviceOS=").append(device.getDeviceOS())
              .append("deviceType=").append(device.getDeviceType())
              .append("mac=").append(device.getMac())
              .append(appKey);			
			
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();
            
            params.put("sign", sign);
            
            url = url + "/addDevice";
            
			String result = U8HttpUtils.httpGet(url, params);
			
			Log.d("U8SDK", "The sign is " + sign + " The result is "+result);
			
			if(result != null && result.trim().length() > 0){
				
				JSONObject jsonObj = new JSONObject(result);
				int state = jsonObj.getInt("state");
				
				if(state != 1){
					Log.d("U8SDK", "submit device info failed. the state is "+ state);
				}else{
					Log.d("U8SDK", "submit device info success");
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			Log.e("U8SDK", "submit device info failed.\n"+e.getMessage());
		}
	}
	
	/**
	 * 手机用户设备信息
	 * @param context
	 * @param appID
	 * @return
	 */
	public UDevice collectDeviceInfo(Activity context, Integer appID, Integer channelID){
		try{
			
			UDevice device = new UDevice();
			device.setAppID(appID);
			device.setChannelID(channelID);
			device.setDeviceID(GUtils.getDeviceID(context));
			device.setMac(GUtils.getMacAddress(context));
			device.setDeviceType(android.os.Build.MODEL);
			device.setDeviceOS(1);
			device.setDeviceDpi(GUtils.getScreenDpi(context));
			
			return device;
			
		}catch(Exception e){
			e.printStackTrace();
			Log.e("U8SDK", e.getMessage());
		}
		
		return null;
	}
}
