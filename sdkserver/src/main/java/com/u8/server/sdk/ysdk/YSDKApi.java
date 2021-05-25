package com.u8.server.sdk.ysdk;

import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.log.Log;
import com.u8.server.sdk.ysdk.api.OpenApiV3;
import com.u8.server.utils.IDGenerator;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用宝YSDK相关处理API
 * Created by xiaohei on 16/5/6.
 */
public class YSDKApi {

    //查询余额
    public static JSONObject queryMoney(PayRequest payRequest){

        try{

            UChannel channel = payRequest.getUser().getChannel();
            if(channel == null){
                Log.e("the channel is not exists of user:"+payRequest.getUser().getChannelID());
                return null;
            }

            String url = channel.getChannelOrderUrl();
            String scriptName = "/mpay/get_balance_m";

            Map<String,String> params = new HashMap<String, String>();

            params.put("openid", payRequest.getOpenID());
            params.put("openkey", payRequest.getOpenKey());


            params.put("ts", Long.toString(System.currentTimeMillis() / 1000));
            params.put("pf", payRequest.getPf());
            params.put("pfkey", payRequest.getPfkey());
            params.put("zoneid", payRequest.getZoneid());

            String urlPath = "/v3/r"+scriptName;

            String resp = OpenApiV3.api_pay(url, urlPath, channel.getCpAppID(), channel.getCpPayID(), payRequest.getAccountType(), params);

            JSONObject json = JSONObject.fromObject(resp);
            int ret = json.getInt("ret");
            if(ret == 0){

                return json;
            }

        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    //扣费,返回交易流水号
    public static JSONObject charge(PayRequest payRequest){
        try{

            UChannel channel = payRequest.getUser().getChannel();
            if(channel == null){
                Log.e("the channel is not exists of user:"+payRequest.getUser().getChannelID());
                return null;
            }

            String url = channel.getChannelOrderUrl();
            String scriptName = "/mpay/pay_m";

            Map<String,String> params = new HashMap<String, String>();

            params.put("openid", payRequest.getOpenID());
            params.put("openkey", payRequest.getOpenKey());


            params.put("ts", Long.toString(System.currentTimeMillis() / 1000));
            params.put("pf", payRequest.getPf());
            params.put("pfkey", payRequest.getPfkey());
            params.put("zoneid", payRequest.getZoneid());
            params.put("amt", payRequest.getCoinNum()+"");
            params.put("billno", payRequest.getOrder().getOrderID()+"");

            String urlPath = "/v3/r"+scriptName;

            String resp = OpenApiV3.api_pay(url, urlPath, channel.getCpAppID(), channel.getCpPayID(), payRequest.getAccountType(), params);

            JSONObject json = JSONObject.fromObject(resp);
            int ret = json.getInt("ret");
            if(ret == 0){

                return json;
            }else{
                Log.e("charge from ysdk failed:"+resp);
            }

        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
