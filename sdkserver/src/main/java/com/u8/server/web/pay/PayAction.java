package com.u8.server.web.pay;

import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKScriptExt;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.service.*;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.RSAUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/***
 * 请求获取订单号
 */
@Controller
@Namespace("/pay")

public class PayAction extends UActionSupport{

    private static Logger log = Logger.getLogger(PayAction.class.getName());

    private String userID;
    private String productID;  //当前商品ID
    private String productName;
    private String productDesc;
    private int money;          //单位 分
    private String roleID;      //玩家在游戏服中的角色ID
    private String roleName;    //玩家在游戏服中的角色名称
    private String roleLevel;   //玩家等级
    private String serverID;    //玩家所在的服务器ID
    private String serverName;  //玩家所在的服务器名称
    private String extension;
    private String notifyUrl;   //支付回调通知的游戏服地址


    private String signType;    //签名算法， RSA|MD5
    private String sign;        //RSA签名

    private String uniqueSerial;//游服渠道唯一标示符号
    private String spdata;//游服双支付渠道分辨支付类型

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UChannelExchangeManager channelExchangeManage;

    @Autowired
    private UChannelPayTypeManager payTypeManagerManage;

    @Autowired
    private UPayTypeManager payTypeManage;

    private boolean isSignOK(UUser user) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("userID=").append(this.userID).append("&")
                .append("productID=").append(this.productID == null ? "" : this.productID).append("&")
                .append("productName=").append(this.productName == null ? "" : this.productName).append("&")
                .append("productDesc=").append(this.productDesc == null ? "" : this.productDesc).append("&")
                .append("money=").append(this.money).append("&")
                .append("roleID=").append(this.roleID == null ? "" : this.roleID).append("&")
                .append("roleName=").append(this.roleName == null ? "" : this.roleName).append("&")
                .append("roleLevel=").append(this.roleLevel == null ? "" : this.roleLevel).append("&")
                .append("serverID=").append(this.serverID == null ? "" : this.serverID).append("&")
                .append("serverName=").append(this.serverName == null ? "" : this.serverName).append("&")
                .append("extension=").append(this.extension == null ? "" : this.extension);

        if(!StringUtils.isEmpty(notifyUrl)){
            sb.append("&notifyUrl=").append(this.notifyUrl);
        }


        if("rsa".equalsIgnoreCase(this.signType)){
            String encoded = URLEncoder.encode(sb.toString(), "UTF-8");
            encoded = encoded.toLowerCase();
            String key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAME10bNE2ECRwjHgQP4lfX8roKCUwUgvtzhOu7N+OM3b302VFX3U4dhgPJtru59HVQ8jDmFm5046oDwAJ7czZK+/GIZpRZw8C9ofJE5zHW5LLqBGWpdcy0cu/m1v1k8WdkxGSUJGCk1RQxHW0fRXt/BHi08D7D4NuLbB8m0Ve2OnAgMBAAECgYEAimShe1ZBzCZbwlwOUbzK4q9+U+eZdrpu8885lb1AtPvcPHcgOMympNVyNCV79AAlRj1nDI+n+Rn3MmMuD4SphhamHnzQgDuP16EUBI7oqSwJ+/Da7FltLuUo7/P8d7fNUogXVpIl6ns1c+mI9oNamVWfrz1BDSpZk/LqQckEA6ECQQDiCVe9vSV30ZZhR7yUvCsG5NWfm3FPU79uFFkCoTEbqBzvHB+loHYwqJdbsm41KSzylb1IaKKFfdPBtyYaLBO3AkEA2tKAV0LqitXa6/54Yp5n9YHbvOCdfHDvctKBCCttkMNt0AvylE3fK6Q6fvq5WKp5OsQwdGT5L+FqjzPqsoePkQJBANvRZii5Z1Ss2Gfmkbi7fcSIik9bpcgVk9cDpsRt6goRZYILgaNa91W+vuOIFLFSR8nqYVenmlXj1ilPaZiOQOUCQQCetjscD47qL/5fWOycKuSgLxXfwtK3JXqnP4MTF9yaOQT29xN0+Z46mx1KaDPy2YKgWxgB9BLA5bBSOYerAaPhAkAdNJ7mM1XM9qc3gYvw+yiMLI1b6VUITDNbZ0myK05OV2jebO7zGeeTdYcMbTp+C7q2T05Dc6nEVPqp6P9kCcv1";
            String sign2 = RSAUtils.sign(encoded, key, "UTF-8", "SHA1withRSA");

            log.debug("The encoded getOrderID sign is "+encoded);
            log.debug("The getOrderID sign is "+sign);
            sign = sign.replace(" ","+");
            String pubKey = user.getGame().getAppRSAPubKey();
            log.debug("----------------->sign's sb:"+sb.toString()+", sign:"+sign+", pubKey:"+user.getGame().getAppRSAPubKey()+", priKey:"+user.getGame().getAppRSAPubKey());
            boolean b = RSAUtils.verify(encoded, sign2,  user.getGame().getAppRSAPubKey(), "UTF-8", "SHA1withRSA");
            boolean a = RSAUtils.verify(encoded, sign,  user.getGame().getAppRSAPubKey(), "UTF-8", "SHA1withRSA");
            return RSAUtils.verify(encoded, sign,  user.getGame().getAppRSAPubKey(), "UTF-8", "SHA1withRSA");
        }

        //md5 sign
        sb.append(user.getGame().getAppkey());

        log.debug("the appkey:"+user.getGame().getAppkey());

        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");

        log.debug("The encoded getOrderID sign is "+encoded);
        log.debug("The getOrderID sign is "+sign);
        String newSign = EncryptUtils.md5(encoded);

        log.debug("the sign now is md5; newSign:"+newSign);
        return newSign.toLowerCase().equals(this.sign);

    }
    

    @Action("getOrderID")
    public void getOrderID(){
        try{

            UUser user = null;
            UChannel channel=null;
            this.spdata = this.request.getParameter("spdata");
            log.debug("-------->getOrderID ,spdata:"+this.spdata+", userID:"+this.userID);

            //兼容宝石内部生成uid，相当于没走我们登录，直接调支付
            if(this.userID.startsWith("BSFB")){
                this.uniqueSerial = this.request.getParameter("uniqueSerial");
                log.debug("-------->getOrderID BSFB user getOrder,uniqueSerial:"+uniqueSerial);
                String[] arr = this.uniqueSerial.split("-");
                //final UChannel channel = channelExchangeManage.queryChannel(arr[0]);
                channel = channelExchangeManage.queryChannel(arr[0]);
                String bsUID = this.userID.substring(4);
                SDKVerifyResult sdkResult = new SDKVerifyResult();
                sdkResult.setUserID(bsUID);
                sdkResult.setUserName(this.userID);
                sdkResult.setNickName(this.userID);
                user = userManager.getUserByCpID(channel.getAppID(),channel.getChannelID(),bsUID);
                if(user==null){
                    user = userManager.generateUser(channel, sdkResult);
                    log.error("-------->getOrderID BSFB user is null,appid:" + channel.getAppID()+", channelID:"+channel.getChannelID()+", bsUID:"+bsUID);
                }
            }else{
                log.debug("-------->getOrderID user getOrder");
                user = userManager.getUser(Integer.parseInt(this.userID));
                log.debug("-------->getOrderID user:"+user);
                channel=user.getChannel();
            }


            if(user == null){
                log.error("the user is not found. userID:"+this.userID);
                renderState(StateCode.CODE_USER_NONE, null);
                return;
            }

            if(money < 0 ){
                log.error("the money is not valid. money:"+ money);
                renderState(StateCode.CODE_MONEY_ERROR, null);
                return;
            }

            if(!isSignOK(user)){

                log.error("the sign is not valid. sign:"+this.sign);
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            if(!user.getChannel().isPayOpen()){
                log.error("the pay not opened in u8server manage system.. ");
                renderState(StateCode.CODE_PAY_CLOSED, null);
                return;
            }

            final UOrder order = orderManager.generateOrder(user, money, productID, productName, productDesc, roleID,roleName,serverID,serverName, extension, notifyUrl);
            log.debug("----------------->the generateOrder:" + order.toJSON());

            if(order != null){
                ISDKScript script = SDKCacheManager.getInstance().getSDKScript(order.getChannel());

                if(script == null) {
                    log.error("the ISDKScript is not found. channelID:" + order.getChannelID());
                    renderState(StateCode.CODE_ORDER_ERROR, null);
                    return;
                }

                log.debug("----------------->the spdata:" + spdata+", platType:"+channel.getPlatType());

                if(this.spdata!=null&&1==(channel.getPlatType())) {
                    ISDKScriptExt scriptExt = (ISDKScriptExt) script;
                    int payType = Integer.parseInt(this.spdata);

                    order.setPayType(payType);
                    order.setPlatID(channel.getPlatID());

                    //设置多渠道支付方式
                    int checkPayType = payTypeManage.checkPayType(order);

                    if (checkPayType != -1) {
                        order.setPayType(checkPayType);
                    }

                    orderManager.saveOrder(order);

                    scriptExt.onGetOrderIDByType(user, order, payType, payTypeManagerManage, new ISDKOrderListener() {
                        @Override
                        public void onSuccess(String jsonStr) {

                            JSONObject data = new JSONObject();
                            data.put("orderID", order.getOrderID());
                            data.put("extension", jsonStr);

                            log.info("------------->The onGetOrderIDByType extension is " + jsonStr);

                            renderState(StateCode.CODE_SUCCESS, data);

                        }

                        @Override
                        public void onFailed(String err) {
                            log.error("------------->The onGetOrderIDByType extension fail:"+err);

                            JSONObject data = new JSONObject();
                            data.put("orderID", order.getOrderID());
                            data.put("extension", "");
                            renderState(StateCode.CODE_SUCCESS, data);
                        }
                    });
                    return;

                }

                log.debug("----------------->ready to onGetOrderID,the spdata:" + spdata);
                if (this.spdata != null) {
                    int payType = Integer.parseInt(this.spdata);

                    //设置渠道支付方式
                    order.setPayType(payType);
                    order.setPlatID(channel.getPlatID());

                    int checkPayType = payTypeManage.checkPayType(order);

                    if (checkPayType != -1) {
                        order.setPayType(checkPayType);
                    }

                    orderManager.saveOrder(order);
                } else {
                    int checkPayType = payTypeManage.checkPayType(order);

                    if (checkPayType != -1) {
                        order.setPayType(checkPayType);
                    }
                    order.setPlatID(channel.getPlatID());
                    orderManager.saveOrder(order);
                }

                script.onGetOrderID(user, order, new ISDKOrderListener() {
                    @Override
                    public void onSuccess(String jsonStr) {

                        JSONObject data = new JSONObject();
                        data.put("orderID", order.getOrderID());
                        data.put("extension", jsonStr);

                        log.error("---------------->The onGetOrderID extension is "+jsonStr);

                        renderState(StateCode.CODE_SUCCESS, data);

                    }

                    @Override
                    public void onFailed(String err) {

                        log.error("------------->The onGetOrderID onGetOrderID extension fail:"+err);

                        JSONObject data = new JSONObject();
                        data.put("orderID", order.getOrderID());
                        data.put("extension", "");
                        renderState(StateCode.CODE_SUCCESS, data);
                    }
                });

            }


        }catch (Exception e){
            renderState(StateCode.CODE_ORDER_ERROR, null);
            log.error("-------------->"+e.getMessage());
        }


    }

    private void renderState(int state, JSONObject data){
        JSONObject json = new JSONObject();
        json.put("state", state);
        json.put("data", data);
        super.renderJson(json.toString());
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }
}
