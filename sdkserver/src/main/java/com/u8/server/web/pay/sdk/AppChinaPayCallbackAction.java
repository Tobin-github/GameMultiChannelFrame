package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.RSAUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 应用汇支付回调通知接口
 * Created by ant on 2015/4/28.
 */
@Controller
@Namespace("/pay/appchina")
public class AppChinaPayCallbackAction extends UActionSupport{


    private String transdata;
    private String sign;
    private String signtype;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            AppChinaPayContent data = (AppChinaPayContent)JsonUtils.decodeJson(transdata, AppChinaPayContent.class);

            if(data == null){
                Log.e("The content parse error...");
                return;
            }

            long localOrderID = Long.parseLong(data.getCporderid());

            UOrder order = orderManager.getOrder(localOrderID);

            if(order == null || order.getChannel() == null){
                Log.d("The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                Log.d("The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if(!"0".equals(data.getResult())){
                Log.e("平台支付失败 local orderID:"+localOrderID+"; order id:" + data.getTransid());
                this.renderState(false, "支付中心返回的结果是支付失败");
                return;
            }

            if(isValid(order.getChannel())){
                order.setChannelOrderID(data.getTransid());
                order.setRealMoney((int)(Float.valueOf(data.getMoney()) * 100));
                order.setSdkOrderTime(data.getTranstime());
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                order.setChannelOrderID(data.getTransid());
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }

        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel){

        //return CpTransSyncSignValid.validSign(this.transdata, this.sign, channel.getCpPayKey());
        return RSAUtils.verify(this.transdata, this.sign, channel.getCpPayKey(), "UTF-8");

    }

    private void renderState(boolean suc, String msg) throws IOException {

        PrintWriter out = this.response.getWriter();

        if(suc){
            out.write("SUCCESS");
        }else{
            out.write("FAILURE");
        }
        out.flush();
    }

    public static class AppChinaPayContent{

        private String transtype;
        private String cporderid;
        private String transid;
        private String appuserid;
        private String appid;
        private String waresid;
        private String feetype;
        private String money;
        private String currency;
        private String result;
        private String transtime;
        private String cppprivate;
        private String paytype;


        public String getTranstype() {
            return transtype;
        }

        public void setTranstype(String transtype) {
            this.transtype = transtype;
        }

        public String getCporderid() {
            return cporderid;
        }

        public void setCporderid(String cporderid) {
            this.cporderid = cporderid;
        }

        public String getTransid() {
            return transid;
        }

        public void setTransid(String transid) {
            this.transid = transid;
        }

        public String getAppuserid() {
            return appuserid;
        }

        public void setAppuserid(String appuserid) {
            this.appuserid = appuserid;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getWaresid() {
            return waresid;
        }

        public void setWaresid(String waresid) {
            this.waresid = waresid;
        }

        public String getFeetype() {
            return feetype;
        }

        public void setFeetype(String feetype) {
            this.feetype = feetype;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTranstime() {
            return transtime;
        }

        public void setTranstime(String transtime) {
            this.transtime = transtime;
        }

        public String getCppprivate() {
            return cppprivate;
        }

        public void setCppprivate(String cppprivate) {
            this.cppprivate = cppprivate;
        }

        public String getPaytype() {
            return paytype;
        }

        public void setPaytype(String paytype) {
            this.paytype = paytype;
        }
    }


    public String getTransdata() {
        return transdata;
    }

    public void setTransdata(String transdata) {
        this.transdata = transdata;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSigntype() {
        return signtype;
    }

    public void setSigntype(String signtype) {
        this.signtype = signtype;
    }
}
