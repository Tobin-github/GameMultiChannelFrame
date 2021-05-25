package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.qihoo360.Util;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 360支付回调
 * Created by ant on 2015/2/28.
 */
@Controller
@Namespace("/pay/singleqihoo360")
public class SingleQihooPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleQihooPayCallbackAction.class.getName());

    private String app_key;
    private String product_id;
    private int amount;
    private String app_uid;
    private String app_ext1;
    private String app_ext2;
    private long user_id;
    private long order_id;
    private String gateway_flag;
    private String sign_type;   //md5
    private String app_order_id;
    private String sign_return;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        log.info("------------->sign's param,app_key:"+app_key+
                ", product_id:"+this.product_id+
                ", amount:"+this.amount+
                ", app_uid:"+this.app_uid+
                ", app_ext1:"+this.app_ext1+
                ", app_ext2:"+this.app_ext2+
                ", user_id:"+this.user_id+
                ", order_id:"+this.order_id+
                ", gateway_flag:"+this.gateway_flag+
                ", sign_type:"+this.sign_type+
                ", app_order_id:"+this.app_order_id+
                ", sign_return:"+this.sign_return+
                ", sign:"+this.sign);
        try{

            long orderID = Long.parseLong(app_order_id);

            UOrder order = orderManager.getOrder(orderID);

            log.info("------------->order:"+order.toJSON());

            if(order == null || order.getChannel() == null){
                log.error("------------->The order is null or the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("-------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true);
                return;
            }

            if (order.getMoney() != amount) {
                log.error("-------------->The money error "+order.toJSON());
                this.renderState(true);
                return ;
            }

            UChannel channel = order.getChannel();
            String cpAppKey = channel.getCpAppKey();
            String cpAppSecret = channel.getCpAppSecret();
            log.info("------------->cpAppKey:"+cpAppKey+", cpAppSecret:"+cpAppSecret);

            Map<String, String[]> paramterMap = request.getParameterMap();
            HashMap params = new HashMap<String, String>();
            String k, v;
            Iterator<String> iterator = paramterMap.keySet().iterator();
            while (iterator.hasNext()) {
                k = iterator.next();
                String arr[] = paramterMap.get(k);
                v = (String) arr[0];
                params.put(k, v);
            }

            String mySign = Util.getSign(params, cpAppSecret);

            if (mySign.equals(this.sign)) {
                log.info("------------->sign ok ok ok ok ok");
            }

            if("success".equals(gateway_flag) && cpAppKey.equals(app_key)){
                log.info("------------->sign success");
                order.setRealMoney(this.amount);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(this.order_id + "");
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true);
            }else{
                log.error("------------->sign error");
                order.setChannelOrderID(this.order_id + "");
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(true);
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("------------->exception msg:"+e.getMessage());
        }

    }


    /**
     * 过期
     * @param channel
     * @return
     */
    private boolean isValid(UChannel channel){


        StringBuilder sb = new StringBuilder();
        if(amount > 0){
            sb.append(amount).append("#");
        }

        if(!StringUtils.isEmpty(app_ext1)){
            sb.append(app_ext1).append("#");
        }

        if(!StringUtils.isEmpty(app_ext2)){
            sb.append(app_ext2).append("#");
        }

        if(!StringUtils.isEmpty(app_key)){
            sb.append(app_key).append("#");
        }

        if(!StringUtils.isEmpty(app_order_id)){
            sb.append(app_order_id).append("#");
        }

        if(!StringUtils.isEmpty(app_uid)){
            sb.append(app_uid).append("#");
        }

        if(!StringUtils.isEmpty(gateway_flag)){
            sb.append(gateway_flag).append("#");
        }

        if(order_id > 0){
            sb.append(order_id).append("#");
        }

        if(!StringUtils.isEmpty(product_id)){
            sb.append(product_id).append("#");
        }

        if(!StringUtils.isEmpty(sign_type)){
            sb.append(sign_type).append("#");
        }

        if(user_id > 0){
            sb.append(user_id).append("#");
        }

        sb.append(channel.getCpAppSecret());

        log.info("------------------->The 360 pay params str for sign is "+sb.toString() + " the sign returned is "+this.sign);
        String sign = EncryptUtils.md5(sb.toString());
        log.info("------------->sign:"+sign+", channelSign:"+this.sign);
        return sign.toLowerCase().equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        String res = "ok";
        if(suc){
            PrintWriter out = this.response.getWriter();
            out.write(res);
            out.flush();
        }


    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getApp_uid() {
        return app_uid;
    }

    public void setApp_uid(String app_uid) {
        this.app_uid = app_uid;
    }

    public String getApp_ext1() {
        return app_ext1;
    }

    public void setApp_ext1(String app_ext1) {
        this.app_ext1 = app_ext1;
    }

    public String getApp_ext2() {
        return app_ext2;
    }

    public void setApp_ext2(String app_ext2) {
        this.app_ext2 = app_ext2;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getGateway_flag() {
        return gateway_flag;
    }

    public void setGateway_flag(String gateway_flag) {
        this.gateway_flag = gateway_flag;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getApp_order_id() {
        return app_order_id;
    }

    public void setApp_order_id(String app_order_id) {
        this.app_order_id = app_order_id;
    }

    public String getSign_return() {
        return sign_return;
    }

    public void setSign_return(String sign_return) {
        this.sign_return = sign_return;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
