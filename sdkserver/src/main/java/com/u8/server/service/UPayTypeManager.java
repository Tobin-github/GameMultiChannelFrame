package com.u8.server.service;

import com.u8.server.dao.UPayTypeDao;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UOrder;
import com.u8.server.data.UPayType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("payTypeManager")
public class UPayTypeManager {

    private static Logger log = Logger.getLogger(UPayTypeManager.class.getName());

    @Autowired
    private UPayTypeDao payTypeDao;

    public UPayType getPayTypeByMasterId(int masterID){

        String hql = "from UPayType where masterID = ?";

        return (UPayType)payTypeDao.findUnique(hql, masterID);

    }

    public UPayType getPayTypeByTypeId(int payTypeId){

        String hql = "from UPayType where payTypeId = ?";

        return (UPayType)payTypeDao.findUnique(hql, payTypeId);

    }

    public int checkPayType(UOrder order) {
        UChannelMaster master = order.getChannel().getMaster();
        if (master == null) {
            return -1;
        }
        log.info("----------------------->master's name:"+master.getMasterName()+", masterId:"+master.getMasterID());

        if ("BSGF".equals(master.getMasterName()) || "AppStore".equals(master.getMasterName())) {

            if (null == order.getPayType()) {
                UPayType originalPayType = getPayTypeByMasterId(master.getMasterID());
                order.setPayType(originalPayType.getPayTypeId());
            }

            log.info("----------------------->官方包支付方式:"+order.getPayType());
            System.out.println("------------------------>order.getPayType():"+order.getPayType());
            return order.getPayType();
        }



        UPayType payType = getPayTypeByMasterId(master.getMasterID());

        if (payType == null) {
            return -1;
        }

        log.info("----------------------->第三方包支付方式:"+payType.getPayTypeId());
        return payType.getPayTypeId();
    }
}
