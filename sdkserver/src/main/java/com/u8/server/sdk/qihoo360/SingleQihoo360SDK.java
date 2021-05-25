package com.u8.server.sdk.qihoo360;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2015/2/27.
 */
public class SingleQihoo360SDK implements ISDKScript {

    private static Logger log = Logger.getLogger(SingleQihoo360SDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            Map<String, String> data = new HashMap<String, String>();
            data.put("access_token", extension);
            data.put("fields", "id,name,avatar,sex,area,nick");
            String url = channel.getChannelAuthUrl();
            log.info("------------->verify request's data:"+data.toString()+",auth url:"+url);

            UHttpAgent.getInstance().get(url, data, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try{
                        log.info("-------------->verify request result:"+result);
                        QihooUserInfo userInfo = (QihooUserInfo) JsonUtils.decodeJson(result, QihooUserInfo.class);

                        if(userInfo != null && !StringUtils.isEmpty(userInfo.getId())){
                            log.info("-------------->verify request result is success:"+result);
                            SDKVerifyResult vResult = new SDKVerifyResult(true, userInfo.getId(), userInfo.getName(), userInfo.getNick());
                            callback.onSuccess(vResult);
                            return;
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("------------->verify request result exception-1:"+e.getMessage());
                        QihooErrorInfo error = (QihooErrorInfo)JsonUtils.decodeJson(result, QihooErrorInfo.class);
                        if(error != null){
                            log.error("------------->verify request result exception-1:"+error.getError());
                            callback.onFailed(channel.getMaster().getSdkName()+" verify failed. msg:"+error.getError());
                            return;
                        }

                    }


                    log.error("------------->verify request result fail-1:"+result);
                    callback.onFailed(channel.getMaster().getSdkName() +" verify failed. the get result is "+result);
                }

                @Override
                public void failed(String e) {
                    log.error("------------->verify request result fail-2:"+e);
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });


        }catch(Exception e){
            log.error("------------->verify request result exception-2:"+e.getMessage());
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        String qihooUserId = user.getChannelUserID();
        //Integer moneyAmount =1;
        DecimalFormat df=new DecimalFormat("0.00");
        String orderMoney =df.format(order.getMoney()/100.00);
        String productName = order.getProductName();
        String productId = order.getProductID();

        /*String appUserName = order.getRoleName();
        String appUserId = order.getRoleID();*/

        String appUserName = "hiahia";
        String appUserId = "1";

        String appOrderId = order.getOrderID()+"";
        String productCount="1";
        /*String serverId = order.getServerID();
        String serverName = order.getServerName();*/

        String serverId = "1";
        String serverName = "heihei";

        String exchangeRate = "10";
        String gameMoneyName = "元宝";
        /*String roleId = order.getRoleID();
        String roleName = order.getRoleName();*/

        String roleId = "1";
        String roleName = "hiahia";

        String roleGrade = "999";
        String roleBalance="100";
        String roleVip="100";
        String roleUserParty="王者归来";

        JSONObject json = new JSONObject();

        try {
            json.put("qihooUserId", qihooUserId);
            json.put("moneyAmount", orderMoney);
            json.put("productName", productName);
            json.put("productId", productId);
            json.put("notifyUrl", order.getChannel().getMaster().getPayCallbackUrl());
            json.put("appUserName", appUserName);
            json.put("appUserId", appUserId);
            json.put("appOrderId", appOrderId);
            json.put("productCount", productCount);
            json.put("serverId", serverId);
            json.put("serverName", serverName);
            json.put("exchangeRate", exchangeRate);
            json.put("gameMoneyName", gameMoneyName);
            json.put("roleId", roleId);
            json.put("roleName", roleName);
            json.put("roleGrade", roleGrade);
            json.put("roleBalance", roleBalance);
            json.put("roleVip", roleVip);
            json.put("roleUserParty", roleUserParty);

            log.info("------------->order response result:"+json.toString());
        } catch (Exception e) {
            log.error("------------->order response exception:"+e.getMessage());
            e.printStackTrace();
        }

        if (callback != null) {
            log.info("------------->order response success:"+json.toString());
            callback.onSuccess(json.toString());
        }
    }
}
