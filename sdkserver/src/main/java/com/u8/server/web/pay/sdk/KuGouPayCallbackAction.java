package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

/**
 * 酷狗SDK支付回调处理
 * Created by ant on 2016/1/18.
 */
@Controller
@Namespace("/pay/kugou")
public class KuGouPayCallbackAction extends UActionSupport{

    private String orderid;     //平台订单号（联运平台唯一订单号）
    private String outorderid;  //游戏厂商订单号(游戏厂商需保证订单唯一性) (回调时url编码)
    private Integer amount;      //充值金额(人民币)，以此值兑换游戏币。单位（元）
    private String username;    //平台帐号(回调时url编码)
    private Integer status;      //是否扣费成功.1:成功,0:不成功
    private String time;        //发起请求时间，Unix时间戳
    private String ext1;        //扩展字段1,原样传回(回调时url编码)
    private String ext2;        //扩展字段2,原样传回(回调时url编码)
    private String sign;        //md5后的结果小写md5(orderid+outorderid+amount+username+status+time+ext1+ext2+key)

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(outorderid);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null");
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if(channel == null){
                Log.d("the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if(!isSignOK(channel)){
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), outorderid);
                this.renderState(false);
                return;
            }

            if(this.status == 1){

                int moneyInt = amount * 100;  //以分为单位

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(time);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(orderid);
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                order.setChannelOrderID(orderid);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }


            renderState(true);

        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel){
        StringBuilder sb = new StringBuilder();
        sb.append(orderid).append(outorderid)
                .append(amount).append(username)
                .append(status).append(time)
                .append(ext1).append(ext2)
                .append(channel.getCpAppSecret());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String res = "SUCCESS";
        if(!suc){
            res = "FAIL";
        }

        renderText(res);
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOutorderid() {
        return outorderid;
    }

    public void setOutorderid(String outorderid) {
        this.outorderid = outorderid;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
