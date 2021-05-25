package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.htc.RSASignature;
import com.u8.server.service.UOrderManager;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * HtcSDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/htc")
public class HtcPayCallbackAction extends UActionSupport{

    private int AppID;
    private String OrderSerial;
    private String CooperatorOrderSerial;
    private String Sign;
    private String Content;

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            String payContent = new String(IOUtils.toByteArray(request
                    .getInputStream()), "utf-8");
            System.out.println(">>>订单内容" + payContent);
            Map<String, String> paramters = changeToParamters(payContent);
            String signType = paramters.get("sign_type");//签名类型，一般是RSA
            String sign = java.net.URLDecoder.decode(paramters.get("sign"),"utf-8");// 签名
            String order = paramters.get("order");

            System.out.println("signType="+signType);
            System.out.println("sign="+sign);
            System.out.println("order1="+order);

            // 订单真实数据
            String orderDecoderToJson = java.net.URLDecoder.decode(order, "utf-8");// urlDecoder
            System.out.println("order2="+orderDecoderToJson);																// 为json数据
            // step1 先校验订单

                try {
                    //json解析，仅供参考
                    JSONObject jsonObject = JSONObject.fromObject(orderDecoderToJson);
                    String cpOrderId=(String)jsonObject.get("game_order_id");//cp自身的订单号
                    UOrder uOrder = orderManager.getOrder(Long.parseLong(cpOrderId));

                    if(uOrder == null){
                        Log.d("The order is null, orderId:"+cpOrderId);
                        response.getWriter().print("fail");
                        response.getWriter().flush();
                        return;
                    }

                    UChannel channel = uOrder.getChannel();
                    if(channel == null){
                        Log.d("the channel is null.");
                        response.getWriter().print("fail");
                        response.getWriter().flush();
                        return;
                    }

                    if(uOrder.getState() > PayState.STATE_PAYING) {
                        Log.d("The state of the order is complete. The state is " + uOrder.getState());
                        response.getWriter().print("fail");
                        response.getWriter().flush();
                        return;
                    }

                    String rsaKey = uOrder.getChannel().getCpPayKey();

                    int resultCode=jsonObject.getInt("result_code");//1成功 0失败

                    boolean isOk = RSASignature.doCheck(orderDecoderToJson, sign, rsaKey);

                    if (isOk&&resultCode==1) {
                        String resultMsg=(String)jsonObject.get("result_msg");//支付信息
                        String gameCode=(String)jsonObject.get("game_code");//游戏编号
                        int realAmount=(int)jsonObject.getInt("real_amount");//付款成功金额，单位人民币分

                        String joloOrderId=(String)jsonObject.get("jolo_order_id");//jolo订单
                        String createTime=(String)jsonObject.get("gmt_create");//创建时间 订单创建时间 yyyy-MM-dd  HH:mm:ss
                        String payTime=(String)jsonObject.get("gmt_payment");//支付时间 订单支付时间  yyyy-MM-dd  HH:mm:ss

                        if (realAmount != uOrder.getMoney()) {
                            Log.d("The money is not right. ");
                            response.getWriter().print("fail");
                            response.getWriter().flush();
                            return ;
                        }

                        uOrder.setRealMoney(realAmount);
                        uOrder.setSdkOrderTime("");
                        uOrder.setCompleteTime(new Date());
                        uOrder.setChannelOrderID(joloOrderId);
                        uOrder.setState(PayState.STATE_SUC);

                        orderManager.saveOrder(uOrder);

                        SendAgent.sendCallbackToServer(this.orderManager, uOrder);

                        response.getWriter().print("success");
                        response.getWriter().flush();
                    }


                } catch (Exception e) {
                    response.getWriter().print("fail");
                    response.getWriter().flush();
                    e.printStackTrace();
                }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Map<String, String> changeToParamters(String payContent) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotBlank(payContent)) {
            String[] paramertes = payContent.split("&");
            for (String parameter : paramertes) {
                String[] p = parameter.split("=");
                map.put(p[0], p[1].replaceAll("\"", ""));
            }
        }
        return map;
    }



    public int getAppID() {
        return AppID;
    }

    public void setAppID(int appID) {
        AppID = appID;
    }

    public String getOrderSerial() {
        return OrderSerial;
    }

    public void setOrderSerial(String orderSerial) {
        OrderSerial = orderSerial;
    }

    public String getCooperatorOrderSerial() {
        return CooperatorOrderSerial;
    }

    public void setCooperatorOrderSerial(String cooperatorOrderSerial) {
        CooperatorOrderSerial = cooperatorOrderSerial;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
