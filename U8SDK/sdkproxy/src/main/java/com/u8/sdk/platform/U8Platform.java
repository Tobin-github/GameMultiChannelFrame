package com.u8.sdk.platform;

import com.u8.sdk.PayParams;
import com.u8.sdk.U8Code;
import com.u8.sdk.U8SDK;
import com.u8.sdk.UserExtraData;
import com.u8.sdk.base.IU8SDKListener;
import com.u8.sdk.log.Log;
import com.u8.sdk.plugin.U8Pay;
import com.u8.sdk.plugin.U8User;
import com.u8.sdk.verify.UToken;

import android.app.Activity;

/**
 * 游戏层调用接口， 所有游戏层需要调用的接口，都从U8Platform中调用
 * 所有接口和回调都已经在主线程中调用了，调用者无需考虑线程问题
 * @author xiaohei
 *
 */
public class U8Platform {

	private static U8Platform instance;
	
	private boolean isSwitchAccount = false;
	
	private U8Platform(){
		
	}
	
	public static U8Platform getInstance(){
		if(instance == null){
			instance = new U8Platform();
		}
		return instance;
	}
	
	/**
	 * SDK初始化，需要在游戏启动Activity的onCreate中调用
	 * @param context
	 * @param callback
	 */
	public void init(Activity context, final U8InitListener callback){
		
		if(callback == null){
			Log.d("U8SDK", "U8InitListener must be not null.");
			return;
		}
		
		try{
			
			U8SDK.getInstance().setSDKListener(new IU8SDKListener() {
				
				@Override
				public void onResult(final int code, final String msg) {
					Log.d("U8SDK", "onResult.code:"+code+";msg:"+msg);
					
					U8SDK.getInstance().runOnMainThread(new Runnable() {
						
						@Override
						public void run() {
							switch(code){
							case U8Code.CODE_INIT_SUCCESS:
								callback.onInitResult(U8Code.CODE_INIT_SUCCESS, msg);
								break;
							case U8Code.CODE_INIT_FAIL:
								callback.onInitResult(U8Code.CODE_INIT_FAIL, msg);
								break;
							case U8Code.CODE_LOGIN_FAIL:
								callback.onLoginResult(U8Code.CODE_LOGIN_FAIL, null);
								break;
							case U8Code.CODE_PAY_SUCCESS:
								callback.onPayResult(U8Code.CODE_PAY_SUCCESS, msg);
								break;
							case U8Code.CODE_PAY_FAIL:
								callback.onPayResult(U8Code.CODE_PAY_FAIL, msg);
								break;
							case U8Code.CODE_PAY_CANCEL:
								callback.onPayResult(U8Code.CODE_PAY_CANCEL, msg);
								break;
							case U8Code.CODE_PAY_UNKNOWN:
								callback.onPayResult(U8Code.CODE_PAY_UNKNOWN, msg);
								break;
							case U8Code.CODE_PAYING:
								callback.onPayResult(U8Code.CODE_PAYING, msg);
								break;
							}
						}
					});

				}
				
				@Override
				public void onLogout() {
					U8SDK.getInstance().runOnMainThread(new Runnable() {
						
						@Override
						public void run() {
							callback.onLogout();
						}
					});
				}
				
				@Override
				public void onSwitchAccount() {
					U8SDK.getInstance().runOnMainThread(new Runnable() {
						
						@Override
						public void run() {
							callback.onLogout();
						}
					});
				}
				
				@Override
				public void onLoginResult(String data) {
					Log.d("U8SDK", "SDK 登录成功,不用做处理，在onAuthResult中处理登录成功, 参数如下:");
					Log.d("U8SDK", data);
					isSwitchAccount = false;
				}
				
				@Override
				public void onSwitchAccount(String data) {
					Log.d("U8SDK", "SDK 切换帐号并登录成功,不用做处理，在onAuthResult中处理登录成功, 参数如下:");
					Log.d("U8SDK", data);
					isSwitchAccount = true;
				}
				

				
				@Override
				public void onAuthResult(final UToken authResult) {
					U8SDK.getInstance().runOnMainThread(new Runnable() {
						
						@Override
						public void run() {
							
							if(isSwitchAccount){
								if(authResult.isSuc()){
									callback.onSwitchAccount(authResult);
								}else{
									Log.e("U8SDK", "switch account auth failed.");
								}
							}else{
								
								if(!authResult.isSuc()){
									callback.onLoginResult(U8Code.CODE_LOGIN_FAIL, null);
									return;
								}
								callback.onLoginResult(U8Code.CODE_LOGIN_SUCCESS, authResult);
							}
						}
					});
				}
			});
			
			U8SDK.getInstance().init(context);
			U8SDK.getInstance().onCreate();
			
		}catch(Exception e){
			callback.onInitResult(U8Code.CODE_INIT_FAIL, e.getMessage());
			Log.e("U8SDK", "init failed.", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 登录，登录成功或者失败，会触发初始化回调中的onLoginResult
	 * @param context
	 */
	public void login(Activity context){
		U8SDK.getInstance().setContext(context);
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				U8User.getInstance().login();
			}
		});
	}
	
	/**
	 * 登出(选接)，登出没有回调
	 */
	public void logout(){
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				if(U8User.getInstance().isSupport("logout"))
					U8User.getInstance().logout();
			}
		});
	}
	
	/**
	 * 显示个人用户中心（选接）
	 */
	public void showAccountCenter(){
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				if(U8User.getInstance().isSupport("showAccountCenter")){
					U8User.getInstance().showAccountCenter();
				}
			}
		});
	}
	
	/**
	 * 提交游戏中角色数据（必接）
	 * @param data
	 */
	public void submitExtraData(final UserExtraData data){
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				U8User.getInstance().submitExtraData(data);
			}
		});
	}
	
	/**
	 * 退出游戏，弹出确认框（必接）
	 * @param callback
	 */
	public void exitSDK(final U8ExitListener callback){
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				if(U8User.getInstance().isSupport("exit")){
					U8User.getInstance().exit();
				}else{
					if(callback != null){
						callback.onGameExit();
					}
				}
			}
		});
	}
	
	/**
	 * 支付，支付成功或者失败，会触发初始化回调中onPayResult
	 * @param context
	 */
	public void pay(Activity context, final PayParams data){
		U8SDK.getInstance().setContext(context);
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			
			@Override
			public void run() {
				U8Pay.getInstance().pay(data);
			}
		});
	}
	
}
