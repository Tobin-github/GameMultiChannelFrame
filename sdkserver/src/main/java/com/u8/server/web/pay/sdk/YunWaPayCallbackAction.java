package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
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

/**
 * 云娃SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/yunwa")
public class YunWaPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(YunWaPayCallbackAction.class.getName());

    private String userId;
    private String goodsId;
    private String goodsName;
    private String payOrderId;
    private String payPrice;
    private String payStatus;
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
                log.debug("-------> params.name:" + name + ", value:" + value);
            }

            long orderId = Long.parseLong(goodsId);

            UOrder order = orderManager.getOrder(orderId);

            int resultCode = 1;
            String resultMsg = "成功";

            if(order == null || order.getChannel() == null){
                log.error("--------------->The order is null or the channel is null.");
                this.renderState(false,"The order is null or the channel is null.");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("--------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true,"the order is complete. The state is "+order.getState());
                return;
            }

            log.info("--------------->The order:"+order.toJSON());

            if(!payPrice.equals(order.getMoney()+"")){
                log.error("-------------->订单金额不一致! local orderID:"+goodsId+"; channel money :"+payPrice+", order money:"+order.getMoney());
                this.renderState(false, "该订单金额不一致");
                return;
            }

            if(!"1".equals(payStatus)){
                log.error("-------------->云娃支付失败 local orderID:"+goodsId+", status:" + payStatus);
                this.renderState(false, "该订单状态失败，status："+payStatus);
                return;
            }


            if(isValid(order.getChannel())){
                log.info("-------------->云娃平台sign验证成功...");
                order.setChannelOrderID(payOrderId);
                order.setRealMoney(Integer.parseInt(payStatus));
                order.setSdkOrderTime(System.currentTimeMillis()/1000+"");
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.error("-------------->云娃平台sign验证失败:sign:"+this.sign+";key:"+order.getChannel().getCpAppSecret());
                order.setChannelOrderID(payOrderId);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }
        }catch (Exception e){
            log.error("--------------->payBack exception,msg:"+e.getMessage());
            e.printStackTrace();
        }

    }

    private boolean isValid(UChannel channel){

        StringBuilder sb = new StringBuilder();

        sb.append("goodsId=").append(goodsId).append("&").
                append("goodsName=").append(goodsName).append("&").
                append("payOrderId=").append(payOrderId).append("&").
                append("payPrice=").append(payPrice).append("&").
                append("payStatus=").append(payStatus).append("&").
                append("userId=").append(userId).append("&").
                append("appKey=").append(channel.getCpAppKey());


        String mySign = EncryptUtils.md5(sb.toString());

        log.info("-------------->mySign:"+mySign+", channelSign:"+sign+", Param's str:"+sb.toString());

        if(mySign.equals(this.sign)){
            return true;
        }

        return false;
    }

    private void renderState(boolean isSuccess,String resultMsg) throws IOException {

        String result = "fail";

        if (isSuccess) {
            result = "0";
        }

        log.info("---------------->The result to sdk is "+result+",msg:"+resultMsg);

        PrintWriter out = this.response.getWriter();
        out.write(result);
        out.flush();

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
