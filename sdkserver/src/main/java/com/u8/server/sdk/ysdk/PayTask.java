package com.u8.server.sdk.ysdk;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.TimeUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 支付任务
 * Created by ant on 2015/10/14.
 */
public class PayTask implements Runnable, Delayed{

    public static final int STATE_INIT = 1;        //第一次状态
    public static final int STATE_COMPLETE = 2;     //完成状态
    public static final int STATE_RETRY = 3;        //重试状态
    public static final int STATE_FAILED = 4;       //失败状态

    private PayRequest payRequest;

    private int state = STATE_INIT;          //任务状态
    private long time;                       //任务执行时间
    private int retryCount = 0;              //已经重试的次数
    private int maxRetryCount;

    public PayTask(PayRequest req, long delayMillis, int maxRetryCount){

        this.payRequest = req;
        this.state = STATE_INIT;
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
        this.retryCount = 0;
        this.maxRetryCount = maxRetryCount;
    }

    public void setDelay(long delayMillis){
        this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayMillis, TimeUnit.MILLISECONDS);
    }

    public PayRequest getPayRequest() {
        return payRequest;
    }

    public void setPayRequest(PayRequest payRequest) {
        this.payRequest = payRequest;
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
        PayTask task = (PayTask)o;
        long result = this.getTime() - task.getTime();

        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }

    /**
     * 时间到了，执行支付逻辑
     */
    @Override
    public void run() {

        if(this.state == STATE_COMPLETE || this.state == STATE_FAILED){
            return;
        }

        try{
            Log.d("now to execute a new ysdk pay req. id:%s; userID : %s; retryNum: %s ", payRequest.getId(), payRequest.getUser().getId(), retryCount);

            int coinNum = this.payRequest.getCoinNum();

            if(coinNum > 0){

                JSONObject payResult = YSDKApi.charge(this.payRequest);

                if( payResult != null ){

                    this.state = STATE_COMPLETE;

                    UOrderManager orderManager = (UOrderManager) UApplicationContext.getBean("orderManager");
                    if(orderManager != null){

                        UOrder order = this.payRequest.getOrder();
                        order.setRealMoney(order.getMoney());
                        order.setChannelOrderID("");
                        order.setSdkOrderTime(TimeUtils.format_yyyyMMddHHmmss(new Date()));
                        order.setCompleteTime(new Date());
                        order.setState(PayState.STATE_SUC);
                        orderManager.saveOrder(order);
                        SendAgent.sendCallbackToServer(orderManager, order);
                    }

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
            Log.e(e.getMessage());
            e.printStackTrace();
        }
    }

}
