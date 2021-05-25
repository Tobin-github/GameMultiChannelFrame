package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.uc.SinglePayCallbackResponse;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
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
import java.text.DecimalFormat;
import java.util.Date;


/***
 * UC单机渠道的支付回调请求处理
 */
@Controller
@Namespace("/pay/singleuc")
public class SingleUCPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleUCPayCallbackAction.class.getName());

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

            SinglePayCallbackResponse rsp = (SinglePayCallbackResponse) JsonUtils.decodeJson(sb.toString(), SinglePayCallbackResponse.class);

            if(rsp == null){
                this.renderState(false);
                return;
            }

            long orderID = Long.parseLong(rsp.getData().getOrderId());
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

            DecimalFormat df=new DecimalFormat("0.00");
            String orderMoney =df.format(order.getMoney()/100.00);
            if (!orderMoney.equals(rsp.getData().getAmount())) {
                log.error("---------------------> The order meney eror.order money:"+orderMoney+", channel money:"+rsp.getData().getAmount());
                this.renderState(false);
            }

            UGame game = order.getGame();

            if (null == game) {
                log.error("---------------------> The order game is null.");
                this.renderState(false);
            }

            if(!isValid(rsp.getData(),order.getChannel().getCpAppKey(),rsp.getSign())){
                log.error("---------------------> The sign is not matched.");
                this.renderState(false);
                return;
            }

            if("S".equals(rsp.getData().getOrderStatus())){
                float money = Float.parseFloat(rsp.getData().getAmount());
                int moneyInt = (int)(money * 100);  //以分为单位

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(rsp.getData().getOrderId());
                order.setState(PayState.STATE_SUC);

                log.info("---------------------> The callback is success.");
                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);
                renderState(true);
            }else{
                log.info("---------------------> The callback is not success.");
                order.setChannelOrderID(rsp.getData().getOrderId());
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                renderState(false);
            }

        }catch (Exception e){
            e.printStackTrace();
            try{
                this.renderState(false);
            }catch (Exception e2){
                e2.printStackTrace();
                Log.e(e2.getMessage());
            }

            log.error("------------>"+e.getMessage());

        }

    }

    private boolean isValid(SinglePayCallbackResponse.PayCallbackResponseData data,String appKey,String channelSign){
        StringBuilder sb = new StringBuilder();
        sb.append("amount").append("=").append(data.getAmount()).
            append("attachInfo").append("=").append(data.getAttachInfo()).
            append("failedDesc").append("=").append(data.getFailedDesc()).
            append("gameId").append("=").append(data.getGameId()).
            append("orderId").append("=").append(data.getOrderId()).
            append("orderStatus").append("=").append(data.getOrderStatus()).
            append("payType").append("=").append(data.getPayType()).
            append("tradeId").append("=").append(data.getTradeId()).
            append("tradeTime").append("=").append(data.getTradeTime()).
            append(appKey);

        log.info("---------------------> The unSignStr:"+sb.toString());
        String sign = EncryptUtils.md5(sb.toString());
        log.info("---------------------> The Sign:"+sign+", channelSign:"+channelSign);
        return sign.equals(channelSign);
    }

    private void renderState(boolean suc) throws IOException{

        String res = "SUCCESS";
        if(!suc){
            res = "FAILURE";
        }
        log.info("---------------------> The callback renderState:"+res);
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
