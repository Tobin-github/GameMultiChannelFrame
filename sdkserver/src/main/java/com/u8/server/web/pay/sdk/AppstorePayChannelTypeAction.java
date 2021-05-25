package com.u8.server.web.pay.sdk;

import com.opensymphony.xwork2.ActionContext;
import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.USwitch;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.sdk.ali.RSA;
import com.u8.server.service.*;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by xzy on 15/12/21.
 */
@Controller
@Namespace("/pay/apple")
public class AppstorePayChannelTypeAction extends UActionSupport {

    private static Logger log = Logger.getLogger(AppstorePayChannelTypeAction.class.getName());


    private static final String payViewUrl = "http://120.25.243.133:8080/pay/apple/showPayView";
    public static final String IAPGetOrderUrl = "http://120.25.243.133:8080/pay/apple/getOrderID";
    private static final String wxExtensionUrl = "http://120.25.243.133:8080/pay/apple/sdkGetOrder";
    public static final String AliReturnUrl = "http://120.25.243.133:8080/return.html";
    public static final String notify_url = "http://120.25.243.133:8080/pay/apple/payCallback";
    public static final String AliNotifyUrl = "http://120.25.243.133:8080/pay/alipay/payCallback";

    /*private static final String payViewUrl = "http://www.wgb.cn:9080/pay/apple/showPayView";
    public static final String IAPGetOrderUrl = "http://www.wgb.cn:9080/pay/apple/getOrderID";
    private static final String wxExtensionUrl = "http://www.wgb.cn:9080/pay/apple/sdkGetOrder";
    public static final String notify_url = "http://www.wgb.cn:9088/pay/apple/payCallback";
    public static final String AliReturnUrl = "http://www.wgb.cn:9088/return.html";
    public static final String AliNotifyUrl = "http://www.wgb.cn:9088/pay/alipay/payCallback";*/


    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private USwitchManager switchManager;

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UChannelPayTypeManager payTypeManagerManage;

    @Autowired
    private UPayTypeManager payTypeManage;

    private String appId;
    private String channelId;
    private String sdkUserId;
    private String orderId;
    private String spdata;


    @Action("payChannelType")
    public void payChannelType() {
        try {

            Map<String, String[]> requestParams = request.getParameterMap();
            String jsonStr = "";
            for (Iterator<String> iter =    requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                jsonStr = name + valueStr;
            }

            if (StringUtils.isEmpty(jsonStr)) {
                log.error("------------->payChannelType, ios jsonStr is null..., jsonStr:" + jsonStr);
                renderError("jsonStr不存在...");
                return;
            }

            //IOS传的appId and channelId
            JSONObject json = JSONObject.fromObject(jsonStr);
            int appId = json.getInt("AppId");
            int channelID = json.getInt("Channel");


            log.info("------------->payChannelType, ios order:" + json.toString());

            // 得到切换配置项
            USwitch uSwitch = switchManager.getSwitchByIndex(appId, channelID);

            if (null == uSwitch) {
                renderError("未配置切换");
                return;
            }

            log.info("------------->payChannelType, USwitch:" + uSwitch.toJSON());

            JSONObject json2 = new JSONObject();
            json2.put("switchType", uSwitch.getSwitchType());

            if (0 == uSwitch.getStatus()) {//不切换支付
                json2.put("payViewUrl", IAPGetOrderUrl);
            } else if (3==uSwitch.getSwitchType()) {//其他渠道公用内购下单
                json2.put("payViewUrl", IAPGetOrderUrl);
            } else {
                json2.put("payViewUrl", payViewUrl);
            }

            json2.put("callBackUrl", notify_url);

            if (3 == uSwitch.getSwitchType()) {//其他渠道callBackUrl
                UChannel uChannel = channelManager.queryChannel(channelID);
                String payCallbackUrl = uChannel.getMaster().getPayCallbackUrl();
                json2.put("callBackUrl", payCallbackUrl);
            }

            log.info("------------->payChannelType, ios order json:" + json.toString());
            log.info("------------->payChannelType, ios order json2:" + json2.toString());

            renderResponse(uSwitch.getStatus(),json2);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderError("未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * IOS内购下单
     * @return
     */
    @Action("getOrderID")
    public void appStoreGetOrderID() {
        try {
            log.info("------------->appStoreGetOrderID enter");
            Map<String, String[]> requestParams = request.getParameterMap();
            String jsonStr = "";
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                jsonStr = name + valueStr;
            }

            if (StringUtils.isEmpty(jsonStr)) {
                log.error("------------->appStoreGetOrderID, ios jsonStr is null..., jsonStr:" + jsonStr);
                renderError("jsonStr不存在...");
            }
            log.info("------------->appStoreGetOrderID, ios getOrder json:" + jsonStr.toString());

            //ios下单传的值
            JSONObject json = JSONObject.fromObject(jsonStr);
            String productName = json.getString("productName");
            String productDesc = json.getString("productDesc");
            String productId = json.getString("productId");
            int price = json.getInt("price");
            int buyNum = json.getInt("buyNum");
            String roleId = json.getString("roleId");
            String roleName = json.getString("roleName");
            String extension = json.getString("extension");
            String serverId = json.getString("serverId");
            String serverName = json.getString("serverName");
            String zylSdkUserID = json.getString("zylSdkUserID");
            int zylAppID = json.getInt("zylAppID");
            int zylChannelID = json.getInt("zylChannelID");

            if (StringUtils.isEmpty(zylAppID+"") || StringUtils.isEmpty(zylChannelID+"") || StringUtils.isEmpty(zylSdkUserID)) {
                log.error("------------->appStoreGetOrderID, ios jsonStr is null..., jsonStr:" + jsonStr);
                renderError("appID或channelID或sdkUserID不存在...");
            }

            UUser user = userManager.getUserByCpID(zylAppID, zylChannelID, zylSdkUserID);

            if(user == null){
                log.error("------------->appStoreGetOrderID,the user is not found. ");
                renderError("user不存在...");
            }

            if (null == user.getChannel()) {
                log.error("------------->appStoreGetOrderID,the channel is not found. ");
                renderError("channel...");
            }

            if(!user.getChannel().isPayOpen()){
                log.error("------------>the pay not opened in u8server manage system.. ");
                renderError("该渠道不开启支付");
                return;
            }

            log.info("------------->appStoreGetOrderID, ios getOrder user:" + user.toJSON());

            final UOrder order = orderManager.generateOrder(user, price*buyNum, productId, productName, productDesc, roleId,roleName,serverId,serverName, extension, "");

            if (null == order) {
                renderError("订单生成失败...");
            }



            order.setPayType(1);

            int checkPayType = payTypeManage.checkPayType(order);

            if (checkPayType != -1) {
                order.setPayType(checkPayType);
            }

            order.setPlatID(user.getChannel().getPlatID());
            orderManager.saveOrder(order);

            JSONObject repJson = new JSONObject();
            repJson.put("orderId", order.getOrderID());
            repJson.put("extension", order.getExtension());

            log.info("------------->appStoreGetOrderID, ios order json:" + json.toString());

            renderResponse(1,repJson);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderError("未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * IOS内购支付切换至Ali、微信的下单
     * @return
     */
    @Action(value = "showPayView",
            results = {@Result(name = "success", location = "/WEB-INF/pay/iosPay.jsp")})
    public String showAppStorePayView() {
        try {
            log.info("------------->showAppStorePayView enter");
            Map<String, String[]> requestParams = request.getParameterMap();
            String jsonStr = "";
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                jsonStr = name + valueStr;
            }

            if (StringUtils.isEmpty(jsonStr)) {
                log.error("------------->showAppStorePayView, ios jsonStr is null..., jsonStr:" + jsonStr);
                renderError("jsonStr不存在...");
                return "";
            }
            log.info("------------->showAppStorePayView, ios getOrder json:" + jsonStr.toString());

            //ios下单传的值
            JSONObject json = JSONObject.fromObject(jsonStr);
            String orderID = json.getString("orderID");
            String productName = json.getString("productName");
            String productDesc = json.getString("productDesc");
            String productId = json.getString("productId");
            int price = json.getInt("price");
            int buyNum = json.getInt("buyNum");
            String coinNum = json.getString("coinNum");
            String roleId = json.getString("roleId");
            String roleName = json.getString("roleName");
            String extension = json.getString("extension");
            String roleLevel = json.getString("roleLevel");
            String serverId = json.getString("serverId");
            String serverName = json.getString("serverName");
            String vip = json.getString("vip");
            String zylUserID = json.getString("zylUserID");
            String zylUsername = json.getString("zylUsername");
            String zylExtention = json.getString("zylExtention");
            String zylSdkUserID = json.getString("zylSdkUserID");
            int zylAppID = json.getInt("zylAppID");
            int zylChannelID = json.getInt("zylChannelID");

            if (StringUtils.isEmpty(zylAppID+"") || StringUtils.isEmpty(zylChannelID+"") || StringUtils.isEmpty(zylSdkUserID)) {
                log.error("------------->showAppStorePayView, ios jsonStr is null..., jsonStr:" + jsonStr);
                renderError("appID或channelID或sdkUserID不存在...");
                return "";
            }

            UUser user = userManager.getUserByCpID(zylAppID, zylChannelID, zylSdkUserID);

            if(user == null){
                log.error("------------->the user is not found. ");
                renderError("user不存在...");
                return "";
            }

            if(!user.getChannel().isPayOpen()){
                log.error("------------>the pay not opened in u8server manage system.. ");
                renderError("该渠道不开启支付");
                return "";
            }

            log.info("------------->showAppStorePayView, ios getOrder user:" + user.toJSON());

            final UOrder order = orderManager.generateOrder(user, price*buyNum, productId, productName, productDesc, roleId,roleName,serverId,serverName, extension, "");

            ActionContext.getContext().put("appId", zylAppID);
            ActionContext.getContext().put("channelId", zylChannelID);
            ActionContext.getContext().put("sdkUserId", zylSdkUserID);
            ActionContext.getContext().put("orderId", order.getOrderID());
            ActionContext.getContext().put("wxExtensionUrl", wxExtensionUrl);


            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderError("未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return "";
    }

    @Action("sdkGetOrder")
    public void sdkGetOrder() {
        try {
            int zylAppId=Integer.parseInt(appId);
            int zylChannelId=Integer.parseInt(channelId);
            log.info("------------->sdkGetOrder,appId:" + zylAppId + ", channelId:" + zylChannelId);

            UUser user = userManager.getUserByCpID(zylAppId, zylChannelId, sdkUserId);
            log.info("------------->sdkGetOrder,appId:" + zylAppId + ", channelId:" + zylChannelId+", sdkUserId:"+sdkUserId+",orderId:"+orderId+" user:"+user.toJSON());

            if (null == user) {
                log.error("------------->sdkGetOrder,the user is not found. appId:" + appId + ", channelId:" + channelId + ", sdkuserId:" + sdkUserId);
                renderError("sdkGetOrder user不存在...");
                return;
            }

            UChannel channel = user.getChannel();
            log.info("------------->sdkGetOrder,channel:"+channel.toJSON());

            if (null == channel) {
                log.error("------------->sdkGetOrder,the channel is not found. user:" + user.toJSON());
                renderError("sdkGetOrder channel不存在...");
                return;
            }
            long zylOrderId = Long.parseLong(orderId);

            UOrder order = orderManager.getOrder(zylOrderId);
            log.info("------------->sdkGetOrder,order:"+order.toJSON());

            if (null == order) {
                log.error("------------->sdkGetOrder,the order is not found. orderId:" + orderId);
                renderError("sdkGetOrder order不存在...");
                return;
            }

            ISDKScript script = SDKCacheManager.getInstance().getSDKScript(order.getChannel());
            log.info("------------->sdkGetOrder,order:"+order.toJSON());

            if (script == null) {
                log.error("------------->sdkGetOrder,the ISDKScript is not found. channelID:" + order.getChannelID());
                renderError("sdkGetOrder script不存在...");
                return;
            }

            if (this.spdata != null) {
                ISDKScriptExt scriptExt = (ISDKScriptExt) script;
                int payType = Integer.parseInt(this.spdata);
                log.info("------------->sdkGetOrder,payType:"+payType);

                order.setPayType(payType);
                order.setPlatID(channel.getPlatID());

                //设置多渠道支付方式
                int checkPayType = payTypeManage.checkPayType(order);

                if (checkPayType != -1) {
                    order.setPayType(checkPayType);
                }

                orderManager.saveOrder(order);

                int finalPayType = payType;
                scriptExt.onGetOrderIDByType(user, order, payType, payTypeManagerManage, new ISDKOrderListener() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        log.info("------------->sdkGetOrder,The onGetOrderID extension is " + jsonStr);

                        //Ali
                        if (2== finalPayType) {
                            String orderId=order.getOrderID()+"";
                            log.info("------------->sdkGetOrder,Ali's orderId:" + orderId);
                            //newEchargeByZfb(order.getOrderID());
                            echargeByZfb(order.getOrderID());


                        }else if (3 == finalPayType) {
                            JSONObject data = new JSONObject();
                            data.put("orderID", order.getOrderID());
                            data.put("extension", jsonStr);

                            log.info("------------->sdkGetOrder,WX's data is " + data.toString());

                            renderState(StateCode.CODE_SUCCESS, data);
                        }

                    }

                    @Override
                    public void onFailed(String err) {
                        log.error(err);

                        JSONObject data = new JSONObject();
                        data.put("orderID", order.getOrderID());
                        data.put("extension", "");
                        renderState(StateCode.CODE_ORDER_ERROR, data);
                    }
                });
                return;

            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderError("未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void echargeByZfb(long orderId) {
        try {

            UOrder order = orderManager.getOrder(orderId);
            log.info("------------->echargeByZfb,Ali's order:" + order.toJSON());
            if (null==order) {
                log.error("------------->echargeByZfb,Ali charge fail,order is null");
                return;
            }

            String appID = order.getChannel().getCpAppID();
            String privateKey = order.getChannel().getCpPayPriKey();

            if (order.getChannel().getPlatType() == 1) {
                JSONObject appIDObj = JSONObject.fromObject("{" + appID + "}");
                JSONObject priKeyObj = JSONObject.fromObject("{" + privateKey + "}");
                appID = appIDObj.getString("Ali");
                privateKey = priKeyObj.getString("Ali");
                log.info("------------->echargeByZfb,IOS多渠道支付,appId:"+appID+", privateKey:"+privateKey);
            }
            log.info("------------->echargeByZfb,appId:" + appID+", privateKey:"+privateKey+", notify_url:"+notify_url+", return_url:"+AliReturnUrl);

            DecimalFormat df=new DecimalFormat("0.00");
            String orderAmount =df.format(order.getMoney()/100.00);

            Map<String,String> params = new HashMap<String, String>();
            params.put("service", "alipay.wap.create.direct.pay.by.user");
            params.put("partner", appID);
            params.put("_input_charset", "UTF-8");
            params.put("sign_type", "RSA");
            params.put("return_url", AliReturnUrl);
            params.put("notify_url", AliNotifyUrl);
            params.put("out_trade_no", order.getOrderID()+"");
            params.put("subject", order.getProductName());
            params.put("total_fee", orderAmount);
            //params.put("total_fee", "0.01");
            params.put("seller_id", appID);
            params.put("payment_type", "1");
            params.put("show_url", "http://www.baidu.com");

            log.info("------------->echargeByZfb,params:" + params.toString());

            //拼接签名参数
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("service", "alipay.wap.create.direct.pay.by.user");
            parameters.put("partner", appID);
            parameters.put("_input_charset", "UTF-8");
            parameters.put("return_url", AliReturnUrl);
            parameters.put("notify_url", AliNotifyUrl);
            parameters.put("out_trade_no", order.getOrderID()+"");
            parameters.put("subject", order.getProductName());
            parameters.put("total_fee", orderAmount);
            //parameters.put("total_fee", "0.01");
            parameters.put("seller_id", appID);
            parameters.put("payment_type", "1");
            parameters.put("show_url", "http://www.baidu.com");
            log.info("------------->echargeByZfb,signParameters:" + parameters.toString());

            String signStr = generateSign(parameters);
            signStr = RSA.sign(signStr, privateKey, "UTF-8");
            params.put("sign", signStr);
            log.info("------------->echargeByZfb,Ali's sign:" + signStr+", RSA's sign:"+signStr);

            UHttpAgent.getInstance().get("https://mapi.alipay.com/gateway.do", params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        log.info("------------->echargeByZfb,The ios alipay success:" + result);
                        HttpServletResponse localHttpServletResponse = ServletActionContext.getResponse();
                        if (localHttpServletResponse != null) {
                            localHttpServletResponse.setContentType("text/html;charset=" + "utf-8");
                            localHttpServletResponse.getWriter().write(result);//直接将完整的表单html输出到页面
                            localHttpServletResponse.getWriter().flush();
                            localHttpServletResponse.getWriter().close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("------------->echargeByZfb,The ios alipay exception:" + e.getMessage());
                    }

                }

                @Override
                public void failed(String e) {
                    log.error("------------->echargeByZfb,ios Ali charge fail");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("------------->echargeByZfb,Ali charge Exception:" + e.getMessage());
        }
    }

    private String generateSign(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k).append("=").append(v).append("&");
        }
        String signStr=sb.substring(0,sb.length()-1);

        return signStr;
    }

    private void renderState(int state, JSONObject data){
        JSONObject json = new JSONObject();
        json.put("state", state);
        json.put("data", data);
        super.renderJson(json.toString());
    }

    private void renderResponse(int state,JSONObject data) throws IOException {
        try {

            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("data", data);

            super.renderJson(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        }
    }

    private void renderError(String msg) throws IOException {
        try {

            JSONObject json = new JSONObject();
            json.put("state", 0);
            json.put("data", msg);

            super.renderJson(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        }
    }

    public String getSpdata() {
        return spdata;
    }

    public void setSpdata(String spdata) {
        this.spdata = spdata;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSdkUserId() {
        return sdkUserId;
    }

    public void setSdkUserId(String sdkUserId) {
        this.sdkUserId = sdkUserId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
