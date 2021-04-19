package com.u8.sdk;

import com.u8.sdk.base.Constants;


/***
 * 统计分析插件
 * 目前接口适用于友盟游戏统计分析插件
 * @author saber
 *
 */
public interface IAnalytics extends IPlugin{
	
	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_ANALYTICS;
	
	//开始关卡的时候，调用
	public void startLevel(String level);
	
	//关卡失败时，调用
	public void failLevel(String level);
	
	//关卡结束时，调用
	public void finishLevel(String level);
	
	//开始任务
	public void startTask(String task, String type);
	
	//任务失败
	public void failTask(String task);
	
	//完成任务
	public void finishTask(String task);
	
	//充值的时候调用
	public void pay(double money, int num);
	
	//游戏中所有虚拟消费，比如用金币购买某个道具都使用 buy 方法
	public void buy(String item, int num, double price);
	
	//消耗物品的时候，调用
	public void use(String item, int num, double price);
	
	//额外获取虚拟币时，trigger 触发奖励的事件, 取值在 1~10 之间，“1”已经被预先定义为“系统奖励”， 2~10 需要在网站设置含义。
	public void bonus(String item, int num, double price, int trigger);
	
	//登录的时候调用
	public void login(String userID);
	
	//登出的时候调用
	public void logout();
	
	//当玩家建立角色或者升级时，需调用此接口 
	public void levelup(int level);
}
