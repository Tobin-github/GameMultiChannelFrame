package com.u8.server.sdk.quanmingzhushou.data;

/**
 * author Administrator on 2015-09-21.
 * 验证登陆--服务器返回报文类
 */
public class GameSdkJsonChkSessionResponse {

    protected GameSdkJsonState state;   //响应状态
    protected GameSdkJsonChkSessionResponseData data;   //响应数据
    protected GameSdkJsonExtData extData;   //备用扩展信息

    /**
     * 获得响应状态
     * @return 响应状态
     */
    public GameSdkJsonState getState() {
        return state;
    }

    /**
     * 设置响应状态
     * @param state 响应状态
     */
    public void setState(GameSdkJsonState state) {
        this.state = state;
    }

    /**
     * 获得响应数据
     * @return 响应数据
     */
    public GameSdkJsonChkSessionResponseData getData() {
        return data;
    }

    /**
     * 设置响应数据
     * @param data 响应数据
     */
    public void setData(GameSdkJsonChkSessionResponseData data) {
        this.data = data;
    }

    /**
     * 获得备用扩展信息
     * @return 备用扩展信息
     */
    public GameSdkJsonExtData getExtData() {
        return extData;
    }

    /**
     * 设置备用扩展信息
     * @param extData 备用扩展信息
     */
    public void setExtData(GameSdkJsonExtData extData) {
        this.extData = extData;
    }
}
