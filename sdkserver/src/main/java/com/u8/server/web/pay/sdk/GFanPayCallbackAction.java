package com.u8.server.web.pay.sdk;

import com.thoughtworks.xstream.XStream;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.gfan.GFanPayResponse;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 机锋渠道SDK支付回调,成功才会回调；回调参数中没有机锋的订单号
 * Created by ant on 2015/12/2.
 */
@Controller
@Namespace("/pay/gfan")
public class GFanPayCallbackAction extends UActionSupport{

    private String sign;        //sign
    private String time;        //时间戳，用于加密验证
    private int u8ChannelID;    //渠道号

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Action("payCallback")
    public void payCallback(){
        try{

            if(TextUtils.isEmpty(sign) || TextUtils.isEmpty(time)){
                this.renderState(false);
                return;
            }

            UChannel channel = channelManager.queryChannel(u8ChannelID);
            if(channel == null){

                this.renderState(false);
                return;
            }

            String signLocal = EncryptUtils.md5(channel.getCpID()+time).toLowerCase();
            Log.d("the sign generated local is "+signLocal);

            if(!signLocal.equals(sign)){
                this.renderState(false);
                return;
            }

            BufferedReader br = this.request.getReader();
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine()) != null){
                sb.append(line).append("\r\n");
            }

            Log.d("GFan Pay Callback . request post params:" + sb.toString());

            XStream xStream = new XStream();
            xStream.alias("response", GFanPayResponse.class);
            GFanPayResponse res = (GFanPayResponse)xStream.fromXML(sb.toString());

            if(res == null){
                this.renderState(false);
                return;
            }

            long orderID = Long.parseLong(res.getOrder_id());

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null or the channel is null.orderID:%s", orderID);
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. orderID:%s;state:%s" , orderID, order.getState());
                this.renderState(true);
                return;
            }

            order.setChannelOrderID("");
            order.setRealMoney((res.getCost() / 10) * 100);     //cost是机锋券，1元=10机锋券。这里转为分
            order.setSdkOrderTime(res.getCreate_time());
            order.setCompleteTime(new Date());
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            this.renderState(true);

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

    private void renderState(boolean suc) throws IOException {

        String resp = "<response><ErrorCode>%s</ErrorCode><ErrorDesc>%s</ErrorDesc></response>";

        PrintWriter out = this.response.getWriter();
        if(suc){
            out.write(String.format(resp, "1", "Success"));
        }else{
            out.write(String.format(resp, "0", "Failed"));
        }
        out.flush();
    }

    public int getU8ChannelID() {
        return u8ChannelID;
    }

    public void setU8ChannelID(int u8ChannelID) {
        this.u8ChannelID = u8ChannelID;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
