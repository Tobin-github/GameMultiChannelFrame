package com.u8.server.sdk.WXandAli;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.sdk.wx.WXSDK;
import com.u8.server.service.UChannelLoginTypeManager;
import com.u8.server.service.UChannelPayTypeManager;
import com.u8.server.utils.RSAUtils;

import java.net.URLEncoder;

/**
 * 都玩（易乐）
 * Created by ant on 2016/9/1.
 */
public class WXAndAliSDK implements ISDKScriptExt {
    public static final String createOrderURL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String callBackURL = "/pay/wxpay/payCallback";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
//        JSONObject json = JSONObject.fromObject(extension);
//        final String openid = json.getString("openid");
//        final String openkey = json.getString("openkey");

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try{

        }
        catch (Exception e){

        }
        if(callback != null){
            callback.onSuccess("");
        }
    }

    public void verifyByType(UChannel channel, String extension, UChannelLoginTypeManager manager, ISDKVerifyListener callback){

    }

    //根据宝石风暴的参数 payType = 2 是为微信支付  payType = 1 是阿里支付
    public void onGetOrderIDByType(UUser user, UOrder order, int payType, UChannelPayTypeManager manager, ISDKOrderListener callback){
        if(payType==2){
            WXSDK.WxGetOrder(user, order,true, callback);
        }else  if(payType==1){
            //AliSDK.AliGetOrder(user,order,true,callback);
        }
    }

    /**
     * 支付宝签名
     * @param array
     * @return
     */
    private String signAllString(String [] array,final String key){
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < array.length; i++) {
            if(i==(array.length-1)){
                sb.append(array[i]);
            }else{
                sb.append(array[i]+"&");
            }
        }
        System.out.println(sb.toString());
        String sign = "";
        try {
            String rsaStirng = RSAUtils.sign(sb.toString(), key, "UTF-8", "SHA1withRSA");
            sign = URLEncoder.encode(rsaStirng, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("&sign=\""+sign+"\"&");
        sb.append("sign_type=\"RSA\"");
        return sb.toString();
    }


}
