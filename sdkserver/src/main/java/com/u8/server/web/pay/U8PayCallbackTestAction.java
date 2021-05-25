package com.u8.server.web.pay;

import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UStatistics;
import com.u8.server.log.Log;
import com.u8.server.service.*;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.RSAUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 这个是U8Server通知游戏服的Demo
 * 模拟游戏服务器的处理支付回调的接口
 * Created by ant on 2015/2/9.
 */
@Controller
@Namespace("/pay/game")
public class U8PayCallbackTestAction extends UActionSupport {

    private static Logger log = Logger.getLogger(U8PayCallbackTestAction.class.getName());

    private Integer appId;
    private Integer channelId;
    private int userId;

    @Autowired
    private UUserManager mUUserManager;
    @Autowired
    private UOrderManager mOrderManager;
    @Autowired
    private UPayTypeManager payTypeManage;
    @Autowired
    private LogDataManager mLogDataManager;
    @Autowired
    private UChannelManager mChannelManager;

    @Action("payCallback")
    public void payCallback() {

        try {
            /*List<UChannel> uChannels = mChannelManager.queryAll();

            for (UChannel channel :
                    uChannels) {*/

                /*if (channel.getAppID() == 1) {
                    continue;
                }
                if (channel.getAppID() == 2) {
                    continue;
                }*/

            UStatistics statistics = mLogDataManager.getStatisticsByAppId(appId);

            UChannel channel = mChannelManager.getChannelByChannelId(channelId);

            System.out.println("--------> statistics:" + statistics.toJSON());

            String token = mLogDataManager.getLogToken(statistics.getCid(), statistics.getUserName(), statistics.getPassword());

            //UUser user = mUUserManager.getUser(userId);

            String code = mLogDataManager.logChannelData(statistics.getCid(), channel, token);
            System.out.println(code);
            //}



            /*String logRegisterResult = mLogDataManager.logRegisterData(statistics.getCid(), user, token);
            String logLoginResult = mLogDataManager.logLoginData(statistics.getCid(), user, token);

            UOrder order = mOrderManager.getOrder(1472937357463257103L);

            String logOrderResult = mLogDataManager.logOrderData(statistics.getCid(), order, token);*/
            /*UUser user = mUUserManager.getUser(userId);

            String logToken = LogDataManager.getToken();
            String registerCode = mLogDataManager.logRegisterData(user, logToken);

            if ("401".equals(registerCode)) {
                String logRegisterAgain = mLogDataManager.getLogToken();
                LogDataManager.setToken(logRegisterAgain);
                mLogDataManager.logRegisterData(user, logRegisterAgain);
            }

            String logToken2 =LogDataManager.getToken();

            String loginCode = mLogDataManager.logLoginData(user, logToken2);

            if ("401".equals(loginCode)) {
                String logLoginAgain = mLogDataManager.getLogToken();
                LogDataManager.setToken(logLoginAgain);
                mLogDataManager.logRegisterData(user, logLoginAgain);
            }


            BufferedReader br = this.request.getReader();
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine()) != null){
                sb.append(line).append("\r\n");
            }

            Log.d("U8Server Pay Callback . response params:" + sb.toString());

            JSONObject json = JSONObject.fromObject(sb.toString());
            if(json.containsKey("state")){
                renderText("SUCCESS");

                int state = json.getInt("state");
                if(state == 1){
                    String dataStr = json.getString("data");
                    Log.d("the data is "+dataStr);
                    U8PayCallbackData data = (U8PayCallbackData)JsonUtils.decodeJson(dataStr, U8PayCallbackData.class);

                    Log.d("the data obj:"+data);

                    if(isSignOK(data)){
                        Log.d("U8Server Pay Callback. verify sign ok.");
                        //TODO:这里游戏服务器，发放游戏币给玩家
                    }else{
                        Log.d("U8Server Pay Callback. verify sign failed.");
                    }

                }

            }else{
                renderText("FAIL");
            }*/


        } catch (Exception e) {
            renderText("FAIL");
            e.printStackTrace();
        }

    }

    private boolean isSignOK(U8PayCallbackData order) {

        String appSecret = "7513a2c235647e3213538c6eb329eec9";
        Log.d("channelID:" + order.getChannelID());
        Log.d("currency:" + order.getCurrency());
        Log.d("extension:" + order.getExtension());

        StringBuilder sb = new StringBuilder();
        sb.append("channelID=").append(order.getChannelID()).append("&")
                .append("currency=").append(order.getCurrency()).append("&")
                .append("extension=").append(order.getExtension()).append("&")
                .append("gameID=").append(order.getGameID()).append("&")
                .append("money=").append(order.getMoney()).append("&")
                .append("orderID=").append(order.getOrderID()).append("&")
                .append("productID=").append(order.getProductID()).append("&")
                .append("serverID=").append(order.getServerID()).append("&")
                .append("userID=").append(order.getUserID()).append("&")
                .append(appSecret);

        if ("md5".equalsIgnoreCase(order.getSignType())) {
            return EncryptUtils.md5(sb.toString()).toLowerCase().equals(order.getSign());
        } else {
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDtJvawWjhQhI+J3EnD3gvuh+t1zB4bOMW9PJUdk27YQDyiGVd42QdHLofdTN1yXKXYZR1Bmy4W1pZhucSoDdS7fGfkKHm3zRMsijNOiPWHg0spMEchI4YTlIC43iFVdzSPE/2sIZfrW/9MspXfuWqFySsTsf6c6qJc6A0bNKJhMwIDAQAB";
            return RSAUtils.verify(sb.toString(), order.getSign(), publicKey, "UTF-8", "SHA1withRSA");
        }
    }

    public static class U8PayCallbackData {
        private String productID;
        private String orderID;
        private String userID;
        private String channelID;
        private String gameID;
        private String serverID;
        private String money;
        private String currency;
        private String extension;

        private String signType;
        private String sign;

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public String getOrderID() {
            return orderID;
        }

        public void setOrderID(String orderID) {
            this.orderID = orderID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getChannelID() {
            return channelID;
        }

        public void setChannelID(String channelID) {
            this.channelID = channelID;
        }

        public String getGameID() {
            return gameID;
        }

        public void setGameID(String gameID) {
            this.gameID = gameID;
        }

        public String getServerID() {
            return serverID;
        }

        public void setServerID(String serverID) {
            this.serverID = serverID;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
