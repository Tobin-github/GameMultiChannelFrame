package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.dao.IAPGoodsINfoDao;
import com.u8.server.data.IAPGoodsInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iAPGoodsInfoManager")
public class IAPGoodsInfoManager {

    @Autowired
    private IAPGoodsINfoDao infosDao;


    //根据cp渠道ID获取u8的渠道
    public IAPGoodsInfos queryInfo(int proID,int UChannelID){

        String hql = "from IAPGoodsInfos where proID = ? and UChannelID = ?";
        IAPGoodsInfos cEx = (IAPGoodsInfos)infosDao.findUnique(hql, proID,UChannelID);
        return cEx;
    }


    public void saveInfo(IAPGoodsInfos channel){
        infosDao.save(channel);
    }

    public void deleteInfo(IAPGoodsInfos channel){
        infosDao.delete(channel);
    }
}
