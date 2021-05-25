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
import java.net.URLEncoder;
import java.util.Date;

/**
 * 乐8 支付回调
 * Created by ant on 2016/7/6.
 */
@Controller
@Namespace("/pay/le8")
public class Le8PayCallbackAction extends UActionSupport {

    private String n_time;
    private String appid;
    private String o_id;
    private String t_fee;
    private String g_name;
    private String g_body;
    private String t_status;
    private String o_sign;
    private String u_param;
    private String o_orderid;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{


            long orderID = Long.parseLong(o_id);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", this.o_sign, channel.getCpAppSecret(), o_id);
                this.renderState(false);
                return;
            }

            int moneyInt = (int)(Float.valueOf(t_fee) * 100);  //以分为单位

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(n_time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(o_orderid);
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

        try{

            StringBuilder sb = new StringBuilder();
            sb.append("n_time=").append(URLEncoder.encode(n_time, "UTF-8")).append("&")
                    .append("appid=").append(URLEncoder.encode(appid, "UTF-8")).append("&")
                    .append("o_id=").append(URLEncoder.encode(o_id, "UTF-8")).append("&")
                    .append("t_fee=").append(URLEncoder.encode(t_fee, "UTF-8")).append("&")
                    .append("g_name=").append(URLEncoder.encode(g_name, "UTF-8")).append("&")
                    .append("g_body=").append(URLEncoder.encode(g_body, "UTF-8")).append("&")
                    .append("t_status=").append(URLEncoder.encode(t_status, "UTF-8"))
                    .append(channel.getCpAppSecret());

            Log.d("sign txt:"+sb.toString());

            String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

            Log.d("md5:"+md5);

            return md5.equals(this.o_sign);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(e.getMessage(), e);
        }

        return false;

    }

    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "fail";
        }

        renderText(res);
    }


    public String getN_time() {
        return n_time;
    }

    public void setN_time(String n_time) {
        this.n_time = n_time;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getT_fee() {
        return t_fee;
    }

    public void setT_fee(String t_fee) {
        this.t_fee = t_fee;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getG_body() {
        return g_body;
    }

    public void setG_body(String g_body) {
        this.g_body = g_body;
    }

    public String getT_status() {
        return t_status;
    }

    public void setT_status(String t_status) {
        this.t_status = t_status;
    }

    public String getO_sign() {
        return o_sign;
    }

    public void setO_sign(String o_sign) {
        this.o_sign = o_sign;
    }

    public String getU_param() {
        return u_param;
    }

    public void setU_param(String u_param) {
        this.u_param = u_param;
    }

    public String getO_orderid() {
        return o_orderid;
    }

    public void setO_orderid(String o_orderid) {
        this.o_orderid = o_orderid;
    }
}
