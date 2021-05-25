package com.u8.server.service;

import com.u8.server.dao.UUserLoginLogDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UUser;
import com.u8.server.data.UUserLoginLog;
import com.u8.server.sdk.SDKVerifyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("userLoginLogManager")
public class UUserLoginLogManager {

    @Autowired
    private UUserLoginLogDao userLoginLogDao;

    public UUserLoginLog generateUserLoginLog(UUser user,SDKVerifyResult cpUserInfo){

        UUserLoginLog userLoginLog = new UUserLoginLog();
        userLoginLog.setUserID(user.getId());
        userLoginLog.setUserName(user.getChannelUserName());
        userLoginLog.setNickName(user.getChannelUserNick());
        userLoginLog.setExtension(cpUserInfo.getExtension());
        userLoginLog.setCreateDate(new Date());

        userLoginLogDao.save(userLoginLog);

        return userLoginLog;
    }
}
