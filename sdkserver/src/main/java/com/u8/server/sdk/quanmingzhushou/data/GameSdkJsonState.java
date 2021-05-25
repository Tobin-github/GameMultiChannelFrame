package com.u8.server.sdk.quanmingzhushou.data;


/**
 * author Administrator on 2015-09-21.
 */
public class GameSdkJsonState {
    protected int code;
    protected String msg;


    /**
     * 获得一个指定的状态对象
     * @param code 状态编码
     * @param msg 状态信息
     * @return GameSdkJsonState
     */
    public static GameSdkJsonState getSepStatus(int code,String msg){
        GameSdkJsonState err=new GameSdkJsonState();
        err.setCode(code);
        err.setMsg(msg);
        return err;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
