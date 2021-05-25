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
 * 盛大（逗逗SDK）支付回调处理类
 * Created by ant on 2016/1/19.
 */
@Controller
@Namespace("/pay/doudou")
public class DouDouPayCallbackAction extends UActionSupport {

    private String orderNo;
    private String userId;
    private String gameOrderNo;
    private String product;         //后台商品ID，自由支付，该字段为空格
    private String extend;
    private String sign;
    private String time;

    //支付支付方式才有如下两个字段
    private String itemName;
    private String money;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(gameOrderNo);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpAppSecret(), gameOrderNo);
                this.renderState(false);
                return;
            }

            if(product == null || product.trim().length() == 0){
                //自由支付
                int moneyInt = (int)(Float.valueOf(money) * 100);  //以分为单位

                order.setRealMoney(moneyInt);
            }


            order.setSdkOrderTime(time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderNo);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);

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

        sb.append("extend=").append(extend)
                .append("&gameOrderNo=").append(gameOrderNo);

        if(product == null || product.trim().length() == 0){
            //自由支付
            sb.append("&itemName=").append(itemName)
                    .append("&money=").append(money);
        }

        sb.append("&orderNo=").append(orderNo)
                .append("&product=").append(product)
                .append("&time=").append(time)
                .append("&userId=").append(userId)
                .append(channel.getCpAppSecret());


        Log.d("sign txt:"+sb.toString());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "fail";
        }

        renderText(res);
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGameOrderNo() {
        return gameOrderNo;
    }

    public void setGameOrderNo(String gameOrderNo) {
        this.gameOrderNo = gameOrderNo;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
