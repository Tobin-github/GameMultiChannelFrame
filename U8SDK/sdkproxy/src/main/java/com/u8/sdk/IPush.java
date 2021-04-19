package com.u8.sdk;

import com.u8.sdk.base.Constants;


/**
 * 推送接口
 * @author xiaohei
 *
 */
public interface IPush extends IPlugin{

	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_PUSH;

	/**
	 * 执行通知
	 * @param msgs
	 */
	public void scheduleNotification(String msgs);
	
	/**
	 * 开始推送
	 */
	public void startPush();
	
	/**
	 * 停止推送
	 */
	public void stopPush();
	
	/**
	 * 添加tag
	 * @param tags
	 */
	public void addTags(String... tags);
	
	/**
	 * 删除tag
	 * @param tag
	 */
	public void removeTags(String... tag);
	
	/**
	 * 添加别名
	 * @param alias
	 */
	public void addAlias(String alias);
	
	/**
	 * 删除别名
	 * @param alias
	 */
	public void removeAlias(String alias);
}
