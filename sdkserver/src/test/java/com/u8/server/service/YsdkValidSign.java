package com.u8.server.service;

import com.u8.server.sdk.ysdk.api.OpensnsException;
import com.u8.server.sdk.ysdk.api.SnsSigCheck;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kris
 * @create 2018-01-16 14:47
 */
public class YsdkValidSign {

    @Ignore
    @Test
    public void validSign() {

        //String priKey = "3wZbhw6xGtUBY6Ug27nkSH9tbZufZxLg";
        String priKey = "mqBA2BoFtM1gR0h1";
        String path = "/pay/ysdknew/payCallback";


        String amt = "95";
        String appid = "1106541667";
        String appmeta = "1468380818068996142*wechat*wechat";
        String billno = "-APPDJ72123-20180116-1150260438";
        String cftid = "4200000100201801165080686278";
        String channel_id = "00000000-android-00000000-861189039612048-ysdkwater-wechat";
        String clientver = "android";
        String openid = "o_WJo1eMryuAU4ZL6DJSFIu32Eck";
        String payamt_coins = "0";
        String paychannelsubid = "1";
        String payitem = "xdns_diamonds_30*10*1";
        String providetype = "5";
        String pubacct_payamt_coins = "";
        String token = "6EA67E0E1FF1DDD9BDDD38785C8A741415774";
        String ts = "1516080285";
        String ubazinga = "1";
        String version = "v3";
        String zoneid = "1";
        String sig = "APqk23BZ+GDWDq+KyO2tjOu77Ys=";

        Map<String, String> params = new HashMap<String, String>();

        try {
            boolean signOK = isSignOK(priKey, params, path);
            System.out.println(signOK);
        } catch (OpensnsException e) {
            e.printStackTrace();
        }

    }

    private boolean isSignOK(String  priKey, Map<String, String> params, String path) throws OpensnsException {

        //String sourceStr = StringUtils.generateUrlSortedParamString(params, "&", false, new String[]{"sig","cee_extend"});

        params.remove("u8ChannelID");
        String sig = params.remove("sig");

        for (String key : params.keySet()) {

            String val = params.get(key);
            val = encodeValue(val);
            params.put(key, val);
        }

        String method = "GET";
//        String path = getUrlPath(channel);
//        path = "/pay/ysdknew/payCallback";
//        String secret = channel.getCpPayID()+"&";
        String secret = priKey + "&";
//        secret = "bZifCQRIdhp66zMnOdiT6ksfYC6dI5bF&";//沙箱做测试用key

        String sigLocal = SnsSigCheck.makeSig(method, path, params, secret);

        return sigLocal.equals(sig);


    }

    /**
     * 应用发货URL接口的编码规则
     *
     * @param s
     * @return
     */
    public static String encodeValue(String s) {
        String rexp = "[0-9a-zA-Z!*()]";
        StringBuffer sb = new StringBuffer(s);
        StringBuffer sbRtn = new StringBuffer();
        Pattern p = Pattern.compile(rexp);
        char temp;
        String tempStr;

        for (int i = 0; i < sb.length(); i++) {
            temp = sb.charAt(i);
            tempStr = String.valueOf(temp);
            Matcher m = p.matcher(tempStr);

            boolean result = m.find();
            if (!result) {
                tempStr = hexString(tempStr);
            }
            sbRtn.append(tempStr);
        }

        return sbRtn.toString();
    }

    /**
     * 应用发货URL　十六进制编码
     *
     * @param s
     * @return
     */
    private static String hexString(String s) {
        byte[] b = s.getBytes();
        String retStr = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            retStr = "%" + hex.toUpperCase();
        }
        return retStr;
    }
}
