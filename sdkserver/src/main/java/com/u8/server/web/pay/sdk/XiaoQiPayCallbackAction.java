package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.xiaoqi.RSAHelper;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

/**
 * 小七手游SDK支付回调处理
 * Created by ant on 2016/11/19.
 */

@Controller
@Namespace("/pay/xiaoqi")
public class XiaoQiPayCallbackAction extends UActionSupport{


    private String encryp_data;
    private String game_orderid;
    private String guid;
    private String subject;
    private String xiao7_goid;
    private String sign_data;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            long orderID = Long.parseLong(game_orderid);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                Log.d("The order is null or the channel is null.");
                this.renderState("failed");
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState("success");
                return;
            }

            if(isValid(order.getChannel())){

                String decryptData = new String(RSAHelper.decrypt(order.getChannel().getCpPayKey(), RSAHelper.decode(encryp_data)));
                Map<String, String> decryptMap = RSAHelper.decodeHttpQuery(decryptData);
                //这里是比较解密后的订单号与我们通过POST传递过来的订单号是否一致
                if (decryptMap.containsKey("game_orderid") && decryptMap.get("game_orderid").equals(game_orderid))
                {
                    String payflag = decryptMap.get("payflag");
                    if("1".equals(payflag)){
                        String realMoney = decryptMap.get("pay");
                        order.setState(PayState.STATE_SUC);
                        order.setCompleteTime(new Date());
                        order.setRealMoney(Float.valueOf(realMoney).intValue() * 100);
                        order.setChannelOrderID(xiao7_goid);
                        orderManager.saveOrder(order);
                        SendAgent.sendCallbackToServer(this.orderManager, order);
                        this.renderState("success");
                    }

                }
                else
                {
                    this.renderState("failed");
                }


            }else{
                order.setState(PayState.STATE_FAILED);
                order.setChannelOrderID(xiao7_goid);
                orderManager.saveOrder(order);
                this.renderState("failed");
            }


        }catch (Exception e){
            e.printStackTrace();
            try {
                this.renderState("failed");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private boolean isValid(UChannel channel) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("encryp_data", encryp_data);
        params.put("game_orderid", game_orderid);
        params.put("guid", guid);
        params.put("subject", subject);
        params.put("xiao7_goid", xiao7_goid);

        String signContent = RSAHelper.buildHttpQuery(params);

        Log.d("xiaoqi pay callback check sign. sign content:%s", signContent);
        Log.d("xiaoqi pay callback check sign. sign returned:%s", sign_data);
        Log.d("xiaoqi pay callback check sign. public key:%s", channel.getCpPayKey());

        boolean suc = RSAHelper.doCheck(signContent, sign_data, channel.getCpPayKey());

        Log.d("xiaoqi pay callback check sign result:%s",suc);

        return suc;

    }

    private void renderState(String resultMsg) throws IOException {

        renderText(resultMsg);

    }

    public String getEncryp_data() {
        return encryp_data;
    }

    public void setEncryp_data(String encryp_data) {
        this.encryp_data = encryp_data;
    }

    public String getGame_orderid() {
        return game_orderid;
    }

    public void setGame_orderid(String game_orderid) {
        this.game_orderid = game_orderid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getXiao7_goid() {
        return xiao7_goid;
    }

    public void setXiao7_goid(String xiao7_goid) {
        this.xiao7_goid = xiao7_goid;
    }

    public String getSign_data() {
        return sign_data;
    }

    public void setSign_data(String sign_data) {
        this.sign_data = sign_data;
    }
}
