package com.u8.server.sdk.downjoy;

/**
 * 当乐SDK登录认证返回的结果
 * Created by ant on 2015/2/9.
 */
public class DownjoyResponse {

    private long msg_code;
    private String msg_desc;
    private int valid;
    private int interval;
    private int times;
    private boolean roll;

    public long getMsg_code() {
        return msg_code;
    }

    public void setMsg_code(long msg_code) {
        this.msg_code = msg_code;
    }

    public String getMsg_desc() {
        return msg_desc;
    }

    public void setMsg_desc(String msg_desc) {
        this.msg_desc = msg_desc;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isRoll() {
        return roll;
    }

    public void setRoll(boolean roll) {
        this.roll = roll;
    }
}
