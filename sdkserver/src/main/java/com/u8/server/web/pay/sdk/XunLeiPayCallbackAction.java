package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.xunlei.XunLeiPayResult;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 迅雷SDK支付回调处理
 * Created by ant on 2016/12/7.
 */
@Controller
@Namespace("/pay/xunlei")
public class XunLeiPayCallbackAction extends UActionSupport{


    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            BufferedReader br = this.request.getReader();
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine()) != null){
                sb.append(line);
            }

            Log.d("XunLei Pay Callback . request params:" + sb.toString());

            XunLeiPayResult result = (XunLeiPayResult) JsonUtils.decodeJson(sb.toString(), XunLeiPayResult.class);

            if(result == null || result.getOrder_info() == null){
                renderState(false);
                return;
            }

            long orderID = Long.parseLong(result.getOrder_info().getProduct_id());

            UOrder order = orderManager.getOrder(orderID);

            if(order == null || order.getChannel() == null){
                Log.d("The order is null or the channel is null.");
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if(channel == null){

                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING){
                Log.d("The state of the order is complete. The state is "+order.getState());
                this.renderState(true);
                return;
            }

            if(!"success".equals(result.getOrder_info().getPay_status())){
                Log.d("The remote state of the order %s is %s. ", orderID, result.getOrder_info().getPay_status());
                this.renderState(false);
                return;
            }

            if(!isSignOK(channel, result)){
                Log.d("The sign verify failed.sign:%s;appSecret:%s;orderID:%s", result.getSign(), channel.getCpAppSecret(), orderID);
                this.renderState(false);
                return;
            }

            float money = Float.parseFloat(result.getOrder_info().getTotal_amount());
            int moneyInt = (int)(money * 100);  //以分为单位

            if(money < order.getMoney()){
                Log.d("order:%s; remote price:%s;local price:%s", orderID, moneyInt, order.getMoney());
                this.renderState(false);
                return;
            }


            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(result.getOrder_info().getPay_time());
            order.setCompleteTime(new Date());
            order.setChannelOrderID(result.getOrder_info().getOrder_id());        //渠道orderID貌似没有值
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);


        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel, XunLeiPayResult payData){

        Map<String,String> params = new HashMap<String,String>();

        XunLeiPayResult.XunLeiOrder data = payData.getOrder_info();

        params.put("body",data.getBody());
        params.put("channel_pay_uid",data.getChannel_pay_uid());
        params.put("order_id",data.getOrder_id());
        params.put("channel_trade_no",data.getChannel_trade_no());
        params.put("server_id",data.getServer_id());
        params.put("create_time",data.getCreate_time());
        params.put("pay_channel",data.getPay_channel());
        params.put("pay_time",data.getPay_time());
        params.put("game_id",data.getGame_id());
        params.put("subject",data.getSubject());
        params.put("pay_uid",data.getPay_uid());
        params.put("total_amount",data.getTotal_amount());
        params.put("notify_status",data.getNotify_status());
        params.put("product_id",data.getProduct_id());
        params.put("pay_status",data.getPay_status());

        String signStr = StringUtils.generateUrlSortedParamString(params, "&", true);

        signStr += "&key="+channel.getCpAppSecret();

        Log.d("str to sign:%s", signStr);

        String signLocal = EncryptUtils.md5(signStr).toUpperCase();
        Log.d("sign local:%s;sign remote:%s", signLocal, payData.getSign());

        return signLocal.equals(payData.getSign());
    }

    private void renderState(boolean suc) throws IOException {

        String res = "success";
        if(!suc){
            res = "fail";
        }

        PrintWriter out = this.response.getWriter();
        out.write(res);
        out.flush();
    }

}



