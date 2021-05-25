package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.vivo.VivoSignUtils;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * single VIVO支付回调地址
 * Created by ant on 2015/4/24.
 */

@Controller
@Namespace("/pay/singlevivo")
public class SingleVivoPayCallbackAction extends UActionSupport{

    private static Logger logger = Logger.getLogger(SingleVivoPayCallbackAction.class.getName());

    private String respCode		;					//响应码	200
    private String respMsg		;					//响应信息	交易完成
    private String signMethod	;					//签名方法	对关键信息进行签名的算法名称：MD5
    private String signature	;					//签名信息	对关键信息签名后得到的字符串1，用于商户验签签名规则请参考附录 2.6.3 消息签名
    private String storeId		;				    //storeId	定长20位数字，由vivo分发的唯一识别码
    private String storeOrder   ;					//商户自定义的订单号	商户自定义，最长 64 位字母、数字和下划线组成
    private String vivoOrder	;					//交易流水号	vivo订单号
    private String orderAmount	;					//交易金额	单位：分，币种：人民币，为长整型，如：101，10000
    private String channel		;					//用户使用的支付渠道
    private String channelFee		;					//渠道扣除的费用

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{
            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                logger.debug("SingleVivoPayCallbackAction params.name:" + name + ", value:" + value);
            }

            long orderID = Long.parseLong(storeOrder);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null) {
                logger.error("--------->The order is null~~~~~~~~~~~~~~ or the channel is null.");
                this.renderState(false, "notifyId 错误");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                logger.error("--------->The state of the order is complete. The state is " + order.getState());
                this.renderState(false, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            logger.debug("--------->orderData:" + order.toJSON());

            if(this.respCode.equals("0000")) {
                int realMoney = (int) (Float.parseFloat(orderAmount)*100);

                if (realMoney != order.getMoney()) {
                    logger.error("--------->The order money error,money:"+order.getMoney()+",channelMOney:"+realMoney);
                    this.renderState(false, "notifyId 错误");
                    return;
                }

                if (isValid(order.getChannel().getCpAppKey())) {
                    order.setRealMoney(realMoney);
                    order.setSdkOrderTime(System.currentTimeMillis() + "");
                    order.setCompleteTime(new Date());
                    order.setChannelOrderID(vivoOrder);
                    order.setState(PayState.STATE_SUC);
                    orderManager.saveOrder(order);
                    logger.debug("--------->send to game");
                    SendAgent.sendCallbackToServer(this.orderManager, order);
                    this.renderState(true, "");
                } else {
                    logger.debug("--------->send to game fail,sign error");
                    order.setChannelOrderID(vivoOrder);
                    order.setState(PayState.STATE_FAILED);
                    orderManager.saveOrder(order);
                    this.renderState(false, "sign 错误");
                }
            }else{

                this.renderState(false, "支付失败");
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("--------->payBack exception:"+e.getMessage());
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValid(String key) {
        Map<String,String> params=new HashMap();
        params.put("respCode",respCode);
        params.put("respMsg",respMsg);
        params.put("signMethod",signMethod);
        params.put("signature",signature);
        params.put("storeId",storeId);
        params.put("storeOrder",storeOrder);
        params.put("vivoOrder",vivoOrder);
        params.put("orderAmount",orderAmount);
        params.put("channel",channel);
        params.put("channelFee",channelFee);

        logger.debug("--------->response's param:"+params.toString()+", key:"+key);
        return VivoSignUtils.verifySignature(params,key);

    }

    private void renderState(boolean suc, String msg) throws IOException {

        StringBuilder sb = new StringBuilder();


        sb.append("result=").append(suc ? "OK" : "FAIL");
        sb.append("&").append("resultMsg=").append(msg);

        logger.debug("--------->The result to sdk is " + sb.toString()+",isSuc:"+suc);

        if(suc){
            this.response.setStatus(200);
        }else{
            this.response.setStatus(403);
        }

        PrintWriter out = this.response.getWriter();
        out.write(sb.toString());
        out.flush();


    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        SingleVivoPayCallbackAction.logger = logger;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreOrder() {
        return storeOrder;
    }

    public void setStoreOrder(String storeOrder) {
        this.storeOrder = storeOrder;
    }

    public String getVivoOrder() {
        return vivoOrder;
    }

    public void setVivoOrder(String vivoOrder) {
        this.vivoOrder = vivoOrder;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelFee() {
        return channelFee;
    }

    public void setChannelFee(String channelFee) {
        this.channelFee = channelFee;
    }

    public UOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(UOrderManager orderManager) {
        this.orderManager = orderManager;
    }
}
