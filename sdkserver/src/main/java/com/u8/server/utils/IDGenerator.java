package com.u8.server.utils;

import com.u8.server.cache.CacheManager;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UGame;
import com.u8.server.service.UChannelManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 这个是一个简单的唯一ID生成器
 * 在这个应用中，我们需要生成appID, channelID，orderID等
 */
public class IDGenerator {

    private static IDGenerator instance;

    private AtomicInteger currAppID;
    private AtomicInteger currMasterID;
    private AtomicInteger currChannelID;

    private AtomicInteger currOrderSequence = new AtomicInteger(1);
    //private int currOrderSequence = 1;

    private IDGenerator(){

        loadCurrMaxID();

    }

    public synchronized static IDGenerator getInstance(){
        if(instance == null){
            instance = new IDGenerator();
        }

        return instance;
    }

    private void loadCurrMaxID(){
        int maxID = 0;

        for(UGame game : CacheManager.getInstance().getGameList()){
            if(maxID < game.getAppID()){
                maxID = game.getAppID();
            }
        }

        this.currAppID = new AtomicInteger(maxID);

        maxID = 0;
        for(UChannelMaster master : CacheManager.getInstance().getMasterList()){
            if(maxID < master.getMasterID()){
                maxID = master.getMasterID();
            }
        }

        this.currMasterID = new AtomicInteger(maxID);

        maxID = 0;
        for(UChannel channel : CacheManager.getInstance().getChannelList()){
            if(maxID < channel.getChannelID()){
                maxID = channel.getChannelID();
            }
        }

        this.currChannelID = new AtomicInteger(maxID);
    }

    public int nextAppID(){


        return this.currAppID.incrementAndGet();
    }

    public int nextMasterID(){
        return this.currMasterID.incrementAndGet();
    }

    /**
     * 当前将渠道号，改为后台可以手动设置和修改的方式了
     * 不再自动生成。因为很多业务中，创建渠道或者后面修改渠道的时候，都有可能需要指定渠道号或者变更渠道号
     * @return
     */
    @Deprecated
    public int nextChannelID(){

        return this.currChannelID.incrementAndGet();
    }

    public long nextOrderID(){

        int seq = this.currOrderSequence.getAndIncrement();

        Calendar can = Calendar.getInstance();
        int year = can.get(Calendar.YEAR) - 2013;
        int month = can.get(Calendar.MONTH) + 1;
        int day = can.get(Calendar.DAY_OF_MONTH);
        int hour = can.get(Calendar.HOUR_OF_DAY);
        int min = can.get(Calendar.MINUTE);
        int sec = can.get(Calendar.SECOND);

        long orderId = year;
        orderId = orderId << 4 | month;
        orderId = orderId << 5 | day;
        orderId = orderId << 5 | hour;
        orderId = orderId << 6 | min;
        orderId = orderId << 6 | sec;
        orderId = orderId << 32| seq;

        return orderId;
    }

//    public static void main(String[] ags){
//
//        final List<Long> lst = new ArrayList<Long>();
//
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0; i<500; i++){
//                    long id = IDGenerator.getInstance().nextOrderID();
//                    if(lst.contains(id)){
//                        System.out.println("duplicated id:"+id+"; bcode:"+Long.toBinaryString(id));
//
//                    }else{
//                        lst.add(id);
//                    }
//                }
//
//            }
//        };
//
//        for(int i=0; i<10; i++){
//            Thread t = new Thread(r);
//            t.start();
//        }
//
//
//    }
}
