package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * 单机Oppo支付回调通知接口
 * Created by ant on 2015/4/22.
 */
@Controller
@Namespace("/pay/singleoppo")
public class SingleOppoPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleOppoPayCallbackAction.class.getName());

    private String notifyId  	;				//回调通知 ID（该值使用系统为这次支付生成的订单号）String(50)
    private String partnerOrder ; 				//开发者订单号（客户端上传）  String(100)
    private String productName  ;				//商品名称（客户端上传）  String(50)
    private String productDesc  ;				//商品描述（客户端上传）  String(100)
    private String price  		;				//商品价格(以分为单位)  int
    private String count  		;				//商品数量（一般为 1）  int
    private String attach  		;				//请求支付时上传的附加参数（客户端上传）   String(200)
    private String sign  		;				//签名  String

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(partnerOrder);

            log.info("--------------->The orderId:"+partnerOrder+",notifyId:"+notifyId+",attach:"+attach);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                log.error("--------------->The order is null or the channel is null.");
                this.renderState(false, "notifyId  错误");
                return;
            }
            log.info("--------------->The orderId:"+partnerOrder+", order:"+order.toJSON());

            if(order.getState() > PayState.STATE_PAYING){
                log.error("--------------->The state of the order is complete. The state is "+order.getState());
                this.renderState(true, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if (order.getMoney() != Integer.parseInt(price)) {
                log.error("--------------->order money:"+order.getMoney()+", channel:"+Integer.parseInt(price));
                this.renderState(false, "金额比对失败");
                return;
            }

            String baseString = getBaseString();
            String publicKey = order.getChannel().getCpPayKey();

            log.info("--------------->baseString:"+baseString+",publicKey:"+publicKey+",sign:"+sign);

            if(doCheck(baseString,sign,publicKey)){
                log.info("--------------->The sign success");
                order.setRealMoney(Integer.valueOf(price));
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(notifyId);
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);
                this.renderState(true, "");
            }else{
                log.info("--------------->The sign error");
                order.setChannelOrderID(notifyId);
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

    /*private boolean isValid(UChannel channel){

        String content = getBaseString();
        log.info("--------------->The unSignParam:"+content);
        try{

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(channel.getCpPayKey());
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));
            boolean bverify = signature.verify(Base64.decodeBase64(sign));
            log.info("--------------->The Sign result:"+bverify);
            return bverify;

        }catch(Exception e){
            log.error("--------------->The sign exception:"+e.getMessage());
            e.printStackTrace();
        }

        return false;

    }*/

    public static boolean doCheck(String content, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new
                    X509EncodedKeySpec(encodedKey));
            java.security.Signature signature =
                    java.security.Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));
            boolean bverify = signature.verify(Base64.decodeBase64(sign));
            return bverify;
        } catch (Exception e) {
            log.error("--------------->The sign exception:"+e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String getBaseString() {
        StringBuilder sb = new StringBuilder();
        sb.append("notifyId=").append(notifyId);
        sb.append("&partnerOrder=").append(partnerOrder);
        sb.append("&productName=").append(productName);
        sb.append("&productDesc=").append(productDesc);
        sb.append("&price=").append(price);
        sb.append("&count=").append(count);
        sb.append("&attach=").append(attach);
        return sb.toString();
    }

    private void renderState(boolean suc, String msg) throws IOException {

        StringBuilder sb = new StringBuilder();


        sb.append("result=").append(suc ? "OK" : "FAIL");
        sb.append("&").append("resultMsg=").append(msg);

        log.info("--------------->The result to sdk is "+sb.toString());

        PrintWriter out = this.response.getWriter();
        out.write(sb.toString());
        out.flush();


    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getPartnerOrder() {
        return partnerOrder;
    }

    public void setPartnerOrder(String partnerOrder) {
        this.partnerOrder = partnerOrder;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
