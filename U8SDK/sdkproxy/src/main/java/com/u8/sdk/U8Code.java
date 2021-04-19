package com.u8.sdk;

public class U8Code {

	/**
	 * 没有网络连接
	 */
	public static final int CODE_NO_NETWORK = 0;
	
	/**
	 * 初始化成功
	 */
	public static final int CODE_INIT_SUCCESS = 1;
	
	/**
	 * 初始化失败
	 */
	public static final int CODE_INIT_FAIL = 2;
	
	/**
	 * 没有初始化
	 */
	public static final int CODE_UNINIT = 3;
	
	/**
	 * 登录成功
	 */
	public static final int CODE_LOGIN_SUCCESS = 4;
	
	/**
	 * 登录失败
	 */
	public static final int CODE_LOGIN_FAIL = 5;
	
	/**
	 * 登录超时
	 */
	public static final int CODE_LOGIN_TIMEOUT = 6;
	
	/**
	 * 没有登录
	 */
	public static final int CODE_UNLOGIN = 7;
	
	/**
	 * 登出成功
	 */
	public static final int CODE_LOGOUT_SUCCESS = 8;
	
	/**
	 * 登出失败
	 */
	public static final int CODE_LOGOUT_FAIL = 9;
	
	/**
	 * 支付成功
	 */
	public static final int CODE_PAY_SUCCESS = 10;
	
	/**
	 * 支付失败
	 */
	public static final int CODE_PAY_FAIL = 11;
	
	/**
	 * 添加Tag成功
	 */
	public static final int CODE_TAG_ADD_SUC = 12;
	
	/**
	 * 添加Tag失败
	 */
	public static final int CODE_TAG_ADD_FAIL = 13;
	
	/**
	 * 删除Tag成功
	 */
	public static final int CODE_TAG_DEL_SUC = 14;
	
	/**
	 * 删除Tag失败
	 */
	public static final int CODE_TAG_DEL_FAIL = 15;
	
	/**
	 * 添加Alias成功
	 */
	public static final int CODE_ALIAS_ADD_SUC = 16;
	
	/**
	 * 添加Alias失败
	 */
	public static final int CODE_ALIAS_ADD_FAIL = 17;
	
	/**
	 * 删除Alias成功
	 */
	public static final int CODE_ALIAS_REMOVE_SUC = 18;
	
	/**
	 * 删除Alias失败
	 */
	public static final int CODE_ALIAS_REMOVE_FAIL = 19;
	
	/**
	 * Push 收到msg
	 */
	public static final int CODE_PUSH_MSG_RECIEVED = 20;
	
	/**
	 * 参数 错误
	 */
	public static final int CODE_PARAM_ERROR = 21;
	
	/**
	 * 参数不全
	 */
	public static final int CODE_PARAM_NOT_COMPLETE = 22;
	
	/**
	 * 分享成功
	 */
	public static final int CODE_SHARE_SUCCESS = 23;
	
	/**
	 * 分享失败
	 */
	public static final int CODE_SHARE_FAILED = 24;
	
	/**
	 * 更新成功
	 */
	public static final int CODE_UPDATE_SUCCESS = 25;
	
	/**
	 * 更新失败
	 */
	public static final int CODE_UPDATE_FAILED = 26;
	
	/**
	 * 实名注册成功
	 */
	public static final int CODE_REAL_NAME_REG_SUC = 27;
	
	/**
	 * 实名注册失败
	 */
	public static final int CODE_REAL_NAME_REG_FAILED = 28;
	
	/**
	 * 房沉迷查询结果
	 */
	public static final int CODE_ADDICTION_ANTI_RESULT = 29;
	
	/**
	 * 推送enable成功的回调，携带一个参数，比如友盟推送，这参数是Device Token
	 */
	public static final int CODE_PUSH_ENABLED = 30;
	
	/**
	 * 提交礼包兑换码成功
	 */
	public static final int CODE_POST_GIFT_SUC = 31;
	
	/**
	 * 提交礼包兑换码失败
	 */
	public static final int CODE_POST_GIFT_FAILED = 32;
	
	/**
	 * 取消支付
	 */
	public static final int CODE_PAY_CANCEL = 33;
	
	/**
	 * 支付未知
	 */
	public static final int CODE_PAY_UNKNOWN = 34;	
	
	/**
	 * 支付中
	 */
	public static final int CODE_PAYING = 35;	
}
