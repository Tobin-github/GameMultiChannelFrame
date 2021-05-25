package com.u8.server.service;

import com.u8.server.utils.EncryptUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author kris
 * @create 2018-01-10 14:22
 */
public class SingleBaiduPayBackTest {

    @Ignore
    @Test
    public void validSign() {
        /*String appid = "134";
        String orderid = "tzqgEpl6luIxdwp";
        String amount = "0";
        String unit = "yuan";
        String status = "success";
        String paychannel = "ct_sfdx";
        String appSecret = "1234567";*/

        String appid = "10465458";
        String orderid = "uRi4r4rNKZ7L6GS";
        String amount = "1";
        String unit = "fen";
        String status = "success";
        String paychannel = "tencentmm";
        String appSecret = "sXftnOc0macEpydGjOdXHVBOlx5aeGp6";

        StringBuilder sb = new StringBuilder();

        sb.append(appid).
                append(orderid).
                append(amount).
                append(unit).
                append(status).
                append(paychannel).
                append(appSecret);

        System.out.println(sb.toString());

        //10465458uRi4r4rNKZ7L6GS1fensuccesstencentmmFw5wcwd4G2u2Ni0KxD7k9bOb
        String mySign = EncryptUtils.md5(sb.toString());
        System.out.println(mySign);

    }

}
