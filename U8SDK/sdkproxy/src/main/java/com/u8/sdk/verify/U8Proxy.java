package com.u8.sdk.verify;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.u8.sdk.PayParams;
import com.u8.sdk.U8SDK;
import com.u8.sdk.log.Log;
import com.u8.sdk.utils.EncryptUtils;
import com.u8.sdk.utils.GUtils;
import com.u8.sdk.utils.U8HttpUtils;

/**
 * U8Server 登录认证和下单相关逻辑
 * @author xiaohei
 *
 */
public class U8Proxy{

	/***
	 * 去U8Server进行SDK的登录认证，同时获取U8Server返回的token，userID,sdkUserID等信息
	 * @param result
	 * @return
	 */
	@SuppressLint("DefaultLocale") 
	public static UToken auth(String result){
		
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("appID", U8SDK.getInstance().getAppID()+"");
			params.put("channelID", "" + U8SDK.getInstance().getCurrChannel());
			params.put("extension", result);
			params.put("sdkVersionCode", U8SDK.getInstance().getSDKVersionCode());
			params.put("deviceID", GUtils.getDeviceID(U8SDK.getInstance().getContext()));
			
            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(U8SDK.getInstance().getAppID()+"")
                    .append("channelID=").append(U8SDK.getInstance().getCurrChannel())
                    .append("extension=").append(result).append(U8SDK.getInstance().getAppKey());			
			
            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();
            
            params.put("sign", sign);
            
			String authResult = U8HttpUtils.httpGet(U8SDK.getInstance().getAuthURL(), params);
			
			Log.d("U8SDK", "The sign is " + sign + " The auth result is "+authResult);
			
			return parseAuthResult(authResult);
			
		}catch(Exception e){
			Log.e("U8SDK", "u8server auth exception.", e);
			e.printStackTrace();
		}
		
		return new UToken();
		
	}
	
	/***
	 * 访问U8Server验证sid的合法性，同时获取U8Server返回的token，userID,sdkUserID信息
	 * 这里仅仅是测试，正式环境下，请通过游戏服务器来获取订单号，不要放在客户端操作
	 * @param result
	 * @return
	 */
	public static UOrder getOrder(PayParams data){
		
		try{
			
			UToken tokenInfo = U8SDK.getInstance().getUToken();
			if(tokenInfo == null){
				Log.e("U8SDK", "The user not logined. the token is null");
				return null;
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("userID", ""+tokenInfo.getUserID());
			params.put("productID", data.getProductId());
			params.put("productName", data.getProductName());
			params.put("productDesc", data.getProductDesc());
			params.put("money", ""+data.getPrice() * 100);
			params.put("roleID", ""+data.getRoleId());
			params.put("roleName", data.getRoleName());
			params.put("serverID", data.getServerId());
			params.put("serverName", data.getServerName());
			params.put("extension", data.getExtension());
			params.put("notifyUrl", data.getPayNotifyUrl());
			
			params.put("signType", "md5");
			String sign = generateSign(tokenInfo, data);
			params.put("sign", sign);
			
			String orderResult = U8HttpUtils.httpPost(U8SDK.getInstance().getOrderURL(), params);
			
			Log.d("U8SDK", "The order result is "+orderResult);
			
			return parseOrderResult(orderResult);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
	}
	
    private static String generateSign(UToken token, PayParams data) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("userID=").append(token.getUserID()).append("&")
        		.append("productID=").append(data.getProductId()).append("&")
                .append("productName=").append(data.getProductName()).append("&")
                .append("productDesc=").append(data.getProductDesc()).append("&")
                .append("money=").append(data.getPrice() * 100).append("&")
                .append("roleID=").append(data.getRoleId()).append("&")
                .append("roleName=").append(data.getRoleName()).append("&")
                .append("serverID=").append(data.getServerId()).append("&")
                .append("serverName=").append(data.getServerName()).append("&")
                .append("extension=").append(data.getExtension());
        
		//这里是游戏服务器自己的支付回调地址，可以在下单的时候， 传给u8server。
		//u8server 支付成功之后， 会优先回调这个地址。 如果不传， 则需要在u8server后台游戏管理中配置游戏服务器的支付回调地址
        //如果传notifyUrl，则notifyUrl参与签名
		if(data.getPayNotifyUrl() != null){
			sb.append("&notifyUrl=").append(data.getPayNotifyUrl());
		}
        
		sb.append(U8SDK.getInstance().getAppKey());
		
        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");	//url encode

        Log.d("U8SDK", "The encoded getOrderID sign is "+encoded);

        //这里用md5方式生成sign
        String sign = EncryptUtils.md5(encoded).toLowerCase();
        
        //如果签名方式是RSA，走下面方式
		//String privateKey = U8SDK.getInstance().getPayPrivateKey();
        //String sign = RSAUtils.sign(encoded, privateKey, "UTF-8", "SHA1withRSA");
        
        Log.d("U8SDK", "The getOrderID sign is "+sign);
        
        return sign;

    }
	
	
	private static UToken parseAuthResult(String authResult){
		
		if(authResult == null || TextUtils.isEmpty(authResult)){
			
			return new UToken();
		}
		
		try {
			JSONObject jsonObj = new JSONObject(authResult);
			int state = jsonObj.getInt("state");
			
			if(state != 1){
				Log.d("U8SDK", "auth failed. the state is "+ state);
				return new UToken();
			}
			
			JSONObject jsonData = jsonObj.getJSONObject("data");
			
			return new UToken(jsonData.getInt("userID")
					, jsonData.getString("sdkUserID")
					, jsonData.getString("username")
					, jsonData.getString("sdkUserName")
					, jsonData.getString("token")
					, jsonData.getString("extension"));
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		return new UToken();
	}
	
	private static UOrder parseOrderResult(String orderResult){
		
		try {
			JSONObject jsonObj = new JSONObject(orderResult);
			int state = jsonObj.getInt("state");
			
			if(state != 1){
				Log.d("U8SDK", "get order failed. the state is "+ state);
				return null;
			}
			
			JSONObject jsonData = jsonObj.getJSONObject("data");
			
			return new UOrder(jsonData.getString("orderID"), jsonData.getString("extension"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
