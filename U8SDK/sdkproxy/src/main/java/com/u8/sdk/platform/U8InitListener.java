package com.u8.sdk.platform;

import com.u8.sdk.verify.UToken;


/**
 * U8SDK初始化回调
 * @author xiaohei
 *
 */
public interface U8InitListener {

	/**
	 * 初始化结果
	 * @param code
	 * @param msg
	 */
	void onInitResult(int code, String msg);
	
	
	/**
	 * U8平台登录回调
	 * @param data
	 */
	void onLoginResult(int code, UToken data);
	
	/**
	 * 游戏中通过SDK切换到新账号的回调，游戏收到该回调，需要引导用户重新登录，重新加载该新用户对应的角色数据
	 * @param data
	 */
	void onSwitchAccount(UToken data);
	
	/**
	 * 用户登出回调（需要收到该回调需要返回游戏登录界面，并调用login接口，打开SDK登录界面）
	 */
	void onLogout();
	
	/**
	 * 支付结果回调
	 * @param code
	 * @param msg
	 */
	void onPayResult(int code, String msg);
	
}
