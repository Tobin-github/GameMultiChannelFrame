package com.u8.server.service;

import com.u8.server.common.OrderParameter;
import com.u8.server.common.OrderParameters;
import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.constants.PayState;
import com.u8.server.dao.UOrderDao;
import com.u8.server.data.UMsdkOrder;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.utils.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("orderManager")
public class UOrderManager {

    @Autowired
    private UOrderDao orderDao;

    public UOrder getOrder(long orderID){

        return orderDao.get(orderID);
    }

    public void saveOrder(UOrder order){
        orderDao.save(order);
    }

    public void saveUMsdkOrder(UMsdkOrder order){
        orderDao.getSession().saveOrUpdate(order);
    }

    public void deleteOrder(UOrder order){
        orderDao.delete(order);
    }

    public UOrder generateOrder(UUser user, int money, String productID, String productName, String productDesc, String roleID, String roleName, String serverID, String serverName,String extension,String notifyUrl){

        UOrder order = new UOrder();
        order.setOrderID(IDGenerator.getInstance().nextOrderID());
        order.setAppID(user.getAppID());
        order.setChannelID(user.getChannelID());
        order.setMoney(money);
        order.setProductID(productID);
        order.setProductName(productName);
        order.setProductDesc(productDesc);
        order.setCurrency("RMB");
        order.setUserID(user.getId());
        order.setUsername(user.getName());
        order.setExtension(extension);
        order.setState(PayState.STATE_PAYING);
        order.setChannelOrderID("");
        order.setRoleID(roleID);
        order.setRoleName(roleName);
        order.setServerID(serverID);
        order.setServerName(serverName);
        order.setCreatedTime(new Date());
        order.setNotifyUrl(notifyUrl);

        orderDao.save(order);

        return order;
    }

    public UMsdkOrder generateMsdkOrder(UUser user, String channelOrderID, int coinNum, boolean firstPay, int allMoney){

        UMsdkOrder order = new UMsdkOrder();
        order.setAppID(user.getAppID());
        order.setChannelID(user.getChannelID());
        order.setUserID(user.getId());
        order.setUsername(user.getName());
        order.setCoinNum(coinNum);
        order.setFirstPay(firstPay ? 1:0);
        order.setAllMoney(allMoney);
        order.setChannelOrderID(channelOrderID);
        order.setCreatedTime(new Date());

        orderDao.getSession().saveOrUpdate(order);

        return order;
    }

    //分页查找
    public Page<UOrder> queryPage(int currPage, int num){

        PageParameter page = new PageParameter(currPage, num, true);
        OrderParameters order = new OrderParameters();
        order.add("id", OrderParameter.OrderType.DESC);
        String hql = "from UOrder";
        return orderDao.find(page, hql, null, order);
    }
}
