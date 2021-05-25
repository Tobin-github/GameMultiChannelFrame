package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.sdk.ysdk.api.OpensnsException;
import com.u8.server.sdk.ysdk.api.SnsSigCheck;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 仅限YSDK道具直购模式，游戏币托管模式，请看YSDKNewPayAction。
 * 道具直购模式，支付成功应用宝会通知该接口进行发货。
 * Created by ant on 2017/5/10.
 */

@Controller
@Namespace("/pay/singleysdknew")
public class SingleYSDKPayCallbackAction extends UActionSupport {

    private static Logger logger = Logger.getLogger(SingleYSDKPayCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;


    @Action("payCallback")
    public void payCallback() {

        try {
            logger.debug("YSDKPayCallbackAction--------->kris");
            String zylCallbackuUrl = "http://10.104.246.142:9001/Third/Notify/MidasPay";

            Enumeration pNames = request.getParameterNames();
            Map<String, String> params = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                params.put(name, value);
                logger.debug("YSDKPayCallbackAction params.name:" + name + ", value:" + value);
            }

            for (String key :
                    params.keySet()) {
                String value = params.get(key);

                if (value.startsWith("AO")) {
                    logger.debug("---------------->order start with AO,zylCallbackuUrl:" + zylCallbackuUrl + ", params:" + params.toString());
                    sendToZYLGame(zylCallbackuUrl, params);
                    return;
                } else if (value.startsWith("BO")) {
                    logger.debug("---------------->order start with BO,zylCallbackuUrl:" + zylCallbackuUrl + ", params:" + params.toString());
                    sendToZYLGame(zylCallbackuUrl, params);
                    return;
                }
            }


            if (!params.containsKey("appmeta")) {
                logger.debug("the cee_extend param is not in params of sdk callback.");
                this.renderState(false);
                return;
            }

            String p = params.get("appmeta");
            String[] splits = p.split("\\*");
            String url = this.request.getRequestURI();

            long orderID = Long.parseLong(splits[0]);
            String amt = params.get("amt");
            String billno = params.get("billno");
            UOrder order = orderManager.getOrder(orderID);
            logger.debug("YSDKPayCallbackAction The order is " + order.toJSON() + ", amt=" + amt);
            if (order == null) {
                logger.debug("YSDKPayCallbackAction The order is null ,orderId:." + orderID);
                this.renderState(false);
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                logger.debug("YSDKPayCallbackAction The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }


            UChannel channel = order.getChannel();
            logger.debug("YSDKPayCallbackAction The UChannel :" + channel.toJSON());
            if (channel == null) {
                logger.error("YSDKPayCallbackAction pay callback error. channel is not exists:" + order.getChannelID());
                renderState(false);
                return;
            }

            if (!isSignOK(channel, params, url)) {
                logger.error("YSDKPayCallbackAction pay callback error. sign not matched.");
                renderState(false);
                return;
            }

            int moneyInt = Float.valueOf(amt).intValue();       //amt 0.1Q点 --》对应人民币1分
            if(order.getMoney() != moneyInt){
                logger.error(" 订单金额不一致! local orderID:"+orderID+"; money returned:"+moneyInt+"; order money:"+order.getMoney());
                this.renderState(false);
                return;
            }

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID(billno);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);


            renderState(true);


        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderState(false);
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.error(e2.getMessage());
            }

            logger.error(e.getMessage());

        }

    }

    public void sendToZYLGame(String callbackUrl, Map params) throws IOException {

        String serverRes = UHttpAgent.getInstance().post(callbackUrl, params);

        logger.debug("---------------->callback to game success,result:" + serverRes);

        JSONObject json = JSONObject.fromObject(serverRes);

        int ret = json.getInt("ret");
        if (0 == ret) {
            renderState(true);
        } else {
            renderState(false);
        }

        UHttpAgent.getInstance().post(callbackUrl, params, new UHttpFutureCallback() {
            @Override
            public void completed(String result) {
                try {
                    logger.debug("---------------->callback to game success,result:" + result);

                    JSONObject json = JSONObject.fromObject(result);

                    int ret = json.getInt("ret");
                    if (0 == ret) {
                        renderState(true);
                    } else {
                        renderState(false);
                    }
                } catch (IOException e) {
                    logger.debug("---------------->callback to game,exceptionMsg:" + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(String e) {

                try {
                    logger.debug("---------------->callback to game fail,result:" + e);
                    renderState(false);
                } catch (IOException e1) {
                    logger.debug("---------------->callback to game,exceptionMsg2:" + e1.getMessage());
                    e1.printStackTrace();
                }
            }

        });


    }

    private boolean isSignOK(UChannel channel, Map<String, String> params, String path) throws OpensnsException {

        //String sourceStr = StringUtils.generateUrlSortedParamString(params, "&", false, new String[]{"sig","cee_extend"});

        params.remove("u8ChannelID");
        String sig = params.remove("sig");

        for (String key : params.keySet()) {

            String val = params.get(key);
            val = encodeValue(val);
            params.put(key, val);
            logger.debug("sign key-pair. key:" + key + "value:" + val);
        }

        String method = "GET";
//        String path = getUrlPath(channel);
//        path = "/pay/ysdknew/payCallback";
//        String secret = channel.getCpPayID()+"&";
        String secret = channel.getCpPayPriKey() + "&";
//        secret = "bZifCQRIdhp66zMnOdiT6ksfYC6dI5bF&";//沙箱做测试用key
        logger.debug("the secret key :" + secret + ", path:" + path + ", method:" + method);

        String sigLocal = SnsSigCheck.makeSig(method, path, params, secret);

        return sigLocal.equals(sig);


    }

    private void renderState(boolean suc) throws IOException {

        JSONObject json = new JSONObject();
        json.put("ret", suc ? 0 : 1);
        json.put("msg", suc ? "OK" : "FAIL");

        renderJson(json.toString());
    }


    private static String getUrlPath(UChannel channel) {

        //String url = "http://localhost:8080/pay/ysdk/payCallback/10";
        String url = channel.getPayCallbackUrl();

        logger.debug("the pay callback url:" + url);

        String[] splits = url.substring("http://".length()).split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            if (i > 0) {
                sb.append("/").append(splits[i]);
            }
        }

        return sb.toString();

    }

    /**
     * 应用发货URL接口的编码规则
     *
     * @param s
     * @return
     */
    public static String encodeValue(String s) {
        String rexp = "[0-9a-zA-Z!*()]";
        StringBuffer sb = new StringBuffer(s);
        StringBuffer sbRtn = new StringBuffer();
        Pattern p = Pattern.compile(rexp);
        char temp;
        String tempStr;

        for (int i = 0; i < sb.length(); i++) {
            temp = sb.charAt(i);
            tempStr = String.valueOf(temp);
            Matcher m = p.matcher(tempStr);

            boolean result = m.find();
            if (!result) {
                tempStr = hexString(tempStr);
            }
            sbRtn.append(tempStr);
        }

        return sbRtn.toString();
    }

    /**
     * 应用发货URL　十六进制编码
     *
     * @param s
     * @return
     */
    private static String hexString(String s) {
        byte[] b = s.getBytes();
        String retStr = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            retStr = "%" + hex.toUpperCase();
        }
        return retStr;
    }

//    public static void main(String[] args) throws OpensnsException {
//        String sourceStr = "http://ip/pay/mt.php?amt=320&appid=1101255891&appmeta=customkey*qdqb*qq&billno=-APPDJSX18246-20140401-\n" +
//                "1206311492&clientver=android&openid=F11669C63D76BAB0BC2F6CC869B19E53&payamt_coins=0\n" +
//                "&payitem=G1*20*2&providetype=5&pubacct_payamt_coins=&token=5056117C0597793C38C4F1D29F884C5E25887\n" +
//                "&ts=1396325191&version=v3&zoneid=1&sig=ai1eD5CA16n5pWBx9abjZguMR5Y%3D";
//
//        String appkey = "Lf6AtMEB1QlE8BYS";
//
//        Map<String,String> params = new HashMap<>();
//        params.put("amt", "320");
//        params.put("appid", "1101255891");
//        params.put("appmeta", "customkey*qdqb*qq");
//        params.put("billno", "-APPDJSX18246-20140401-1206311492");
//        params.put("clientver", "android");
//        params.put("pubacct_payamt_coins", "");
//        params.put("openid", "F11669C63D76BAB0BC2F6CC869B19E53");
//        params.put("payamt_coins", "0");
//        params.put("payitem", "G1*20*2");
//        params.put("providetype", "5");
//        params.put("token", "5056117C0597793C38C4F1D29F884C5E25887");
//        params.put("ts", "1396325191");
//        params.put("version", "v3");
//        params.put("zoneid", "1");
//
//
//
//        for(String key : params.keySet()){
//
//            String val = params.get(key);
//            val = encodeValue(val);
//            params.put(key, val);
//            logger.debug("sign key-pair. key:%s;value:%s", key, val);
//        }
//
//        String method = "GET";
//        String path = "/pay/mt.php";
//        String secret = "Lf6AtMEB1QlE8BYS&";
//
//        logger.debug("the secret key :%s", secret);
//
//        String sigLocal = SnsSigCheck.makeSig(method, path, params, secret);
//
//        logger.debug(sigLocal);
//
//
//    }

}
