package com.u8.server.sdk.CYPlatform;

import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 31游SDK
 * Created by ant on 2016/5/10.
 */
public class CYPlatformSDK implements ISDKScript{

    private static Logger log = Logger.getLogger(CYPlatformSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String sessionId = json.getString("sessionid");
            String openuId = json.getString("openuid");
            String userName = json.getString("username");

            Integer appID = Integer.parseInt(channel.getCpAppID());
            String appKey = channel.getCpAppKey();

            log.info("--------->verify ,The auth data :" + json.toString()+", appID:"+appID+", appKey:"+appKey);

            //通过游戏服务器 取得用户信息
            int time = ParseNow();
            String param = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s", appID, GetEncode(sessionId), time);
            String code = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s%s", appID, GetEncode(sessionId.replace(" ", "+")), time, appKey);
            String sign = getMD5(code);
            param += "&sign=" + sign;
            String url = channel.getMaster().getAuthUrl();
            String sResponseFromServer = HttpPost(url, param);

            log.info("--------->verify ,The auth result :" + sResponseFromServer);

            //如果成功的话返回值格式：{"userInfo":{"username":"yx598640","uid":"6596"},"code":1}
            if (sResponseFromServer != null && sResponseFromServer.indexOf("\"code\":1") >= 0) {
                SDKVerifyResult vResult = new SDKVerifyResult(true, openuId, "", "","");
                callback.onSuccess(vResult);
                return ;
            }

            callback.onFailed("verify failed. the result is " + sResponseFromServer);

        }catch(Exception e){
            log.error("---------> verify total exception,the result is " + e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        DecimalFormat df=new DecimalFormat("0.00");
        String orderMoney =df.format(order.getMoney()/100.00);

        JSONObject json = new JSONObject();
        json.put("product_name",order.getProductName());
        //json.put("amount",order.getMoney()/100+"");
        json.put("amount",orderMoney);
        json.put("orderNo",order.getOrderID()+"");
        json.put("currency","金币");

        if (callback != null) {
            Log.d("SouGouSDK onGetOrderID,the json is " + json.toString());
            callback.onSuccess(json.toString());
        }
    }

    private static int ParseNow()
    {
        return (int) (Calendar.getInstance().getTimeInMillis() / 1000);
    }

    protected static String GetEncode(String str)
    {
        try {
            return URLEncoder.encode(str,"utf-8");
        } catch (Exception e) {
        }
        return "";
    }

    private static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {

        }

        return "";
    }

    public static String HttpPost(String targetUrl, String param) {

        byte[] postData = param.getBytes();
        InputStream instr = null;
        try {
            URL url = new URL(targetUrl);
            trustAllHosts();
            HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
            urlCon.setRequestProperty("charset", "utf-8");
            urlCon.setRequestProperty("Content-length",
                    String.valueOf(postData.length));
            System.out.println(String.valueOf(postData.length));
            DataOutputStream printout = new DataOutputStream(
                    urlCon.getOutputStream());
            printout.write(postData);
            printout.flush();
            printout.close();
            instr = urlCon.getInputStream();
            byte[] bis = IOUtils.toByteArray(instr);
            String ResponseString = new String(bis, "UTF-8");
            if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                System.out.println("返回空");
            }
            System.out.println("返回数据为:" + ResponseString);
            return ResponseString;

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            try {
                instr.close();

            } catch (Exception ex) {
                return "0";
            }
        }
    }

    private static void trustAllHosts()
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
            }
        } };

        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
