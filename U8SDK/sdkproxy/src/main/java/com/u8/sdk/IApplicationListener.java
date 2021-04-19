package com.u8.sdk;

import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationListener {

	public void onProxyCreate();
	
	public void onProxyAttachBaseContext(Context base);
	
	public void onProxyConfigurationChanged(Configuration config);
	
	public void onProxyTerminate();
	
}
