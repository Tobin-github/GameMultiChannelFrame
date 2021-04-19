package com.u8.sdk.plugin;

import android.util.Log;

import com.u8.sdk.IPush;
import com.u8.sdk.base.PluginFactory;

/**
 * 推送插件
 * @author xiaohei
 *
 */
public class U8Push {

	private static U8Push instance;
	
	private IPush pushPlugin;
	
	private U8Push(){
		
	}
	
	public void init(){
		this.pushPlugin = (IPush)PluginFactory.getInstance().initPlugin(IPush.PLUGIN_TYPE);
	}
	
	public static U8Push getInstance(){
		
		if(instance == null){
			instance = new U8Push();
		}
		
		return instance;
	}
	
	public boolean isSupport(String method){
		if(isPluginInited()){
			return this.pushPlugin.isSupportMethod(method);
		}
		
		return false;
	}
	
	public void scheduleNotification(String msgs)
	{
		if(isPluginInited()){
			this.pushPlugin.scheduleNotification(msgs);
		}
	}
	
	/**
	 * 开始推送功能
	 */
	public void startPush(){
		if(isPluginInited()){
			this.pushPlugin.startPush();
		}
		
	}
	
	/**
	 * 停止推送功能
	 */
	public void stopPush(){
		if(isPluginInited()){
			this.pushPlugin.stopPush();
		}
	}
	
	/**
	 * 增加标签
	 * 您可以为用户加上标签，方便推送时按照标签来筛选。
	 * 目前每个用户tag限制在64个， 每个tag 最大256个字符。
	 * 
	 * 1、可为每个用户打多个标签。 
     * 2、不同应用程序、不同的用户，可以打同样的标签。 
	 * @param tags
	 */
	public void addTags(String...tags){
		if(isPluginInited()){
			this.pushPlugin.addTags(tags);
		}
	}
	
	/**
	 * 删除标签
	 * @param tags
	 */
	public void removeTags(String...tags){
		if(isPluginInited()){
			this.pushPlugin.removeTags(tags);
		}
	}
	
	/**
	 * 增加别名
	 * 为安装了应用程序的用户，取个别名来标识。以后给该用户 Push 消息时，就可以用此别名来指定。 
     * 1、每个用户只能指定一个别名。 
     * 2、同一个应用程序内，对不同的用户，建议取不同的别名。这样，尽可能根据别名来唯一确定用户。 
     * 3、系统不限定一个别名只能指定一个用户。如果一个别名被指定到了多个用户，当给指定这个别名发消息时，服务器端API会同时给这多个用户发送消息。 
     * 举例：在一个用户要登录的游戏中，可能设置别名为 userid。游戏运营时，发现该用户 3 天没有玩游戏了，则根据 userid 调用服务器端API发通知到客户端提醒用户。 	 * 
	 * @param alias
	 */
	public void addAlias(String alias){
		if(isPluginInited()){
			this.pushPlugin.addAlias(alias);
		}
	}
	
	/**
	 * 删除别名
	 * @param alias
	 */
	public void removeAlias(String alias){
		if(isPluginInited()){
			this.pushPlugin.removeAlias(alias);
		}
	}
	
	private boolean isPluginInited(){
		if(this.pushPlugin == null){
			Log.e("U8SDK", "The push plugin is not inited or inited failed.");
			return false;
		}
		return true;
	}
}
