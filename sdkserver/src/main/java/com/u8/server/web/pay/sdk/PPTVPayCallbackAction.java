package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * PPTV 支付回调处理类
 * Created by ant on 2016/1/21.
 *
 * 
 */

@Controller
@Namespace("/pay/pptv")
public class PPTVPayCallbackAction extends UActionSupport {

    private String sid		;   //服务器标识，使用字母与数字的组合
    private String roid     ;   //服务器角色id，使用字母与数字的组合，无特殊必要，可以为空（游戏方的）
    private String username ;   //用户名， urlencode
    private String oid      ;   //订单号(pptv订单号)
    private String amount   ;   //充值金额 元
    private String extra    ;   //附属信息，需要urlencode。一般情况此参数为空。
    private String time     ;   //充值发起时间，unix 时间戳
    private String sign     ;   //验证串

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(extra);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null");
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if(channel == null){
                Log.d("the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if(!isSignOK(channel)){
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", sign, channel.getCpPayKey(), extra);
                this.renderState(false);
                return;
            }

            int moneyInt = (int)(Float.valueOf(amount) * 100);

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID(this.oid);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            SendAgent.sendCallbackToServer(this.orderManager, order);

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

    private boolean isSignOK(UChannel channel){
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(sid).append(URLEncoder.encode(username, "UTF-8"))
                    .append(roid).append(oid).append(amount).append(time)
                    .append(channel.getCpAppSecret());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("sign txt:"+sb.toString());

        String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equals(this.sign);
    }

    private void renderState(boolean suc) throws IOException {

        JSONObject json = new JSONObject();

        if(suc){

            json.put("code", "1");
            json.put("message", "");
        }else{

            json.put("code", "2");
            json.put("message", "支付失败");
        }

        renderJson(json.toString());
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getRoid() {
        return roid;
    }

    public void setRoid(String roid) {
        this.roid = roid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
