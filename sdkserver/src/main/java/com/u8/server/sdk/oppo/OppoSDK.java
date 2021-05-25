package com.u8.server.sdk.oppo;

import com.nearme.oauth.model.AccessToken;
import com.nearme.oauth.open.AccountAgent;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.Base64;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.HmacSHA1Encryption;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Oppo SDK
 * Created by ant on 2015/4/22.
 */
public class OppoSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try{

            JSONObject json = JSONObject.fromObject(extension);


            final String token = json.getString("token");
            final String ssoid = json.getString("ssoid");

            Map<String,String> params = new HashMap<String, String>();
            params.put("fileId", ssoid);
            params.put("token", token);

            Map<String,String> headers = new HashMap<String, String>();
            String baseStr = generateBaseString(channel.getCpAppKey(), token, System.currentTimeMillis()+"", System.currentTimeMillis()+"");
            headers.put("param", baseStr);
            String sign = generateSign(channel.getCpAppSecret(), baseStr);
            headers.put("oauthSignature", sign);

            Log.d("baseStr:"+baseStr);
            Log.d("sign:"+sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, headers, params, "UTF-8", new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {
                        Log.e("The auth result is " + result);

                        OppoBriefUser user = (OppoBriefUser)JsonUtils.decodeJson(result, OppoBriefUser.class);
                        if(user != null){

                            if (user.getResultCode() == 200) {

                                callback.onSuccess(new SDKVerifyResult(true, ssoid, user.getUserName(), ""));
                                return;

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);
                }

                @Override
                public void failed(String e) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });


        }catch (Exception e){
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }



    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }


    public static final String OAUTH_CONSUMER_KEY = "oauthConsumerKey";
    public static final String OAUTH_TOKEN = "oauthToken";
    public static final String OAUTH_SIGNATURE_METHOD = "oauthSignatureMethod"; public static final String OAUTH_SIGNATURE = "oauthSignature";
    public static final String OAUTH_TIMESTAMP = "oauthTimestamp";
    public static final String OAUTH_NONCE = "oauthNonce";
    public static final String OAUTH_VERSION = "oauthVersion";
    public static final String CONST_SIGNATURE_METHOD = "HMAC-SHA1";
    public static final String CONST_OAUTH_VERSION = "1.0";

    public static String generateBaseString(String appKey,String token, String timestamp,String nonce){
        StringBuilder sb = new StringBuilder(); try {
            sb.append(OAUTH_CONSUMER_KEY).
                    append("="). append(URLEncoder.encode(appKey, "UTF-8")). append("&").
                    append(OAUTH_TOKEN).
                    append("="). append(URLEncoder.encode(token,"UTF-8")). append("&").
                    append(OAUTH_SIGNATURE_METHOD).
                    append("="). append(URLEncoder.encode(CONST_SIGNATURE_METHOD,"UTF-8")). append("&").
                    append(OAUTH_TIMESTAMP).
                    append("="). append(URLEncoder.encode(timestamp,"UTF-8")). append("&").
                    append(OAUTH_NONCE).
                    append("=").
                    append(URLEncoder.encode(nonce,"UTF-8")).
                    append("&").
                    append(OAUTH_VERSION).
                    append("="). append(URLEncoder.encode(CONST_OAUTH_VERSION, "UTF-8")). append("&");
        } catch (UnsupportedEncodingException e1) { // TODO Auto-generated catch block e1.printStackTrace();
        }
        return sb.toString();
    }

    public static String generateSign(String appSecret, String baseStr) {
        byte[] byteHMAC = null;
        try {

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = null;
            String oauthSignature = encode(appSecret) + "&";
            spec = new
                    SecretKeySpec(oauthSignature.getBytes(), "HmacSHA1");
            mac.init(spec);
            byteHMAC = mac.doFinal(baseStr.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            return URLEncoder.encode(String.valueOf(Base64.encode(byteHMAC)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }

        return buf.toString();
    }
}
