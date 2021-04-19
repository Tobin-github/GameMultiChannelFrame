package com.u8.sdk.plugin;

import com.u8.sdk.IAnalytics;
import com.u8.sdk.base.PluginFactory;

/**
 * U8 统计分析插件，目前接口适用于 【友盟游戏统计分析插件】
 * @author xiaohei
 *
 */
public class U8Analytics {

	private static U8Analytics instance;
	
	private IAnalytics analyticsPlugin;
	
	private U8Analytics(){
		
	}
	
	public static U8Analytics getInstance(){
		if(instance == null){
			instance = new U8Analytics();
		}
		
		return instance;
	}
	
	public void init(){
		this.analyticsPlugin = (IAnalytics)PluginFactory.getInstance().initPlugin(IAnalytics.PLUGIN_TYPE);
	}
	
	/**
	 * 是否支持某个方法
	 * @param method
	 */
	public boolean isSupport(String method){
		if(analyticsPlugin == null){
			return false;
		}
		return analyticsPlugin.isSupportMethod(method);
	}
	
	//开始关卡的时候，调用
	public void startLevel(String level){
		if(analyticsPlugin == null){
			return;
		}
		
		analyticsPlugin.startLevel(level);
	}
	
	//关卡失败时，调用
	public void failLevel(String level){
		if(analyticsPlugin == null){
			return;
		}
		
		analyticsPlugin.failLevel(level);
	}
	
	//关卡结束时，调用
	public void finishLevel(String level){
		if(analyticsPlugin == null){
			return;
		}
		
		analyticsPlugin.finishLevel(level);
	}
	
	//开始任务
	public void startTask(String task, String type){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.startTask(task, type);
	}
	
	//任务失败
	public void failTask(String task){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.failTask(task);
	}
	
	//结束任务
	public void finishTask(String task){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.finishTask(task);
	}
	
	//充值的时候调用
	public void pay(double money , int num){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.pay(money, num);
	}
	
	//游戏中所有虚拟消费，比如用金币购买某个道具都使用 buy 方法
	public void buy(String item, int num, double price){
		if(analyticsPlugin == null){
			return;
		}
		
		analyticsPlugin.buy(item, num, price);
	}
	
	//消耗物品的时候，调用
	public void use(String item, int num, double price){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.use(item, num, price);
	}
	
	//额外获取虚拟币时，trigger 触发奖励的事件, 取值在 1~10 之间，“1”已经被预先定义为“系统奖励”， 2~10 需要在网站设置含义。
	public void bonus(String item, int num, double price, int trigger){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.bonus(item, num, price, trigger);
	}
	
	//登录的时候调用
	public void login(String userID){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.login(userID);
	}
	
	//登出的时候调用
	public void logout(){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.logout();
	}
	
	//当玩家建立角色或者升级时，需调用此接口 
	public void levelup(int level){
		if(analyticsPlugin == null){
			return;
		}
		analyticsPlugin.levelup(level);
	}
	
}
