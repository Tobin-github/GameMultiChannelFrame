package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.OrderParameter;
import com.u8.server.common.OrderParameters;
import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.dao.UChannelExChangeDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelExchange;
import com.u8.server.log.Log;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("uChannelExchangeManager")
public class UChannelExchangeManager {

    @Autowired
    private UChannelExChangeDao channelDao;


    //根据cp渠道ID获取u8的渠道
    public UChannel queryChannel(String cpID){

        String hql = "from UChannelExchange where cpChannelID = ?";
        UChannelExchange cEx = (UChannelExchange)channelDao.findUnique(hql, cpID);
        int id = cEx.getUChannelID();
        return CacheManager.getInstance().getChannel(id);
    }

//    //根据渠道用户ID获取用户信息
//    public UUser getUserByCpID(int appID, int channelID, String cpUserID){
//
//        String hql = "from UUser where appID = ? and channelID = ? and channelUserID = ?";
//
//        return (UUser)userDao.findUnique(hql, appID, channelID, cpUserID);
//
//    }
//
//    //获取指定渠道下的所有用户
//    public List<UUser> getUsersByChannel(int channelID){
//        String hql = "from UUser where channelID = ?";
//
//        return userDao.find(hql, new Object[]{channelID}, null);
//    }
//
//    //获取用户数量
//    public long getUserCount(){
//
//        String hql = "select count(id) from UUser";
//        return userDao.findLong(hql, null);
//    }
//
//    //分页查找
//    public Page<UUser> queryPage(int currPage, int num){
//
//        PageParameter page = new PageParameter(currPage, num, true);
//        OrderParameters order = new OrderParameters();
//        order.add("id", OrderParameter.OrderType.DESC);
//        String hql = "from UUser";
//        return userDao.find(page, hql, null, order);
//    }
//
//    //获取用户的渠道分布
//    public String queryUserChannels(int appID){
//
//        String sql = "select user.channelID,count(user.id) from UUser user where user.appID=? group by user.channelID ";
//        List lst = userDao.find(sql, new Object[]{appID}, null);
//
//        StringBuilder sb = new StringBuilder();
//        if(lst != null && lst.size() > 0){
//            for(Object item : lst){
//                Object[] items = (Object[])item;
//                sb.append("['").append(items[0]).append("', ").append(items[1]).append("],");
//            }
//        }
//
//        if(sb.length() > 0){
//            sb.deleteCharAt(sb.length()-1);
//        }
//
//        return sb.toString();
//    }
//
//    public UUser getUser(int userID){
//
//        return userDao.get(userID);
//    }
//
//    //校验sign
//    public boolean isSignOK(String signStr, String sign){
//
//
//        String newSign = EncryptUtils.md5(signStr);
//
//        Log.d("The newSign is "+newSign);
//
//        return newSign.toLowerCase().equals(sign.toLowerCase());
//
//    }
//
//    public boolean checkUser(UUser user, String token){
//
//        long now = System.currentTimeMillis();
//        if(!token.equals(user.getToken()) || (now - Long.valueOf(user.getLastLoginTime())) > 3600 * 1000){
//            return false;
//        }
//
////        String tokenRight = UGenerator.generateToken(user, user.getGame().getAppSecret());
////
////        Log.d("The token is "+token +"; the right token is "+tokenRight);
//
//        return true;
//
//    }
//
//    public UUser generateUser(UChannel channel, SDKVerifyResult cpUserInfo){
//
//        UUser user = new UUser();
//        user.setAppID(channel.getAppID());
//        user.setChannelID(channel.getChannelID());
//        user.setName(System.currentTimeMillis() + channel.getMaster().getNameSuffix());
//        user.setChannelUserID(cpUserInfo.getUserID());
//        user.setChannelUserName(cpUserInfo.getUserName() == null ? "" : cpUserInfo.getUserName());
//        user.setChannelUserNick(cpUserInfo.getNickName() == null ? "" : cpUserInfo.getNickName());
//        Date now = new Date();
//        user.setCreateTime(now);
//        user.setLastLoginTime(now.getTime() + "");
//
//        userDao.save(user);
//
//        return user;
//    }

    public void saveChannel(UChannelExchange channel){
        channelDao.save(channel);
    }

    public void deleteChannel(UChannelExchange channel){
        channelDao.delete(channel);
    }
}
