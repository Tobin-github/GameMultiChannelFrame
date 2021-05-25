package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * 虫虫支付回调通知接口
 * Created by ant on 2018/01/22.
 */
@Controller
@Namespace("/pay/chongchong")
public class ChongChongCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(ChongChongCallbackAction.class.getName());

    private String transactionNo    	;				//虫虫支付订单号
    private String partnerTransactionNo ; 				//商户订单号
    private String statusCode           ;				//订单状态（0000表示支付成功，0002表示支付失败）
    private String productId            ;				//支付商品的Id
    private String orderPrice  		    ;				//订单金额（单位：元）
    private String packageId  		    ;				//游戏ID
    private String productName  	   	;				//支付商品的名称；1.6SDK新增字段，SDK的支付方法的productName参数原样返回
    private String extParam  		    ;				//扩展字段；1.6SDK新增字段，SDK的支付方法的extParam参数原样返回
    private String userId  		        ;				//用户ID；1.6SDK新增字段，用于校验下单是否一致
    private String sign  		        ;				//回调签名，接收到虫虫游戏回调请求时，先进行验签，再进行自己的业务处理

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                log.debug("=========参数======== params'sname:" + name + ", value:" + value);
            }

            long orderID = Long.parseLong(partnerTransactionNo);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                log.error("--------------->The order is null or the channel is null.");
                this.renderState(false, "partnerTransactionNo  错误");
                return;
            }
            log.info("--------------->order:"+order.toJSON());

            if(order.getState() > PayState.STATE_PAYING){
                log.error("--------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            int moneyInt = (int) (Math.round(Double.parseDouble(orderPrice) * 100));  //以分为单位

            if (order.getMoney() != moneyInt) {
                log.error("--------------->order money:"+order.getMoney()+", channel:"+Integer.parseInt(orderPrice)*100);
                this.renderState(false, "金额比对失败");
                return;
            }

            if (!"0000".equals(statusCode)) {
                log.error("--------------->statusCode:"+statusCode+", 订单状态比对失败");
                this.renderState(false, "订单状态比对失败");
                return;
            }

            String appSecret = order.getChannel().getCpAppSecret();

            if(doCheck(appSecret,sign)){
                log.info("---------------->The sign success");
                order.setRealMoney(moneyInt);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(transactionNo);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.info("---------------->The sign error");
                order.setChannelOrderID(transactionNo);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(false, "sign 错误");
            }


        }catch (Exception e){
            log.error("--------------->The payBack exception:"+e.getMessage());
            e.printStackTrace();
            try {
                this.renderState(false, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public boolean doCheck(String appSecret ,String sign) {
        try {
            String content = getBaseString(appSecret);

            log.info("--------------->content:"+content+",appSecret:"+appSecret+",sign:"+sign);
            String mySign = EncryptUtils.md5(content);

            boolean bverify = sign.equals(mySign);
            return bverify;
        } catch (Exception e) {
            log.error("--------------->The sign exception:"+e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String getBaseString(String appKey) {

        Map<String, String> paramsMap = new TreeMap<>();

        paramsMap.put("transactionNo", transactionNo);
        paramsMap.put("partnerTransactionNo", partnerTransactionNo);
        paramsMap.put("statusCode", statusCode);
        paramsMap.put("productId", productId);
        paramsMap.put("orderPrice", orderPrice);
        paramsMap.put("packageId", packageId);
        paramsMap.put("productName", productName);
        paramsMap.put("extParam", extParam);
        paramsMap.put("userId", userId);

        StringBuilder sb = new StringBuilder();

        for (Map.Entry param:
        paramsMap.entrySet()) {
            if (StringUtils.isEmpty(param.getKey() + "") || StringUtils.isEmpty(param.getValue() + "")) {
                continue;
            }
            sb.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }

        sb.append(appKey);
        return sb.toString();
    }

    private void renderState(boolean suc, String msg) throws IOException {
        String responseStr = "fail";

        if (suc) {
            responseStr = "success";
        }

        log.info("--------------->The result to sdk is "+responseStr);

        PrintWriter out = this.response.getWriter();
        out.write(responseStr);
        out.flush();
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getPartnerTransactionNo() {
        return partnerTransactionNo;
    }

    public void setPartnerTransactionNo(String partnerTransactionNo) {
        this.partnerTransactionNo = partnerTransactionNo;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getExtParam() {
        return extParam;
    }

    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
