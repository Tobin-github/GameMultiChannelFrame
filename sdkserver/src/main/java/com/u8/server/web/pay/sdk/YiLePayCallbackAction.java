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
 * 都玩（易乐）支付回调处理类
 * Created by ant on 2016/9/1.
 */
@Controller
@Namespace("/pay/yile")
public class YiLePayCallbackAction extends UActionSupport {

    private int appid;
    private String orderid;
    private String cporderid;
    private int account;
    private String money;
    private String cpinfo;
    private String sign;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(cporderid);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), cporderid);
                this.renderState(false);
                return;
            }

            int moneyInt = (int)(Float.valueOf(money) * 100f);

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderid);
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
        sb.append("account=").append(account).append("&")
          .append("appid=").append(appid).append("&")
          .append("cpinfo=").append(cpinfo).append("&")
          .append("cporderid=").append(cporderid).append("&")
          .append("money=").append(money).append("&")
          .append("orderid=").append(orderid).append(channel.getCpAppSecret());


        Log.d("the str to sign is ");
        Log.d(sb.toString());

        String signLocal = EncryptUtils.md5(sb.toString());

        return signLocal.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        if(suc){
            renderText("success");
        }else{
            renderText("failed");
        }
    }


    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getCporderid() {
        return cporderid;
    }

    public void setCporderid(String cporderid) {
        this.cporderid = cporderid;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCpinfo() {
        return cpinfo;
    }

    public void setCpinfo(String cpinfo) {
        this.cpinfo = cpinfo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
