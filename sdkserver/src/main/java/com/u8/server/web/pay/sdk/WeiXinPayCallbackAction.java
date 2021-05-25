package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 游龙SDK支付回调处理类
 * Created by ant on 2016/1/19.
 */

@Controller
@Namespace("/pay/wxpay")
public class WeiXinPayCallbackAction extends UActionSupport {

    private static Logger log = Logger.getLogger(WeiXinPayCallbackAction.class.getName());

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback() {
        try {
            log.info("---------------->payCallback start");
            InputStream inputStream;
            inputStream = request.getInputStream();

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            String orderNo = "";
            String sign = "";
            String Total_fee = "";
            String result_code = "";

            HashMap hm = new HashMap<String, String>();
            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
            for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
                Element element = (Element) iter.next();

                String value = element.getText();
                if (null != value && value != "") {
                    String key = element.getName();
                    if (key.equals("out_trade_no")) {
                        orderNo = value;
                    }
                    if (key.equals("sign")) {
                        sign = value;
                    } else {
                        params.put(key, value);
                    }
                    if (key.equals("total_fee")) {
                        Total_fee = value;
                    }
                    if (key.equals("result_code")) {
                        result_code = value;
                    }
                }
            }
            log.info("---------------->params:"+params.toString());
            inputStream.close();

            if ("FAIL".equals(result_code)) {
                log.error("--------------------->wei xin payCallBack is Fail");
                return;
            }

            long orderID = Long.parseLong(orderNo);
            UOrder order = orderManager.getOrder(orderID);


            if (order == null) {
                log.error("---------------->The order is null, orderId:" + orderID);
                this.renderState(false);
                return;
            }
            log.info("---------------->order:"+order.toJSON());

            if (!Total_fee.equals(order.getMoney()+"")) {
                log.error("---------------->money 对比失败，money:"+order.getMoney()+",channelMoney:"+Total_fee);
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            log.info("---------------->channel:"+channel.toJSON());
            if (channel == null) {
                log.error("---------------->the channel is null.");
                this.renderState(false);
                return;
            }

            if (order.getState() == PayState.STATE_COMPLETE) {
                log.error("---------------->The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if (!isSignOK(channel, sign, params)) {
                log.error("---------------->The sign verify failed.sign:"+sign+",appKey:"+channel.getCpAppKey()+", orderID:"+orderID);
                this.renderState(false);
                return;
            }

            int moneyInt = Integer.parseInt(Total_fee);  //以分为单位
            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID("");
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);
            log.info("---------------->ready send to game");
            SendAgent.sendCallbackToServer(this.orderManager, order);

            //renderState(true);

            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(renderState(true).getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            try {
                log.info("---------------->exception:"+e.getMessage());
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel, String sign, SortedMap<Object, Object> parameters) {

        String APPkey = channel.getCpAppKey();
        if (APPkey.contains("\"WX\":")) {
            JSONObject appKeyObj = JSONObject.fromObject("{" + APPkey + "}");
            APPkey = appKeyObj.getString("WX");
        }

        log.info("---------------->sing param:"+parameters.toString()+", APPkey:"+APPkey);

        String signLocal = createSign("UTF-8", parameters, APPkey);

        log.info("---------------->sing:"+signLocal+", channel Sign:"+sign);
        return signLocal.equals(sign);
    }

    /**
     * @param characterEncoding 编码格式
     * @param parameters        请求参数
     * @return
     * @author lwz
     * @date 2014-12-8
     * @Description：sign签名
     */
    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = EncryptUtils.md5(sb.toString()).toUpperCase();

        return sign;
    }


    private String renderState(boolean suc) throws IOException {
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        if (!suc) {
            parameters.put("return_code", "FAIL");
            parameters.put("return_msg", "UNKnow");
        } else {
            parameters.put("return_code", "SUCCESS");
            parameters.put("return_msg", "OK");
        }
        String res = getRequestXml(parameters);
        //renderXml(res);
        return res;
    }

    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
