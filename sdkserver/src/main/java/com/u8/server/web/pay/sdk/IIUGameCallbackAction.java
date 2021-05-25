package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.iiugame.MD5Signature;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UUserManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.IIUGameSendAgent;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * IIUGameSDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/iiugame")
public class IIUGameCallbackAction extends UActionSupport{

    private int Ugameid;
    private int Uid;
    private String Roleid;
    private String Serverid;
    private int Pay_channel;
    private String Cp_orderid;
    private String Orderid;
    private String Time;
    private String Amount;
    private String Price;
    private String Currency_type ;
    private String Ctext ;
    private String sPcText ;
    private String Sign ;

    private static Logger log = Logger.getLogger(IIUGameCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UUserManager userManager;
    @Autowired
    private UChannelManager mUChannelManager;

    @Action("payCallback")
    public void payCallback(){

        try {
            Enumeration pNames = request.getParameterNames();
            Map<String, String> paramMap = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String name = (String) pNames.nextElement();
                String value = request.getParameter(name);
                paramMap.put(name, value);

                if ("sPcText".equals(name)) {
                    this.sPcText = value;
                }
                log.debug("----------->params.name:" + name + ", value:" + value);
            }

            UChannel channel = mUChannelManager.getChannelByAppId(Ugameid+"");

            if (null == channel) {
                log.debug("------------>The channel is null or the channel is null.");
                return;
            }

            log.info("------------->channel:"+channel.toJSON());

            UUser user = userManager.getUserByCpID(channel.getAppID(), channel.getChannelID(), Uid + "");

            log.debug("------------>sendCallbackToGlobelServer ,user:" + user.toJSON());
            UOrder order = null;
            if (null == Cp_orderid) {


                if (null == user) {
                    log.error("------------>IIUGameCallback ,user is null,uid:" + Uid);
                    return ;
                }

                int money = (int) (Math.round(Double.parseDouble(this.Price) * 100));  //以分为单位
                log.debug("----------------------------------------->order money:" + money);
                String notifyUrl=user.getGame().getPayCallback();
                notifyUrl = notifyUrl.replace("U8Pay", "IIUGameWebPay");
                order = orderManager.generateOrder(user, money, Amount, "", "", this.Roleid, "", this.Serverid, "", user.getChannelUserID(), notifyUrl);
                log.debug("------------>IIUGameCallback ,IIUGame's order:" + order.toJSON());
            } else {
                long orderID = Long.parseLong(Cp_orderid);
                order = orderManager.getOrder(orderID);
                log.debug("------------>IIUGameCallback , order:" + order.toJSON());
            }


            log.debug("------------>IIUGameCallback enter,Cp_orderid:" + Cp_orderid);

            String realMoney = Price;
            int resultCode = 1;
            String resultMsg = "成功";

            if (order == null || order.getChannel() == null) {
                log.debug("------------>The order is null or the channel is null.");
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                log.debug("------------>The state of the order is complete. The state is " + order.getState());
                this.renderState(resultCode, resultMsg);
                return;
            }

            if (Ugameid == 0 || Uid == 0 || Orderid == null || Sign == null) {

                resultCode = 102;
                resultMsg = "参数错误";
                log.debug("------------>resultCode:" + resultCode + ", resultMsg:" + resultMsg);
                renderState(resultCode, resultMsg);
                return;
            } else {
                //sign验证

                Map<String, String> params = new TreeMap<String, String>(
                        new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 降序排序
                                return obj1.compareTo(obj2);
                            }
                        });

                if (StringUtils.isNotEmpty(Cp_orderid)) {
                    params.put("Cp_orderid", Cp_orderid);
                }

                if (StringUtils.isNotEmpty(Ctext)) {
                    params.put("Ctext", Ctext);
                }

                if (StringUtils.isNotEmpty(sPcText)) {
                    params.put("sPcText", sPcText);
                }

                params.put("Ugameid", Ugameid + "");
                params.put("Uid", Uid + "");
                params.put("Roleid", Roleid);
                params.put("Serverid", Serverid);
                params.put("Pay_channel", Pay_channel + "");
                params.put("Orderid", Orderid);
                params.put("Cp_orderid", Cp_orderid);
                params.put("Time", Time);
                params.put("Amount", Amount);
                params.put("Price", Price);
                params.put("Currency_type", Currency_type);

                String sercerKey = order.getChannel().getCpAppSecret();
                log.debug("------------>params:" + params.toString() + ", sercerKey:" + sercerKey);

                String stringData = getStringData(params);
                log.debug("------------>stringData:" + stringData + ", sercerKey:" + sercerKey);
                String signLocal = MD5Signature.sign(stringData, sercerKey);

                log.debug("------------>The new sign is " + signLocal.toString());
                //签名验证
                if (!signLocal.equals(Sign.toLowerCase())) {
                    resultCode = 102; //sign无效
                    resultMsg = "Sign无效";
                    log.debug("------------>sign is invali,resultCode" + resultCode + ", resultMsg:" + resultMsg);
                    renderState(resultCode, resultMsg);
                    return;
                }
            }
            int moneyInt = (int) (Math.round(Double.parseDouble(realMoney) * 100));  //以分为单位
            log.debug("----------------------------------------->channel money:" + moneyInt);

            if (!(moneyInt+"").equals(order.getMoney()+"")) {
                log.error("------------>The money error,money:"+order.getMoney()+", channelMoney:"+moneyInt);
                return;
            }

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(Time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(Orderid);
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);

            if (null == Cp_orderid) {
                log.debug("------------>sendCallbackToGlobelServer  web pay,send to game,order:" + order.toJSON());
                IIUGameSendAgent.sendCallbackToServer(this.orderManager, order, sPcText);
                renderState(resultCode, resultMsg);
                return;
            }


            log.debug("------------>sendCallbackToGlobelServer ,send to game,order:" + order.toJSON());
            SendAgent.sendCallbackToServer(this.orderManager, order);
            //IIUGameSendAgent.sendCallbackToServer(this.orderManager, order,user.getChannelUserID());

            /*if (Price.equals(order.getMoney()+"")) {

                    order.setRealMoney(Integer.parseInt(Price));
                    order.setSdkOrderTime(Time);
                    order.setCompleteTime(new Date());
                    order.setChannelOrderID(Orderid);
                    order.setState(PayState.STATE_SUC);
                    orderManager.saveOrder(order);
                    SendAgent.sendCallbackToServer(this.orderManager, order);
            }else{
                order.setChannelOrderID(Orderid);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                resultCode= 102; //sign无效
                resultMsg="订单金额比对失败";
            }*/

            renderState(resultCode, resultMsg);

        }catch (Exception e){
            log.error("------------>exception:"+e.getMessage());
            e.printStackTrace();
        }

    }

    private String getStringData(Map<String, String> params) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i <keys.size() ; i++) {
            String key = keys.get(i);
            if (key.equals(Sign)) {
                continue;
            }
            String value = params.get(key);
            if (value != null && value.length() > 0) {
                content.append(value);
            }
        }
        return content.toString();

    }

    private void renderState(int resultCode, String resultMsg) throws IOException {

        JSONObject json = new JSONObject();
        json.put("code", resultCode);
        json.put("msg", resultMsg);

        Log.d("The result to sdk is "+json.toString());

        PrintWriter out = this.response.getWriter();
        out.write(json.toString());
        out.flush();

    }

    /**
     * @param characterEncoding 编码格式
     * @param parameters        请求参数
     * @return
     * @author lwz
     * @date 2014-12-8
     * @Description：sign签名
     */
    public static String createSign(String characterEncoding, Map<String, String> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            k=k.trim();
            if (v != null && v.length() > 0) {
                v = v.trim();
            }
            if (null != v && !"".equals(v) && !"sign".equals(k)) {
                sb.append(k +v );
            }
        }
        sb.append(API_KEY);

        String sign = EncryptUtils.md5(sb.toString()).toUpperCase();

        return sign;
    }

    public int getUgameid() {
        return Ugameid;
    }

    public void setUgameid(int ugameid) {
        Ugameid = ugameid;
    }

    public int getUid() {
        return Uid;
    }

    public void setUid(int uid) {
        Uid = uid;
    }

    public String getRoleid() {
        return Roleid;
    }

    public void setRoleid(String roleid) {
        Roleid = roleid;
    }

    public String getServerid() {
        return Serverid;
    }

    public void setServerid(String serverid) {
        Serverid = serverid;
    }

    public int getPay_channel() {
        return Pay_channel;
    }

    public void setPay_channel(int pay_channel) {
        Pay_channel = pay_channel;
    }

    public String getCp_orderid() {
        return Cp_orderid;
    }

    public void setCp_orderid(String cp_orderid) {
        Cp_orderid = cp_orderid;
    }

    public String getOrderid() {
        return Orderid;
    }

    public void setOrderid(String orderid) {
        Orderid = orderid;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public UOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(UOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public String getCurrency_type() {
        return Currency_type;
    }

    public void setCurrency_type(String currency_type) {
        Currency_type = currency_type;
    }

    public String getCtext() {
        return Ctext;
    }

    public void setCtext(String ctext) {
        Ctext = ctext;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getsPcText() {
        return sPcText;
    }

    public void setsPcText(String sPcText) {
        this.sPcText = sPcText;
    }
}
