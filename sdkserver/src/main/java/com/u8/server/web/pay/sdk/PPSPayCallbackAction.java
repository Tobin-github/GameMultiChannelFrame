package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

/**
 * 爱奇异（PPS）支付回调处理
 * Created by ant on 2016/4/20.
 */

@Controller
@Namespace("/pay/iqiyi")
public class PPSPayCallbackAction extends UActionSupport {


    private String user_id;
    private String role_id;
    private String order_id;
    private String money;
    private String time;
    private String userData;
    private String sign;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(userData);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpAppSecret(), userData);
                this.renderState(false);
                return;
            }

            int moneyInt = (int)(Float.valueOf(money) * 100);

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(this.order_id);
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
        sb.append(user_id).append(role_id)
                .append(order_id).append(money).append(time)
                .append(channel.getCpAppSecret());

        Log.d("sign txt:"+sb.toString());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        JSONObject json = new JSONObject();

        if(suc){

            json.put("result", "0");
            json.put("message", "支付成功");
        }else{

            json.put("result", "-6");
            json.put("message", "支付失败");
        }

        renderJson(json.toString());
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
