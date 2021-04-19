package com.u8.sdk;

import com.u8.sdk.base.Constants;


/***
 * 支付接口
 * @author xiaohei
 *
 */
public interface IPay extends IPlugin{
	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_PAY;
	
	/***
	 * 调用支付界面
	 * @param data
	 */
	public void pay(PayParams data);
}
