package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.sdk.jinli.RSASignature;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * 金立SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/singlejinli")
public class SingleJinliPayCallbackAction extends UActionSupport{

    private static Logger log = Logger.getLogger(SingleJinliPayCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{
            Map<String, String> map = new HashMap<String, String>();

            Enumeration pNames = request.getParameterNames();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                map.put(name, value);
                log.debug("SingleJinliPayCallbackAction params.name:" + name + ", value:" + value);
            }
            String out_order_no = map.get("out_order_no");

            long orderID = Long.parseLong(out_order_no);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                log.error("---------------->The order is null or the channel is null.");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                log.error("---------------->The state of the order is complete. The state is "+order.getState());
                this.renderState("The state of the order is complete.");
                return;
            }

            log.info("---------------->The order:"+order.toJSON());

            String sign = map.get("sign");
            String mapMoney = map.get("deal_price");
            int realMoney= (int) (Float.parseFloat(mapMoney) * 100);
            String pubKey = order.getChannel().getCpPayKey();

            if (realMoney != order.getMoney()) {
                log.error("---------------->The order money error,money:"+order.getMoney()+",channelMoney:"+realMoney);
                this.renderState("The order money error");
                return;
            }

            if (validSign(map, sign, pubKey)) {
                order.setRealMoney(realMoney);
                order.setSdkOrderTime(System.currentTimeMillis() + "");
                order.setCompleteTime(new Date());
                order.setChannelOrderID("");
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                renderState("success");
                log.debug("--------->send to game");
                SendAgent.sendCallbackToServer(this.orderManager, order);
                return;
            }

            this.renderState("fail");
        }catch (Exception e){
            log.error("--------------->payBack exception,msg:"+e.getMessage());
            e.printStackTrace();
            try {
                renderState("exception,msg:"+e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private boolean validSign(Map<String, String> map, String sign, String pubKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        /****************************** 签名验证 *******************************************/

        StringBuilder contentBuffer = new StringBuilder();
        Object[] signParamArray = map.keySet().toArray();
        Arrays.sort(signParamArray);
        for (Object key : signParamArray) {
            String value = map.get(key);
            if (!"sign".equals(key) && !"msg".equals(key)) {// sign和msg不参与签名
                contentBuffer.append(key + "=" + value + "&");
            }
        }
        log.info("---------------->The signParams:"+contentBuffer.toString());

        String content = StringUtils.removeEnd(contentBuffer.toString(), "&");

        log.info("---------------->The content:"+content);
        if (StringUtils.isBlank(sign)) {
            renderState("fail");
            return true;
        }

        return RSASignature.doCheck(content, sign, pubKey, "UTF-8");
    }

    private void renderState(String resultMsg) throws IOException {

        log.info("---------------->The result to resultMsg:"+resultMsg);

        PrintWriter out = this.response.getWriter();
        out.write(resultMsg);
        out.flush();
        out.close();
    }

}
