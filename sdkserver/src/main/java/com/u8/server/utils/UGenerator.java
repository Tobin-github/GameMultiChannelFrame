package com.u8.server.utils;

import com.u8.server.data.UMsdkOrder;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;

import java.util.Date;
import java.util.UUID;

public class UGenerator {

    /***
     * 生成appkey
     * @param appID
     * @param createTime
     * @return
     */
    public static String generateAppKey(int appID, long createTime){

        String txt = appID + "" + createTime;

        return EncryptUtils.md5(txt);
    }

    /***
     * 生成appsecret
     *
     * @return
     */
    public static String generateAppSecret(){
        UUID uuid = UUID.randomUUID();
        return EncryptUtils.md5(uuid.toString());
    }

    /***
     * 生成用于生成登录认证使用的Token
     * @param user
     * @return
     */
    public static String generateToken(UUser user, String appSecret){

        StringBuilder sb = new StringBuilder();
        sb.append(user.getId()).append("-")
                .append(user.getChannelUserID()).append("-")
                .append(user.getLastLoginTime())
                .append(appSecret);

        String txt = sb.toString();

        Log.d("The txt to generate token is " + txt);
        return EncryptUtils.md5(txt);
    }


}
