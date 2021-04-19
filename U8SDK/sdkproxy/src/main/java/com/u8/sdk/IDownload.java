package com.u8.sdk;

import com.u8.sdk.base.Constants;


public interface IDownload extends IPlugin{

	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_DOWNLOAD;
	
	/**
	 * 下载
	 * 
	 * 
	 * @param url apk下载文件地址
	 * @param showConfirm 下载之前是否显示下载确认框
	 */
	public void download(String url, boolean showConfirm, boolean force);
	
}
