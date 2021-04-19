package com.u8.sdk.plugin;

import android.util.Log;

import com.u8.sdk.IDownload;
import com.u8.sdk.base.PluginFactory;

public class U8Download {

	private static U8Download instance;
	
	private IDownload downloadPlugin;
	
	private U8Download(){
		
	}
	
	public static U8Download getInstance(){
		if(instance == null){
			instance = new U8Download();
		}
		return instance;
	}
	
	public void init(){
		this.downloadPlugin = (IDownload)PluginFactory.getInstance().initPlugin(IDownload.PLUGIN_TYPE);
		
	}
	
	public boolean isSupport(String method){
		if(isPluginInited()){
			return this.downloadPlugin.isSupportMethod(method);
		}
		return false;
	}
	
	/**
	 * 下载
	 * 
	 * @param url apk下载文件地址
	 * @param showConfirm 下载之前是否显示下载确认框
	 * @param force 是否强制下载
	 */
	public void download(String url, boolean showConfirm, boolean force){
		if(isPluginInited()){
			this.downloadPlugin.download(url, showConfirm, force);
		}
	}
	
	private boolean isPluginInited(){
		if(this.downloadPlugin == null){
			Log.e("U8SDK", "The download plugin is not inited or inited failed.");
			return false;
		}
		return true;
	}
	

	
}
