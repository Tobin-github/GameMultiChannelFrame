package com.u8.sdk;

import com.u8.sdk.base.Constants;


/***
 * 用户登录插件接口
 * @author xiaohei
 *
 */
public interface IUser extends IPlugin{
	public static final int PLUGIN_TYPE = Constants.PLUGIN_TYPE_USER;
	
	/***
	 * 登录
	 */
	public void login();
	
	/**
	 * 部分渠道可能有特殊的需求
	 * 比如MSDK，有微信登录和QQ登录。游戏可能需要在游戏中
	 * 增加微信和QQ登录按钮，然后通过customData来选择
	 * 调用的是微信还是QQ
	 * @param customData
	 */
	public void loginCustom(String customData);
	
	/***
	 * 切换帐号
	 */
	public void switchLogin();
	
	/***
	 * 显示帐号中心
	 */
	public boolean showAccountCenter();
	
	/***
	 * 登出
	 */
	public void logout();
	
	/***
	 * 发送扩展数据
	 * @param extraData
	 */
	public void submitExtraData(UserExtraData extraData);
	
	/***
	 * SDK的退出方法
	 */
	public void exit();
	
	/***
	 * 上传礼包兑换码
	 * 
	 */
	public void postGiftCode(String code);
	
	/***
	 * 实名注册方法
	 */
	public void realNameRegister();
	
	/***
	 * 防沉迷系统查询接口
	 */
	public void queryAntiAddiction();
}
