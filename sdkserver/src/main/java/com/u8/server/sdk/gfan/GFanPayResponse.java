package com.u8.server.sdk.gfan;

/**
 * 支付回调结果
 * Created by ant on 2015/12/3.
 */
public class GFanPayResponse {

    private String order_id;
    private String appkey;
    private int cost;
    private String create_time;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
