package com.u8.server.sdk;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;

/**
 * SDK操作脚本
 * 第三方登录认证的校验
 *
 * 关于项目中编码问题
 * tomcat中sever.xml中Connector中需要设置编码为utf-8
 * web.xml中
 * struts.xml中
 * 都设置为utf-8
 * payType
 */
public interface ISDKScriptExt extends  ISDKScript {


    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback);

    public void onGetOrderIDByType(UUser user, UOrder order, int payType, UChannelPayTypeManager manager, ISDKOrderListener callback);

}