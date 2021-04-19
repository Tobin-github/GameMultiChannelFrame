package com.u8.sdk;

import com.u8.sdk.base.Constants;


/***
 * 分享插件接口
 * @author xiaohei
 *
 */
public interface IShare extends IPlugin{

	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_SHARE;
	
	/***
	 * 分享
	 * @param params
	 */
	public void share(ShareParams params);
	
}
