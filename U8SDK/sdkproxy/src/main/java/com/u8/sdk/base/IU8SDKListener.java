package com.u8.sdk.base;

import com.u8.sdk.verify.UToken;


public interface IU8SDKListener {
	
	void onResult(int code, String msg);
	
	void onLoginResult(String data);
	
	void onSwitchAccount();
	
	void onSwitchAccount(String data);
	
	void onLogout();
	
	void onAuthResult(UToken authResult);
}
