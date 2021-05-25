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
 * iOS渠道同步支付回调类
 * Created by ant on 2016/2/24.
 */

@Controller
@Namespace("/pay/tongbu")
public class TongbuPayCallbackAction extends UActionSupport{

    private String source;
    private String trade_no;
    private String amount;
    private String partner;
    private String paydes;
    private String debug;
    private String tborder;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{


            long orderID = Long.parseLong(trade_no);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                Log.d("The order is null or the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if(order.getMoney() > Integer.valueOf(this.amount)){
                Log.e("money not match. realMoney:"+this.amount+"; order money:"+order.getMoney());
                this.renderState(false);
                return;
            }

            if(isValid(order.getChannel())){

                order.setState(PayState.STATE_SUC);
                order.setCompleteTime(new Date());
                order.setRealMoney(Integer.valueOf(amount));
                order.setChannelOrderID(tborder);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true);
            }else{
                order.setState(PayState.STATE_FAILED);
                order.setChannelOrderID(tborder);
                orderManager.saveOrder(order);
                this.renderState(false);
            }


        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }


    private boolean isValid(UChannel channel) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("source=").append(source).append("&")
                .append("trade_no=").append(trade_no).append("&")
                .append("amount=").append(amount).append("&")
                .append("partner=").append(partner).append("&")
                .append("paydes=").append(paydes).append("&")
                .append("debug=").append(debug).append("&")
                .append("tborder=").append(tborder)
                .append("&key=").append(channel.getCpAppKey());

        Log.d("the data str to verify is "+sb.toString());

        return EncryptUtils.md5(sb.toString()).toLowerCase().equals(this.sign);

    }

    private void renderState(boolean suc) throws IOException {

        if(suc){
            JSONObject json = new JSONObject();
            json.put("status", "success");
            renderJson(json.toString());
        }

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPaydes() {
        return paydes;
    }

    public void setPaydes(String paydes) {
        this.paydes = paydes;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getTborder() {
        return tborder;
    }

    public void setTborder(String tborder) {
        this.tborder = tborder;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
