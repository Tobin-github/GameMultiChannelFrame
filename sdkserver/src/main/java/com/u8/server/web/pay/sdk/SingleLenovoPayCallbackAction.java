package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.lenovo.SignHelper;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.JsonUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;

/**
 * 联想支付回调
 * Created by ant on 2015/4/27.
 */
@Controller
@Namespace("/pay/singlelenovo")
public class SingleLenovoPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleLenovoPayCallbackAction.class.getName());

    private String transdata;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{
            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("SingleLenovoPayCallbackAction params.name:" + name + ", value:" + value);
            }

            LenovoPayCallbackAction.LenovoPayContent data = (LenovoPayCallbackAction.LenovoPayContent) JsonUtils.decodeJson(transdata, LenovoPayCallbackAction.LenovoPayContent.class);

            if(data == null){
                log.error("------->The content parse error...");
                return;
            }

            long localOrderID = Long.parseLong(data.getCpprivate());

            UOrder order = orderManager.getOrder(localOrderID);

            if(order == null || order.getChannel() == null){
                log.error("------->The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if (!"0".equals(data.getResult())) {
                log.error("------->The order result error,state:"+data.getResult());
                this.renderState(false, "state 错误");
                return;
            }

            int orderMoney = (int) (Float.parseFloat(data.getMoney())*100);      //转换为分

            if(order.getMoney() != orderMoney){
                log.error("------->订单金额不一致! local orderID:"+localOrderID+"; money returned:"+data.getMoney()+"; order money:"+order.getMoney());
            }

            if(!"0".equals(data.getResult())){
                log.error("------->联想平台支付失败 local orderID:"+localOrderID+";lenovo order id:" + data.getTransid());
                this.renderState(false, "联想支付中心返回的结果是支付失败");
                return;
            }

            if(isValid(order.getChannel())){
                log.info("------->联想平台sign验证成功...");
                order.setChannelOrderID(data.getTransid());
                order.setRealMoney(orderMoney);
                order.setSdkOrderTime(data.getTranstime());
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.error("------->联想平台sign验证失败:sign:"+this.sign+";key:"+order.getChannel().getCpPayKey());
                order.setChannelOrderID(data.getTransid());
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }

        }catch (Exception e){
            log.error("------->Exception:"+e.getMessage());
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel){

        log.info("------->params:"+this.transdata+", sign:"+sign+", pubKey:"+channel.getCpPayKey());

        boolean verifyResult = SignHelper.verify(this.transdata, this.sign, channel.getCpPayKey());

        if (verifyResult) {
            return true;
        }

        try {
            if(SignHelper.verify(this.transdata, URLDecoder.decode(this.sign, "utf-8"), channel.getCpPayKey())){
                return  true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;

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

    public static class LenovoPayContent{

        private String exorderno    ;           //游戏订单号
        private String transid      ;           //联想支付平台的订单号
        private String appid  		;			//游戏 id  String  Max(20)    Open AppID
        private String waresid  	;			//商品编码  integer  Max(8)    商品编号
        private String feetype  	;			//计费方式  integer  Max(3)    计费方式：0、按次；1、开放价格；
        private String money  		;			//交易金额  integer  Max(10)    本次交易的金额，单位：分
        private String count  		;			//购买数量  integer  Max(10)    本次购买的商品数量
        private String result  		;			//交易结果  integer  Max(2)    交易结果：0– 交易成功；1–交易失败
        private String transtype  	;			//交易类型  integer  Max(2)    交易类型： 0 –交易； 1 –冲正
        private String transtime  	;			//交易时间  String  Max(20)    交易时间格式：yyyy-mm-dd hh24:mi:ss
        private String cpprivate  	;			//商户私有信息  String  Max(128)  商户私有信息
        private String paytype  	;			//支付方式  Integer  Max(2)  支付方式（该字段值后

        public String getExorderno() {
            return exorderno;
        }

        public void setExorderno(String exorderno) {
            this.exorderno = exorderno;
        }

        public String getTransid() {
            return transid;
        }

        public void setTransid(String transid) {
            this.transid = transid;
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

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTranstype() {
            return transtype;
        }

        public void setTranstype(String transtype) {
            this.transtype = transtype;
        }

        public String getTranstime() {
            return transtime;
        }

        public void setTranstime(String transtime) {
            this.transtime = transtime;
        }

        public String getCpprivate() {
            return cpprivate;
        }

        public void setCpprivate(String cpprivate) {
            this.cpprivate = cpprivate;
        }

        public String getPaytype() {
            return paytype;
        }

        public void setPaytype(String paytype) {
            this.paytype = paytype;
        }

        @Override
        public String toString() {
            return "LenovoPayContent{" +
                    "exorderno='" + exorderno + '\'' +
                    ", transid='" + transid + '\'' +
                    ", appid='" + appid + '\'' +
                    ", waresid='" + waresid + '\'' +
                    ", feetype='" + feetype + '\'' +
                    ", money='" + money + '\'' +
                    ", count='" + count + '\'' +
                    ", result='" + result + '\'' +
                    ", transtype='" + transtype + '\'' +
                    ", transtime='" + transtime + '\'' +
                    ", cpprivate='" + cpprivate + '\'' +
                    ", paytype='" + paytype + '\'' +
                    '}';
        }
    }

    /*private static Logger log = Logger.getLogger(SingleLenovoPayCallbackAction.class.getName());

    private String order_id;
    private String merchant_order_id;
    private String amount;
    private String app_id;
    private String pay_time;
    private String attach;
    private String status;
    private String sign;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("SingleLenovoPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long localOrderID = Long.parseLong(merchant_order_id);

            UOrder order = orderManager.getOrder(localOrderID);

            if(order == null || order.getChannel() == null){
                log.error("-------------->The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            log.info("----------------->order:"+order.toJSON());

            if(order.getState() > PayState.STATE_PAYING){
                log.error("-------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            int orderMoney = (int) (Float.parseFloat(amount)*100);      //转换为分

            if(order.getMoney() != orderMoney){
                log.error("-------------->订单金额不一致! local orderID:"+localOrderID+"; money returned:"+amount+"元; order money:"+order.getMoney());
                this.renderState(false, "该订单金额不一致");
                return;
            }

            if(!"1".equals(status)){
                log.error("-------------->联想平台支付失败 local orderID:"+localOrderID+";lenovo status:" + status);
                this.renderState(false, "联想支付中心返回的结果是支付失败");
                return;
            }

            if(isValid(order.getChannel())){
                log.info("-------------->联想平台sign验证成功...");
                order.setChannelOrderID(order_id);
                order.setRealMoney(orderMoney);
                order.setSdkOrderTime(pay_time);
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.error("-------------->联想平台sign验证失败:sign:"+this.sign+";key:"+order.getChannel().getCpPayKey());
                order.setChannelOrderID(order_id);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }

        }catch (Exception e){
            log.error("-------------->exception1:"+e.getMessage());
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                log.error("-------------->exception2:"+e.getMessage());
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel){

        Map<String,Object> paramsMap = new TreeMap<>();
        paramsMap.put("order_id",order_id);
        paramsMap.put("merchant_order_id",merchant_order_id);
        paramsMap.put("amount",amount);
        paramsMap.put("app_id",app_id);
        paramsMap.put("pay_time",pay_time);
        paramsMap.put("attach",attach);
        paramsMap.put("status",status);

        StringBuilder sb = new StringBuilder();

        for (Map.Entry param:
                paramsMap.entrySet()) {
            sb.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        sb.append("key").append("=").append(channel.getCpAppKey());

        String mySign = EncryptUtils.md5(sb.toString());

        log.info("-------------->mySign:"+mySign+", channelSign:"+sign+", Param's str:"+sb.toString());

        if(mySign.equals(this.sign)){
            return true;
        }

        return false;
    }


    private void renderState(boolean suc, String msg) throws IOException {

        PrintWriter out = this.response.getWriter();
        if(suc){
            out.write("success");
        }else{
            out.write("failure");
        }
        out.flush();

    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMerchant_order_id() {
        return merchant_order_id;
    }

    public void setMerchant_order_id(String merchant_order_id) {
        this.merchant_order_id = merchant_order_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }*/
}
