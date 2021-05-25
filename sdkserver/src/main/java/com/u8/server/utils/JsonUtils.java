package com.u8.server.utils;

import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import net.sf.json.JSONObject;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;


public class JsonUtils {

    public static ObjectMapper objectMapper = new ObjectMapper();
    static{
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setDeserializationConfig(objectMapper.getDeserializationConfig().without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES));

    }

    public static Object decodeJson(String json, Class pojoClass){
        try{
            return objectMapper.readValue(json, pojoClass);
        }catch (Exception e){
            Log.e(e.getMessage());
        }

        return null;
    }

    public static String encodeJson(Object o){
        try{

            return objectMapper.writeValueAsString(o);

        }catch (Exception e){
            Log.e(e.getMessage());
        }

        return null;
    }

    public static JSONObject map2Json(Map<String, Object> data){

        if(data == null || data.size() == 0){
            return new JSONObject();
        }

        JSONObject json = new JSONObject();

        Iterator<String> keyItor = data.keySet().iterator();
        while(keyItor.hasNext()){
            String key = keyItor.next();
            json.put(key, data.get(key));
        }

        return json;
    }

    public static String map2JsonStr(Map<String, Object> data){

        return map2Json(data).toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try{
            String result = "{\"UID\":573570309,\"MerchandiseName\":\"10钻石\",\"OrderMoney\":\"1.00\",\"StartDateTime\":\"2015-12-23 15:49:57\","+
                    "\"BankDateTime\":\"2015-12-23 15:50:10\",\"OrderStatus\":1,\"StatusMsg\":\"成功\",\"ExtInfo\":\"\",\"VoucherMoney\":0}";

            System.out.println("------result="+result);
            BaiduPayResult payResult = (BaiduPayResult) JsonUtils.decodeJson(result, BaiduPayResult.class);

            System.out.println("------payResult status="+payResult.getOrderStatus());
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
