package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * CYPlatformSDK支付回调处理类
 * Created by ant on 2016/5/10.
 * http://localhost:8080/pay/sougou/payCallback
 */
@Controller
@Namespace("/pay/cyPlatform")
public class CYPlatformPayCallbackAction extends UActionSupport {


    private String amount;  //用户充值金额（人民币元）
    private String appid;  //SDK平台的游戏ID
    private String charid;  //角色ID
    private String cporderid;  //游戏传过来的orderid
    private String extinfo;  //游戏传过来的callbackInfo字段的值
    private String gold;  //游戏币数量
    private String orderid;  //SDK平台的订单ID
    private String serverid;  //服务器ID
    private String time;  //时间戳
    private String uid;  //用户ID
    private String sign;  //签名

    @Autowired
    private UOrderManager orderManager;

    private static Logger logger = Logger.getLogger(CYPlatformPayCallbackAction.class.getName());

    @Action("payCallback")
    public void payCallback() {
        try {

            Enumeration pNames = request.getParameterNames();
            Map<String, String> params = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                params.put(name, value);
                logger.debug("---------------->CYPlatformPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long localOrderID = Long.parseLong(cporderid);

            UOrder order = orderManager.getOrder(localOrderID);

            if (order == null || order.getChannel() == null) {
                logger.error("------------->The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            logger.info("------------->callback order:"+order.toJSON());

            if (order.getState() > PayState.STATE_PAYING) {
                logger.error("------------->The state of the order is complete. The state is " + order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            float realMoney = Float.parseFloat(amount) * 100;


            DecimalFormat df=new DecimalFormat("0");
            String orderMoney =df.format(realMoney);
            //logger.info("------------->callback request's param,amount:"+amount+", appid:"+appid+", charid:"+charid+", cporderid:"+cporderid+", extinfo:"+extinfo+", gold:"+gold+", orderid:"+orderid+", serverid:"+serverid+", time:"+time+", uid:"+uid+", sign:"+sign);

            if (!order.getMoney().toString().equals(orderMoney)) {
                logger.error("------------->订单金额不一致! local orderID:" + localOrderID + "; money returned:" + realMoney + "; order money:" + order.getMoney());
                this.renderState(false, "SouGouSDK 订单金额与支付金额不一致");
                return;
            }

            String Pay_Key = order.getChannel().getCpAppSecret();

            String code = String.format("amount=%s&appid=%s&charid=%s&cporderid=%s&extinfo=%s&gold=%s&orderid=%s&serverid=%s&time=%s&uid=%s%s",
                    GetEncode(amount), GetEncode(appid), GetEncode(charid), GetEncode(cporderid),
                    GetEncode(extinfo), GetEncode(gold), GetEncode(orderid), GetEncode(serverid),
                    time, GetEncode(uid), Pay_Key);

            String md5 = getMD5(code);
            logger.info("------------->callback generateSign:" + md5);

            //签名失败
            if (!md5.equals(sign)) {
                renderState(false, "sign ERROR");
                return;
            }

            //处理签名成功
            order.setRealMoney(Integer.parseInt(orderMoney));
            order.setSdkOrderTime(time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderid);
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);
            logger.info("------------->callback signed successful");
            SendAgent.sendCallbackToServer(this.orderManager, order);
            this.renderState(true, "");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void renderState(boolean suc, String msg) throws IOException {

        PrintWriter out = this.response.getWriter();
        if (suc) {
            out.write("SUCCESS");
        } else {
            out.write("ERROR");
        }
        out.flush();
    }

    protected String GetEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
        }
        return "";
    }

    private static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {

        }

        return "";
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getCharid() {
        return charid;
    }

    public void setCharid(String charid) {
        this.charid = charid;
    }

    public String getCporderid() {
        return cporderid;
    }

    public void setCporderid(String cporderid) {
        this.cporderid = cporderid;
    }

    public String getExtinfo() {
        return extinfo;
    }

    public void setExtinfo(String extinfo) {
        this.extinfo = extinfo;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
