package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.uc.PayCallbackResponse;
import com.u8.server.sdk.uc.UCSDK;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.JsonUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


/***
 * UC渠道的支付回调请求处理
 */
@Controller
@Namespace("/pay/uc")
public class UCPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(UCPayCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;

    private int u8ChannelID;

    @Action("payCallback")
    public void payCallback(){

        try{

            BufferedReader br = this.request.getReader();
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine()) != null){
                sb.append(line).append("\r\n");
            }

            log.info("---------------------> UC Pay Callback . request params:" + sb.toString());

            PayCallbackResponse rsp = (PayCallbackResponse) JsonUtils.decodeJson(sb.toString(), PayCallbackResponse.class);

            if(rsp == null){
                this.renderState(false);
                return;
            }

            long orderID = Long.parseLong(rsp.getData().getCallbackInfo());
            UOrder order = orderManager.getOrder(orderID);
            log.info("---------------------> The order:"+order.toJSON());

            if(order == null || order.getChannel() == null){
                log.error("---------------------> The order is null or the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("---------------------> The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }


            UCSDK sdk = new UCSDK();

            if(!sdk.verifyPay(order.getChannel(), rsp)){
                log.error("---------------------> The sign is not matched.");
                this.renderState(true);
                return;
            }

            if("S".equals(rsp.getData().getOrderStatus())){
                float money = Float.parseFloat(rsp.getData().getAmount());
                int moneyInt = (int)(money * 100);  //以分为单位

                if (moneyInt != order.getMoney()) {
                    log.error("---------------------> The order money not matched,money:"+order.getMoney()+", channelMoney:"+moneyInt);
                    this.renderState(true);
                    return;
                }

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(rsp.getData().getOrderId());
                order.setState(PayState.STATE_SUC);

                log.info("---------------------> The callback is success.");
                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);


            }else{
                log.error("---------------------> The callback is not success.");
                order.setChannelOrderID(rsp.getData().getOrderId());
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }


            renderState(true);

        }catch (Exception e){
            e.printStackTrace();
            try{
                this.renderState(false);
            }catch (Exception e2){
                e2.printStackTrace();
                Log.e(e2.getMessage());
            }

            Log.e(e.getMessage());

        }

    }

    private void renderState(boolean suc) throws IOException{

        String res = "SUCCESS";
        if(!suc){
            res = "FAILURE";
        }
        log.info("----------------------> The callback renderState:"+res);
        PrintWriter out = this.response.getWriter();
        out.write(res);
        out.flush();
    }

    public int getU8ChannelID() {
        return u8ChannelID;
    }

    public void setU8ChannelID(int u8ChannelID) {
        this.u8ChannelID = u8ChannelID;
    }
}
