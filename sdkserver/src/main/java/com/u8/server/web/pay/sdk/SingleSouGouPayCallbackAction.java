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
import java.net.URLEncoder;
import java.util.Date;

/**
 * 搜狗SDK支付回调处理类
 * Created by ant on 2016/5/10.
 * http://localhost:8080/pay/sougou/payCallback
 */
@Controller
@Namespace("/pay/singlesougou")
public class SingleSouGouPayCallbackAction extends UActionSupport {

    private String gid        ;  //game id 由平台分配的游戏编号
    private String sid        ;  //server id 由平台分配的游戏区服编号
    private String uid        ;  //user id 平台的用户id
    private String role       ;  //若游戏需充值到角色，传角色名。默认会传空
    private String oid        ;  //订单号，同一订单有可能多次发送通知
    private String date       ;  //订单创建日期，格式为yyMMdd
    private String amount1    ;  //用户充值金额（人民币元）
    private String amount2    ;  //金额（游戏币数量）（手游忽略此参数，但校验时需要传递）
    private String time       ;  //此时间并不是订单的产生或支付时间，而是通知发送的时间，也即当前时间
    private String appdata    ;  //透传参数（可无），若需要须向平台方申请开启此功能，默认开启
    private String realAmount ;  //用户充值真实金额（人民币元）
    private String auth       ;  //验证字符串, 生成方式同auth token, 区别是在第三步, 附加支付秘钥而不是app secret


    @Autowired
    private UOrderManager orderManager;

    private static Logger logger = Logger.getLogger(SingleSouGouPayCallbackAction.class.getName());

    @Action("payCallback")
    public void payCallback(){
        try{


            long localOrderID = Long.parseLong(appdata);

            UOrder order = orderManager.getOrder(localOrderID);

            if(order == null || order.getChannel() == null){
                logger.debug("--------------->The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                logger.debug("--------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }


            int realMoney = Integer.valueOf(this.realAmount) * 100;      //转换为分


            if(order.getMoney() != realMoney){
                logger.error("--------------->订单金额不一致! local orderID:"+localOrderID+"; money returned:"+realMoney+"; order money:"+order.getMoney());
                this.renderState(false, "SouGouSDK 订单金额与支付金额不一致");
                return ;
            }

            if(isValid(order.getChannel())){

                order.setRealMoney(realMoney);
                order.setSdkOrderTime(date);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(oid);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                logger.error("--------------->SouGouSDK callback signed successful");
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                order.setChannelOrderID(oid);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "auth 错误");
            }

        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("amount1=").append(amount1).append("&")
                .append("amount2=").append(amount2).append("&")
                .append("appdata=").append(URLEncoder.encode(appdata, "UTF-8")).append("&")
                .append("date=").append(URLEncoder.encode(date, "UTF-8")).append("&")
                .append("gid=").append(gid).append("&")
                .append("oid=").append(URLEncoder.encode(oid, "UTF-8")).append("&")
                .append("realAmount=").append(realAmount).append("&")
                .append("role=").append(URLEncoder.encode(role, "UTF-8")).append("&")
                .append("sid=").append(sid).append("&")
                .append("time=").append(time).append("&")
                .append("uid=").append(uid).append("&").append(channel.getCpPayID());
        logger.debug("--------------->SouGouSDK callback sign str is "+sb.toString());
        String signLocal = EncryptUtils.md5(sb.toString()).toLowerCase();
        logger.debug("--------------->SouGouSDK callback signed str is "+signLocal);
        return signLocal.equals(this.auth);

    }

    private void renderState(boolean suc, String msg) throws IOException {

        PrintWriter out = this.response.getWriter();
        if(suc){
            out.write("OK");
        }else{
            out.write("ERR_500");
        }
        out.flush();
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount1() {
        return amount1;
    }

    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    public String getAmount2() {
        return amount2;
    }

    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAppdata() {
        return appdata;
    }

    public void setAppdata(String appdata) {
        this.appdata = appdata;
    }

    public String getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(String realAmount) {
        this.realAmount = realAmount;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
