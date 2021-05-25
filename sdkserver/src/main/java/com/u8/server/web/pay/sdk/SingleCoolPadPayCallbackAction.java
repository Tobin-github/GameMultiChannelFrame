package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.coolpad.CoolPadPayResult;
import com.u8.server.sdk.coolpad.api.CpTransSyncSignValid;
import com.u8.server.service.UChannelManager;
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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 酷派支付回调
 * Created by ant on 2015/9/15.
 */
@Controller
@Namespace("/pay/singlecoolpad")
public class SingleCoolPadPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleCoolPadPayCallbackAction.class.getName());

    private String transdata;
    private String sign;
    private String signtype;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            Enumeration pNames = request.getParameterNames();
            Map<String, String> params = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                params.put(name, value);
                log.debug("------>params.name:" + name + ", value:" + value);
            }

            CoolPadPayResult result = (CoolPadPayResult) JsonUtils.decodeJson(this.transdata, CoolPadPayResult.class);

            if(result == null){
                log.error("--------------->The CoolPadPayResult is null,transdata:"+this.transdata);
                this.renderState(false);
                return;
            }

            log.info("--------------->transdata:"+transdata);

            String orderId = result.getExorderno();
            UOrder order = orderManager.getOrder(Long.parseLong(orderId));

            if(order == null || order.getChannel() == null){
                log.error("--------------->The UOrder is not exists. orderId:"+orderId);
                this.renderState(false);
                return;
            }

            log.info("--------------->order:"+order.toJSON());

            UChannel channel = order.getChannel();

            if (channel == null) {
                log.error("--------------->The UChannel is not exists. order:"+order.toJSON());
                this.renderState(false);
                return;
            }

            if(!isSignOK(channel)){
                log.error("--------------->The sign verify failed.sign:"+sign+",appKey:"+channel.getCpAppSecret()+", transdata:"+transdata);
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                log.error("--------------->The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if("0".equals(result.getResult())){
                log.info("--------------->ready send to game");

                int moneyInt = Integer.valueOf(result.getMoney());

                if (moneyInt != order.getMoney()) {
                    log.error("------------->order money error,money:" + order.getMoney()+", channelMoney:"+moneyInt);
                    this.renderState(false);
                    return;
                }

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(result.getTranstime());
                order.setCompleteTime(new Date());
                order.setChannelOrderID(result.getTransid());
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                log.error("--------------->result.getResult() isn't 0");
                order.setChannelOrderID(result.getTransid());
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }


        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                log.error("--------------->exception-1:"+e.getMessage());
                e1.printStackTrace();
            }
            log.error("--------------->exception-2:"+e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel){

        return CpTransSyncSignValid.validSign(transdata, this.sign, channel.getCpAppSecret());
    }

    private void renderState(boolean suc) throws IOException {

        String r = "SUCCESS";
        if(!suc){
            r = "FAILURE";
        }

        PrintWriter out = this.response.getWriter();
        out.write(r);
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

    public String getSigntype() {
        return signtype;
    }

    public void setSigntype(String signtype) {
        this.signtype = signtype;
    }

}
