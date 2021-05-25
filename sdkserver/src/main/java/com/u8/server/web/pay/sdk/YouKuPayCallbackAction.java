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
 * 优酷SDK支付回调处理类
 * Created by ant on 2016/1/18.
 */
@Controller
@Namespace("/pay/youku")
public class YouKuPayCallbackAction extends UActionSupport{

    private String apporderID;      //CP 订单号
    private String uid;
    private String price;           //分
    private String sign;            //签名
    private String passthrough;     //透传
    private String result;          //如果是断代支付，才有该字段。网游不开断代支付
    private String success_amount;  //成功支付金额，断代支付独有

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(apporderID);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), apporderID);
                this.renderState(false);
                return;
            }

            int moneyInt = Integer.valueOf(price);

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID("");
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

        String paycallbackUrl = channel.getPayCallbackUrl();
        StringBuilder sb = new StringBuilder();

        sb.append(paycallbackUrl).append("?apporderID=")
                .append(apporderID).append("&price=").append(price)
                .append("&uid=").append(uid);

        String signLocal = EncryptUtils.hmac(sb.toString(), channel.getCpPayID());

        return signLocal.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        JSONObject json = new JSONObject();
        if(suc){
            json.put("status", "success");
            json.put("desc", "通知成功");
        }else{
            json.put("status", "failed");
            json.put("desc", "CP处理失败");
        }

        renderJson(json.toString());
    }


    public String getApporderID() {
        return apporderID;
    }

    public void setApporderID(String apporderID) {
        this.apporderID = apporderID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPassthrough() {
        return passthrough;
    }

    public void setPassthrough(String passthrough) {
        this.passthrough = passthrough;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSuccess_amount() {
        return success_amount;
    }

    public void setSuccess_amount(String success_amount) {
        this.success_amount = success_amount;
    }
}
