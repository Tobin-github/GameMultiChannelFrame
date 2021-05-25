package com.u8.server.sdk.quanmingzhushou.data;

/**
 * author Administrator on 2015-09-21.
 * 验证登陆--服务器返回报文--数据类
 */
public class GameSdkJsonChkSessionResponseData {
    protected String accountId; //用户游戏账号
    protected String nickName;  //用户昵称

    /**
     * 获得用户游戏账号
     * @return 用户游戏账号
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * 设置用户游戏账号
     * @param accountId 用户游戏账号
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * 获得用户昵称
     * @return 用户昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置用户昵称
     * @param nickName 用户昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
