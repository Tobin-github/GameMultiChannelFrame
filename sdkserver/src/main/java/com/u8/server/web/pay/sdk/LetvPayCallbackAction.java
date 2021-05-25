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
 * 乐视SDK支付回调处理类
 * Created by ant on 2016/1/21.
 */

@Controller
@Namespace("/pay/letv")
public class LetvPayCallbackAction extends UActionSupport{

    private String app_id;                      //应用所属 AppId  String  N  乐视平台分配的应用参数
    private String lepay_order_no;              //乐视支付平台流水号  String  N  由乐视支付平台生成
    private String letv_user_id;                //乐视用户 id  String  N  user_id
    private String out_trade_no;                //支付 sdk 订单号  String  N  与 cp 订单号对应
    private String pay_time;                    //支付时间  string  N
    private String price;                       //金额  float  N  支付金额
    private String product_id;                  //商品 id  string  N  cp 方商品 id
    private String sign;                        // 签名  string  N  注意 null 要转换为  ””，
    private String sign_type;                   //签名类型  string  N  默认 MD5
    private String trade_result;                //交易结果  string  N  取值“TRADE_SUCCESS”
    private String version;                     //版本号  string  N  版本号：1.0
    private String cooperator_order_no;         // cp 订单号  string  N  cp 方支付订单号
    private String extra_info ;                 //cp 自定义信息  string  N  cp 自定义参数

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(cooperator_order_no);

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
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), cooperator_order_no);
                this.renderState(false);
                return;
            }

            if("TRADE_SUCCESS".equals(this.trade_result)){

                int moneyInt = (int)(Float.valueOf(price) * 100);  //以分为单位

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(pay_time);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(lepay_order_no);
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                order.setChannelOrderID(lepay_order_no);
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
        sb.append("app_id=").append(app_id)
                .append("&lepay_order_no=").append(lepay_order_no)
                .append("&letv_user_id=").append(letv_user_id)
                .append("&out_trade_no=").append(out_trade_no)
                .append("&pay_time=").append(pay_time)
                .append("&price=").append(price)
                .append("&product_id=").append(product_id)
                .append("&sign_type=").append(sign_type)
                .append("&trade_result=").append(trade_result)
                .append("&version=").append(version)
                .append("&key=").append(channel.getCpAppSecret());

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


    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getLepay_order_no() {
        return lepay_order_no;
    }

    public void setLepay_order_no(String lepay_order_no) {
        this.lepay_order_no = lepay_order_no;
    }

    public String getLetv_user_id() {
        return letv_user_id;
    }

    public void setLetv_user_id(String letv_user_id) {
        this.letv_user_id = letv_user_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getTrade_result() {
        return trade_result;
    }

    public void setTrade_result(String trade_result) {
        this.trade_result = trade_result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCooperator_order_no() {
        return cooperator_order_no;
    }

    public void setCooperator_order_no(String cooperator_order_no) {
        this.cooperator_order_no = cooperator_order_no;
    }

    public String getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(String extra_info) {
        this.extra_info = extra_info;
    }
}
