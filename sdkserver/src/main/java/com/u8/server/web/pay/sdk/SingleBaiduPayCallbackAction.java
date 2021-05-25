package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.U8OrderIDHexUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

/**
 * 百度SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/singlebaidu")
public class SingleBaiduPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleBaiduPayCallbackAction.class.getName());

    private String appid;
    private String orderid;
    private String amount;
    private String unit;
    private String jfd;
    private String status;
    private String paychannel;
    private String Phone;
    private String channel;
    private String from;
    private String sign;
    private String extchannel;
    private String cpdefinepart;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("SingleBaiduPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long orderId = U8OrderIDHexUtils.decode(cpdefinepart);

            UOrder order = orderManager.getOrder(orderId);

            int resultCode = 1;
            String resultMsg = "成功";

            if(order == null || order.getChannel() == null){
                log.error("--------------->The order is null or the channel is null.");
                this.renderState(false,"The order is null or the channel is null.");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("--------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true,"the order is complete. The state is "+order.getState());
                return;
            }

            int orderMoney;

            if (unit.equals("fen")) {//转换为分
                orderMoney = (int) Float.parseFloat(amount);
            } else {
                orderMoney = (int) (Float.parseFloat(amount) * 100);
            }
            log.info("--------------->The order:"+order.toJSON());

            if(order.getMoney() != orderMoney){
                log.error("-------------->订单金额不一致! local orderID:"+cpdefinepart+"; money returned:"+amount+", order money:"+order.getMoney());
                this.renderState(false, "该订单金额不一致");
                return;
            }

            if(!"success".equals(status)){
                log.error("-------------->百度支付失败 local orderID:"+cpdefinepart+", status:" + status);
                this.renderState(false, "该订单状态失败，status："+status);
                return;
            }


            if(isValid(order.getChannel())){
                log.info("-------------->百度平台sign验证成功...");
                order.setChannelOrderID(orderid);
                order.setRealMoney(orderMoney);
                order.setSdkOrderTime(System.currentTimeMillis()/1000+"");
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.error("-------------->百度平台sign验证失败:sign:"+this.sign+";key:"+order.getChannel().getCpAppSecret());
                order.setChannelOrderID(orderid);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }
        }catch (Exception e){
            log.error("--------------->payBack exception,msg:"+e.getMessage());
            e.printStackTrace();
        }

    }

    private boolean isValid(UChannel channel){

        StringBuilder sb = new StringBuilder();

        sb.append(appid).
                append(orderid).
                append(amount).
                append(unit).
                append(status).
                append(paychannel).
                append(channel.getCpAppSecret());

        String mySign = EncryptUtils.md5(sb.toString());

        log.info("-------------->mySign:"+mySign+", channelSign:"+sign+", Param's str:"+sb.toString());

        if(mySign.equals(this.sign)){
            return true;
        }

        return false;
    }

    private void renderState(boolean isSuccess,String resultMsg) throws IOException {

        String result = "fail";

        if (isSuccess) {
            result = "success";
        }

        log.info("--------------->The result to sdk is "+result+",msg:"+resultMsg);

        PrintWriter out = this.response.getWriter();
        out.write(result);
        out.flush();

    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getJfd() {
        return jfd;
    }

    public void setJfd(String jfd) {
        this.jfd = jfd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaychannel() {
        return paychannel;
    }

    public void setPaychannel(String paychannel) {
        this.paychannel = paychannel;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getExtchannel() {
        return extchannel;
    }

    public void setExtchannel(String extchannel) {
        this.extchannel = extchannel;
    }

    public String getCpdefinepart() {
        return cpdefinepart;
    }

    public void setCpdefinepart(String cpdefinepart) {
        this.cpdefinepart = cpdefinepart;
    }
}
