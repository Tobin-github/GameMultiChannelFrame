package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.dianyou.CpaUtil;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 点游支付回调地址
 * Created by ant on 2015/4/24.
 */

@Controller
@Namespace("/pay/dianyou")
public class DianYouPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(DianYouPayCallbackAction.class.getName());

    private String app		    ;					//游戏标识
    private String cbi		    ;					//在客户端由商户应用指定的业务参数
    private String ct	        ;					//支付完成 UTC 时间戳（毫秒）
    private String fee	        ;					//金额（分）
    private String pt	        ;					//付费时间，订单创建服务器UTC 时间戳（毫秒）
    private String sdk	        ;					//sdk 版本
    private String ssid			;					//客户端传递的 cpOrderId 订单流水号
    private String st		    ;					//是否成功标志，1 标示成功，其余都表示失败
    private String tcd			;					//订单在点游服务器上的订单号
    private String uid          ;					//付费用户在点游服务器里唯一标记
    private String ver	        ;					//协议版本号，目前为“1”
    private String sign	        ;					//上述内容的数字签名，方法在下文会说明

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(ssid);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                log.debug("The order is null or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.debug("The state of the order is complete. The state is " + order.getState());
                this.renderState(false, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if (!fee.equals(order.getMoney() + "")) {
                log.debug("the meney is not equal,orderMeney:" + order.getMoney()+", fee:"+fee);
                this.renderState(false, "下单金额与渠道支付比对失败");
                return;
            }

            log.debug("--------->payCallback ,st:"+st);

            if("1".equals(st)){
                if(isValid(order.getChannel())){
                    order.setRealMoney(Integer.valueOf(fee));
                    order.setSdkOrderTime(pt);
                    order.setCompleteTime(new Date());
                    order.setChannelOrderID(tcd);
                    order.setState(PayState.STATE_SUC);
                    orderManager.saveOrder(order);
                    log.debug("--------->payCallback ,send to game,data:" + order.toJSON());
                    SendAgent.sendCallbackToServer(this.orderManager, order);
                    this.renderState(true, "");
                    return;
                }else{
                    log.error("--------->payCallback fail,msg:sign 错误,order:" + order.toJSON());
                    order.setChannelOrderID(tcd);
                    order.setState(PayState.STATE_FAILED);
                    orderManager.saveOrder(order);
                    this.renderState(false, "sign 错误");
                    return;
                }
            }else{
                log.error("--------->payCallback fail,msg:支付失败,order:"+order.toJSON());
                this.renderState(false, "支付失败");
                return;
            }

        }catch (Exception e){
            log.error("--------->payCallback exception,msg:未知错误");
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
                return;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(UChannel channel){

        Map params=new HashMap<>();
        params.put("app", app);
        params.put("cbi", cbi);
        params.put("ct", ct);
        params.put("fee", fee);
        params.put("pt", pt);
        params.put("sdk", sdk);
        params.put("ssid", ssid);
        params.put("st", st);
        params.put("tcd", tcd);
        params.put("uid", uid);
        params.put("ver", ver);
        params.put("sign", sign);

        String secretKey = channel.getCpConfig();

        log.debug("--------->unSignStr:"+params.toString()+",secretKey:"+secretKey);

        try {
            return CpaUtil.verify(params, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void renderState(boolean suc, String msg) throws IOException {

        log.debug("The result to sdk is " + msg);
        PrintWriter out = this.response.getWriter();

        if(suc){
            out.write("SUCCESS");
        }else{
            out.write("FAIL");
        }

        out.flush();

    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getCbi() {
        return cbi;
    }

    public void setCbi(String cbi) {
        this.cbi = cbi;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getTcd() {
        return tcd;
    }

    public void setTcd(String tcd) {
        this.tcd = tcd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
