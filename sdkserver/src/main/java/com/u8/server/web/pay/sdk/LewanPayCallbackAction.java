package com.u8.server.web.pay.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import com.util.encrypt.AES;
import com.util.encrypt.EncryUtil;
import com.util.encrypt.RSA;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

/**
 * 乐玩SDK支付回调处理类
 * Created by ant on 2016/1/18.
 */
@Controller
@Namespace("/pay/lewan")
public class LewanPayCallbackAction extends UActionSupport{

    private String encryptkey;
    private String data;

    private int u8ChannelID;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            UChannel channel = channelManager.queryChannel(u8ChannelID);
            if(channel == null){
                Log.d("the channel is null.channelID:%s", u8ChannelID);
                this.renderState(false);
                return;
            }

            boolean signOK = EncryUtil.checkDecryptAndSign(data, encryptkey, channel.getCpPayKey(), channel.getCpPayPriKey());

            if(!signOK){
                Log.d("The sign verify failed.sign:%s;payKey:%s;priKey:%s", encryptkey, channel.getCpPayKey(), channel.getCpPayPriKey());
                this.renderState(false);
                return;
            }

            /** 1.使用游戏的的私钥解开aesEncrypt。 */
            String AESKey = "";
            try {
                AESKey = RSA.decrypt(encryptkey, channel.getCpPayPriKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
            /** 2.用aeskey解开data。取得data明文 */
            String realData = AES.decryptFromBase64(data, AESKey);
            TreeMap<String, String> map = JSON.parseObject(realData, new TypeReference<TreeMap<String, String>>() {
            });



            long orderID = Long.parseLong(map.get("gameOrderId"));

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null");
                this.renderState(false);
                return;
            }


            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if("2".equals(map.get("payState"))){

                int moneyInt = Integer.valueOf(map.get("paySuccessMoney"));

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime("");
                order.setCompleteTime(new Date());
                order.setChannelOrderID(map.get("lewanOrderId"));
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                order.setChannelOrderID(map.get("lewanOrderId"));
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }

            renderState(true);

        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "fail";
        }

        renderText(res);
    }

    public String getEncryptkey() {
        return encryptkey;
    }

    public void setEncryptkey(String encryptkey) {
        this.encryptkey = encryptkey;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getU8ChannelID() {
        return u8ChannelID;
    }

    public void setU8ChannelID(int u8ChannelID) {
        this.u8ChannelID = u8ChannelID;
    }
}
