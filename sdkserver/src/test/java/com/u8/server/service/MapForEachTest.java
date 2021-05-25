package com.u8.server.service;

import com.u8.server.utils.EncryptUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author kris
 * @create 2018-01-04 10:50
 */
public class MapForEachTest {

    @Ignore
    @Test
    public void testMapForEach() {
        /*String userId = "1";
        String goodsId = "1";
        String goodsName = "1";
        String payOrderId = "1";
        String payPrice = "1";
        String payStatus = "1";

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        sb2.append("goodsId=").append(goodsId).append("&").
                append("goodsName=").append(goodsName).append("&").
                append("payOrderId=").append(payOrderId).append("&").
                append("payPrice=").append(payPrice).append("&").
                append("payStatus=").append(payStatus).append("&").
                append("userId=").append(userId);

        System.out.println(sb2.toString());

        Map<String, String> params = new TreeMap<>();
        params.put("userId",userId);
        params.put("goodsId",goodsId);
        params.put("goodsName",goodsName);
        params.put("payOrderId",payOrderId);
        params.put("payPrice",payPrice);
        params.put("payStatus",payStatus);

        params.forEach((k,v)->sb.append(k).append("=").append(v).append("&"));

        String paramStr = sb.substring(0, sb.length() - 1);

        //goodsId=1&goodsName=1&payOrderId=1&payPrice=1&payStatus=1&userId=1

        System.out.println(paramStr);
        String mySign = EncryptUtils.md5(paramStr);*/

        /*Map<String, String> params = new TreeMap<>();
        params.put("userId","1");
        params.put("goodsId","1");
        params.put("goodsName","1");
        params.put("payOrderId","1");
        params.put("payPrice","1");
        params.put("payStatus","1");
        params.put("u8ChannelID","1");*/

        /*params.name:payOrderId, value:5a5c679790bbb0e48f84562f
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:u8ChannelID, value:1774
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:goodsId, value:1467901469653991430
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:payPrice, value:120
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:sign, value:3e527d5c90e7d3692cddbe315fac6a10
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:goodsName, value:120000点数
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:payStatus, value:1
        2018-01-15 16:38:45 DEBUG YunWaPayCallbackAction:52 - -------> params.name:userId, value:xx33636400*/

        String paramsStr = "goodsId=1467901469653991430&goodsName=120000点数&payOrderId=5a5c679790bbb0e48f84562f&payPrice=120&payStatus=1&userId=xx33636400&appKey=BSSKDIAQMLXCWNGE";

        System.out.println(paramsStr);

        String s = EncryptUtils.md5(paramsStr);
        System.out.println(s);
    }
}
