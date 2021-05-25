package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.dao.UChannelDao;
import com.u8.server.data.UChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
@Service("channelManager")
public class UChannelManager {

    @Autowired
    private UChannelDao channelDao;

    //根据渠道AppId获取渠道信息
    public UChannel getChannelByAppId(String appId){

        String hql = "from UChannel where cpAppID = ?";

        return (UChannel)channelDao.findUnique(hql, appId);

    }

    //根据渠道channelId获取渠道信息
    public UChannel getChannelByChannelId(Integer channelId){

        String hql = "from UChannel where channelID = ?";

        return (UChannel)channelDao.findUnique(hql, channelId);

    }

    public UChannel generateChannel(int appID, int masterID, String cpID, String cpAppID, String cpAppKey, String cpAppSecret, String cpPayKey){

        return generateChannel(appID, masterID, cpID, cpAppID, cpAppKey, cpAppSecret, cpPayKey, "", "");
    }

    public UChannel generateChannel(int appID, int masterID, String cpID, String cpAppID, String cpAppKey, String cpAppSecret, String cpPayKey, String cpPayPriKey, String cpPayID){

        UChannel channel = new UChannel();
        channel.setAppID(appID);
        channel.setMasterID(masterID);

        channel.setCpID(cpID);
        channel.setCpAppID(cpAppID);
        channel.setCpAppKey(cpAppKey);
        channel.setCpAppSecret(cpAppSecret);
        channel.setCpPayKey(cpPayKey);
        channel.setCpPayPriKey(cpPayPriKey);
        channel.setCpPayID(cpPayID);



        saveChannel(channel);

        return channel;
    }

    public int getChannelCount(){

        return CacheManager.getInstance().getChannelList().size();
    }

    //获取当前一个可用的渠道号，默认算法是获取一个当前最大渠道号+1
    public int getValidChannelID(){

        List<UChannel> lst = CacheManager.getInstance().getChannelList();

        int max = 0;

        for(UChannel c : lst){
            if(c.getChannelID() > max){
                max = c.getChannelID();
            }
        }

        return max+1;
    }

    //获取所有渠道商信息
    public List<UChannel> queryAll(){

        return CacheManager.getInstance().getChannelList();
    }

    //分页查找
    public List<UChannel> queryPage(int currPage, int num){

        List<UChannel> channels = CacheManager.getInstance().getChannelList();

        Collections.sort(channels, new Comparator<UChannel>() {
            @Override
            public int compare(UChannel o1, UChannel o2) {
                return o1.getChannelID() - o2.getChannelID();
            }
        });

        int fromIndex = (currPage-1) * num;

        if(fromIndex >= channels.size()){

            return null;
        }

        int endIndex = Math.min(fromIndex + num, channels.size());

        return channels.subList(fromIndex, endIndex);
    }

    //添加或者修改channel
    public void saveChannel(UChannel channel){

        if(channel.getChannelID() <= 0){
            channel.setChannelID(getValidChannelID());
        }


        channelDao.save(channel);
        CacheManager.getInstance().saveChannel(channel);
    }

    public UChannel queryChannel(int id){

        return CacheManager.getInstance().getChannel(id);
    }

    public void deleteChannel(UChannel channel){
        if(channel == null){
            return;
        }


        channelDao.delete(channel);
        CacheManager.getInstance().removeChannel(channel.getChannelID());
    }


}
