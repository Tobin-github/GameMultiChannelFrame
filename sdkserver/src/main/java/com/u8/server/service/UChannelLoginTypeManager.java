package com.u8.server.service;

import com.u8.server.dao.UChannelLoginTypeDao;
import com.u8.server.data.UChannelloginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uChannelLoginTypeManager")
public class UChannelLoginTypeManager {

    @Autowired
    private UChannelLoginTypeDao channelDao;


    //根据cp渠道ID获取u8的渠道
    public UChannelloginType queryInfo(int cpChannelID,int loginType){

        String hql = "from UChannelloginType where UChannelID = ? and loginType = ?";
        UChannelloginType cEx = (UChannelloginType)channelDao.findUnique(hql, cpChannelID,loginType);
        return cEx;
    }


    public void saveChannel(UChannelloginType channel){
        channelDao.save(channel);
    }

    public void deleteChannel(UChannelloginType channel){
        channelDao.delete(channel);
    }
}
