package com.u8.server.sdk.ysdk;

import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用宝SDK相关支付逻辑
 * Created by ant on 2015/10/14.
 */
//@Component("ysdkManager")
//@Scope("singleton")
public class YSDKManager {

    private static final long DELAY_MILLIS = 20000;      //每次延迟执行间隔,ms
    private static final int MAX_RETRY_NUM = 6;         //最多重试6次

    private static YSDKManager instance;

    private DelayQueue<PayTask> tasks;

    private ExecutorService executor;

    private boolean isRunning = false;

    public YSDKManager(){
        this.tasks = new DelayQueue<PayTask>();
        executor = Executors.newFixedThreadPool(3);
    }

    public static YSDKManager getInstance(){
        if(instance == null){
            instance = new YSDKManager();
        }
        return instance;
    }


    /***
     * 获取当前指定用户的队列中所有支付任务中游戏币的总和
     */
    public int getTotalCoinNum(int userID){

        int coinNum = 0;
        for(PayTask task : this.tasks){
            if(task.getPayRequest().getUser().getId() == userID && (task.getState() == PayTask.STATE_INIT || task.getState() == PayTask.STATE_RETRY) ){
                coinNum += task.getPayRequest().getCoinNum();
            }
        }

        return coinNum;
    }


    //添加一个新支付请求到队列中
    public void addPayRequest(PayRequest req){

        PayTask task = new PayTask(req, 100, MAX_RETRY_NUM);
        this.tasks.add(task);

        if(!isRunning){
            isRunning = true;
            execute();
        }
    }

    public void execute(){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{

                    while(isRunning){
                        PayTask task = tasks.take();
                        task.run();
                        if(task.getState() == PayTask.STATE_RETRY){
                            task.setDelay(DELAY_MILLIS * task.getRetryCount());
                            tasks.add(task);
                        }else if(task.getState() == PayTask.STATE_FAILED){
                            Log.e("the user[%s](channel userID:%s) charge failed.", task.getPayRequest().getUser().getId(), task.getPayRequest().getUser().getChannelUserID());
                        }
                    }

                }catch (Exception e){
                    Log.e(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void destory(){
        this.isRunning = false;
        if(executor != null){
            executor.shutdown();
            executor = null;
        }
    }



    //***************************Test Begin******************************
    static int id = 1;
    public static void main(String[] args){



        for (int i=0; i<10; i++){

            addTestPayRequest(id++);
        }

        Thread t = new Thread(new Runnable(){

            public void run(){
                try{

                    for(int i=0; i<10; i++){
                        Thread.sleep(3000);
                        addTestPayRequest(id++);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
        t.start();
    }

    private static void addTestPayRequest(int id){
        UUser user = new UUser();
        user.setId(62);
        user.setChannelID(51);

        UOrder order = new UOrder();
        order.setOrderID(960007447869652993L);
        order.setMoney(100);
        order.setChannelID(51);
        order.setState(PayState.STATE_PAYING);


        PayRequest req = new PayRequest();
        req.setId(id);
        req.setUser(user);
        req.setAccountType(1);
        req.setOpenID("3245435");
        req.setOpenKey("235435435");
        req.setPf("345345435");
        req.setPfkey("sdfsdfsdfsdf");
        req.setZoneid("1");
        req.setOrder(order);
        YSDKManager.getInstance().addPayRequest(req);
    }

    //***************************Test End ******************************
}
