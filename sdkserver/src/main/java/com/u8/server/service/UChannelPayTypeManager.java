package com.u8.server.service;

import com.u8.server.dao.UChannelLoginTypeDao;
import com.u8.server.dao.UChannelPayTypeDao;
import com.u8.server.data.UChannelloginType;
import com.u8.server.data.UChannelpayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uChannelPayTypeManager")
public class UChannelPayTypeManager {

    @Autowired
    private UChannelPayTypeDao channelDao;


    //根据cp渠道ID获取u8的渠道
    public UChannelpayType queryInfo(int cpChannelID, int payType){

        String hql = "from UChannelpayType where UChannelID = ? and payType = ?";
        UChannelpayType cEx = (UChannelpayType)channelDao.findUnique(hql, cpChannelID,payType);
        return cEx;
    }


    public void saveChannel(UChannelpayType channel){
        channelDao.save(channel);
    }

    public void deleteChannel(UChannelpayType channel){
        channelDao.delete(channel);
    }
}
