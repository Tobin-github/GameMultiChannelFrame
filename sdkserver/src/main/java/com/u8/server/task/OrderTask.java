package com.u8.server.task;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * U8Server处理成功的订单，但是通知游戏服务器失败时，会放到任务队列中，再次尝试一定的次数
 * Created by xiaohei on 2016/6/10.
 */
public class OrderTask implements Runnable, Delayed{

    public static final int STATE_INIT = 1;        //第一次状态
    public static final int STATE_COMPLETE = 2;     //完成状态
    public static final int STATE_RETRY = 3;        //重试状态
    public static final int STATE_FAILED = 4;       //失败状态

    private UOrder order;

    private int state = STATE_INIT;          //任务状态
    private long time;                       //任务执行时间
    private int retryCount = 0;              //已经重试的次数
    private int maxRetryCount;

    public OrderTask(UOrder order, long delayMillis, int maxRetryCount){

        this.order = order;
        this.state = STATE_INIT;
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
        this.retryCount = 0;
        this.maxRetryCount = maxRetryCount;
    }

    public void setDelay(long delayMillis){
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
    }

    public UOrder getOrder() {
        return order;
    }

    public void setOrder(UOrder order) {
        this.order = order;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }


    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        OrderTask task = (OrderTask)o;
        long result = this.getTime() - task.getTime();

        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }

    /**
     * 时间到了，执行支付逻辑
     */
    @Override
    public void run() {

        if(order.getState() != PayState.STATE_SUC){

            return;
        }

        try{
            Log.d("now to execute a new retry order. id:%s; userID : %s; retryNum: %s ", order.getOrderID(), order.getUserID(), retryCount);

            UOrderManager orderManager = (UOrderManager) UApplicationContext.getBean("orderManager");
            if(orderManager != null){
                if(SendAgent.resendCallbackToServer(orderManager, order)){
                    this.state = STATE_COMPLETE;
                    return;
                }
            }

            this.retryCount++;
            if(this.retryCount >= this.maxRetryCount){
                this.state = STATE_FAILED;

            }else{
                this.state = STATE_RETRY;
            }

        }catch (Exception e){
            this.state = STATE_FAILED;
            Log.e(e.getMessage());
            e.printStackTrace();
        }
    }

}
