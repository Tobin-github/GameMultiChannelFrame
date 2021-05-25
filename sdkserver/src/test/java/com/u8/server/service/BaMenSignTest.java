package com.u8.server.service;

import org.junit.Ignore;
import org.junit.Test;

import java.text.DecimalFormat;

/**
 * @author kris
 * @create 2018-01-23 9:34
 */
public class BaMenSignTest {

    @Ignore
    @Test
    public void generSign() {
        /*String appKey = "d819f9b19616d35e243b7bc1910fbfd9";

        String order_no = "1201221813294761";
        String game_order_no = "1471881130515890177";
        String uid = "19447958";
        String pay_money = "0.01";
        String service_id = "1364";
        String pid = "1";
        String time = "1516618132";
        String paystatus = "1";

        String sign = "02ef908c708f63c665b6009b9f142260";

        StringBuilder sb = new StringBuilder();
        sb.append(order_no)
                .append(game_order_no)
                .append(uid)
                .append(pay_money)
                .append(service_id)
                .append(pid)
                .append(time)
                .append(paystatus)
                .append(appKey);

        String md5Str = EncryptUtils.md5(sb.toString());
        System.out.println(md5Str);*/
        DecimalFormat df=new DecimalFormat("0.00");
        String orderMoney =df.format(1/100.00);
        System.out.println(orderMoney);
    }
}
