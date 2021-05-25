package com.u8.server.sdk.ysdk.api;

import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.ysdk.api.OpensnsException;
import com.u8.server.sdk.ysdk.api.SnsSigCheck;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by ant on 2015/10/14.
 */
public class OpenApiV3 {

    private static UHttpAgent httpAgent = UHttpAgent.newInstance();

    /***
     * 支付api调用接口
     * @param serverAddr
     * @param scriptName
     * @param appID
     * @param appKey
     * @param params
     * @return
     */
    public static String api_pay(String serverAddr,
                          String scriptName,
                          String appID,
                          String appKey,
                          int accountType,
                          Map<String, String> params) throws OpensnsException, UnsupportedEncodingException {

        // 检查openid openkey等参数
        if (params.get("openid") == null)
        {
            Log.e("the openid is null");
            return null;
        }

        Log.d("The appID is "+appID);
        Log.d("The appKey is "+appKey);

        HashMap<String,String> cookies = new HashMap<String, String>();

        if(accountType == 1) {
            //微信
            cookies.put("session_id", "hy_gameid");
            cookies.put("session_type", "wc_actoken");
        }else{
            cookies.put("session_id", "openid");
            cookies.put("session_type", "kp_actoken");
        }


        try {
            cookies.put("org_loc", URLEncoder.encode(scriptName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 无需传sig,会自动生成
        params.remove("sig");

        // 添加固定参数
        params.put("appid", appID);

        // 请求方法
        String method = "get";

        // 签名密钥
        String secret = appKey + "&";

        // 计算签名
        String sig = SnsSigCheck.makeSig(method, scriptName, params, secret);

        params.put("sig", sig);

        StringBuilder sb = new StringBuilder(64);
        sb.append(serverAddr).append(scriptName);
        String url = sb.toString();

        String qs= mkQueryString(params);


        url += "?";
        url += qs;

        //通过调用以下方法，可以打印出最终发送到openapi服务器的请求参数以及url，默认注释
        printRequest(url,method,params);
        printRequest(url,method,cookies);

        // 发送请求
        String resp = getRequest(url, cookies);

        Log.e("The resp is "+resp);

        return resp;
    }

    //查询String
    public static String mkQueryString(Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder buffer = new StringBuilder(128);
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            buffer.append(URLEncoder.encode((String)entry.getKey(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("=").append(URLEncoder.encode((String)entry.getValue(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("&");
        }
        String tmp = buffer.toString();
        tmp = tmp.substring(0,tmp.length()-1);
        return tmp;
    }

    /**
     * 辅助函数，打印出完整的请求串内容
     *
     * @param url 请求cgi的url
     * @param method 请求的方式 get/post
     * @param params OpenApi的参数列表
     */
    private static void printRequest(String url,String method,Map<String, String> params)
    {
        System.out.println("==========Request Info==========\n");
        System.out.println("method:  " + method);
        System.out.println("url:  " + url);
        System.out.println("params:");
        System.out.println(params);
        System.out.println("querystring:");
        StringBuilder buffer = new StringBuilder(128);
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            try
            {
                buffer.append(URLEncoder.encode((String) entry.getKey(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("=").append(URLEncoder.encode((String)entry.getValue(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("&");
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        String tmp = buffer.toString();
        tmp = tmp.substring(0,tmp.length()-1);
        System.out.println(tmp);
        System.out.println();
    }

    public static String getRequest(
            String url,
            Map<String, String> cookies)
    {

        Map<String,String> headers = new HashMap<String, String>();

        if (cookies !=null && !cookies.isEmpty())
        {
            Iterator iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                buffer.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("; ");
            }

            // 设置cookie内容
            headers.put("Cookie", buffer.toString());

        }

        headers.put("User-Agent", "Java OpenApiV3 SDK Client");


        String resp = httpAgent.get(url, headers, null, "UTF-8");

        return resp;
    }
}
