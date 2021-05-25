package com.u8.server.sdk.anfeng;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 安峰渠道SDK(56Game)
 * Created by ant on 2015/12/3.
 */
public class AnFengSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            String uuid = json.getString("uuid");
            final String ucid = json.getString("ucid");

            UHttpAgent httpClient = UHttpAgent.getInstance();

            Map<String,String> params = new HashMap<String, String>();
            params.put("uid",uid);
            params.put("ucid",ucid);
            params.put("uuid",uuid);
            params.put("appId",channel.getCpAppID());
            String sign = generateSign(params, channel.getCpAppKey());

            params.put("sign", sign);

            httpClient.post(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    Log.d("The auth result is "+result);

                    JSONObject json = JSONObject.fromObject(result);
                    if(!json.containsKey("returnCode") || "0".equals(json.getString("returnCode"))){

                        callback.onSuccess(new SDKVerifyResult(true, ucid, uid, ""));
                        return;
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);

                }

                @Override
                public void failed(String e) {

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }


            });


        }catch (Exception e){
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            Log.e(e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }

    //生成sign
    public static String generateSign(Map<String, String> params, String signKey){

		/*首先以key值自然排序,生成key1=val1&key2=val2......&keyN=valN格式的字符串*/
        List<String> keys=new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder postdatasb=new StringBuilder();
        for(int i=0;i<keys.size();i++){
            String k=keys.get(i);
            String v=params.get(k);
            postdatasb.append(k+"="+v+"&");
        }
        postdatasb.deleteCharAt(postdatasb.length()-1);
        //对排序后的参数附加开发商签名密钥
        postdatasb.append("&signKey="+signKey);
        String sign= md5(postdatasb.toString().getBytes());

        Log.d("the sign data is "+postdatasb.toString());

        return sign;
    }

    public static  String md5(byte[] source) {
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
            // 进制需要 32 个字符
            int k = 0;// 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
                // 进制字符的转换
                byte byte0 = tmp[i];// 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

            }
            s = new String(str);// 换后的结果转换为字符串

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }
}
