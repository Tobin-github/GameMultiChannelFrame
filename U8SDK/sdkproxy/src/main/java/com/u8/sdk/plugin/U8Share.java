package com.u8.sdk.plugin;

import android.util.Log;

import com.u8.sdk.IShare;
import com.u8.sdk.ShareParams;
import com.u8.sdk.base.PluginFactory;

/**
 * 用户分享
 * @author xiaohei
 *
 */
public class U8Share {

	private static U8Share instance;
	
	private IShare sharePlugin;
	
	private U8Share(){
		
	}
	
	public static U8Share getInstance(){
		if(instance == null){
			instance = new U8Share();
			
		}
		return instance;
	}
	
	public void init(){
		this.sharePlugin = (IShare)PluginFactory.getInstance().initPlugin(IShare.PLUGIN_TYPE);
	}
	
	public boolean isSupport(String method){
		if(isPluginInited()){
			return this.sharePlugin.isSupportMethod(method);
		}
		return false;
	}
	
	/**
	 * 分享接口
	 * @param params
	 */
	public void share(ShareParams params){
		if(isPluginInited()){
			this.sharePlugin.share(params);
		}
	}
	
	private boolean isPluginInited(){
		if(this.sharePlugin == null){
			Log.e("U8SDK", "The share plugin is not inited or inited failed.");
			return false;
		}
		return true;
	}
	
}
