import com.u8.server.data.UOrder;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.iiugame.MD5Signature;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author kris
 * @create 2017-12-07 11:59
 */
public class LogTest {

    public static final String LOG_ORDER_URL = "http://120.77.51.159:5050/logapi/pay/";

    @Ignore
    @Test
    public void testLog() {
        String content = "";

        String md5Str = EncryptUtils.md5(content);
        System.out.println(md5Str);

        String Price = "6.99";
        int money = (int) (Math.round(Double.parseDouble(Price) * 100));
        System.out.println(money);
        System.out.println(Math.round(Double.parseDouble(Price) * 100));

        /*String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALLtY5RUjc/vbv5o skbFVL4jPsybZH5JDF0Z9yS+0PEqBDVbv/2cT7te+QvnFDxLYfZsJ3C1fCXC/eUt ACr58K9WycbfjS4TefKjuJ1zsecgGvlr1/yNKI/h+qojQqkPRsAy9sf64JLUDEsJ koei5mf3fO7HTKrDk9/ZjJP/4txfAgMBAAECgYAsYQLk5H/0BiASi+dD9GfLbTSD TTuEsxuU7/7Dp2xtlI1MqsAY6C9CexDsadk0B0J5+2Ng6jKrrwyITjwPbMTMXgFV RgvVxD8ak6C40vCeFW1DX/cZWX6+EhUiLcphZHmmARQl/A05pKkdmBUnPoFTOuQt BlzlCxvy/8cUODodeQJBAOirZWD2ZZZ1/oYUc3I5MB1oFN29HP4O9VnAUAyYEGIS 3tK49WwtpILF2zN9sFrdeFQSSLt84+pLMW2mZBfD+WUCQQDE3m99R7XbIi2+3/S3 aayZiYn+p9ZuUyxOwbHX2b76/H/Ut/yKhaxQsjwZAL11WgAXt3ErExzy0CsUQTdl /0RzAkEAvL4tY1b+WewmKUZ10Hcr3O8N3kMHPeGMjt4/EbhZsfV05KQ+Ex+DkMJI a6DOaye18R1T+yP5D5sn4bdVAyJrMQJBAIbFg+tu6RcP6eMuq/fX1vnxB1AXFBvI wp9TyGztOunVa6lzHuaxpgpEShIPyKOKrl+ODGIhnoK3cZZXIxanTvMCQQCBdeTP BoaU/w1zQbEKxO8yVOYgPm98Nzo1N5vt+osWC37X02Jcj19TgyQkqQHa264mBguL 5eNfBuP7vQuLtM9Y";
        String unSignParams = "partner=\"2088611034098873\"&seller_id=\"songjh@bzw.cn\"&out_trade_no=\"AO2017080409494630486714557246\"&subject=\"120000点数\"&body=\"120000点数\"&total_fee=\"12.00\"&notify_url=\"http://218.60.113.182:8014/Third/Notify/AlipayNotify/1\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"";
        String unEncode= RSAUtils.sign(unSignParams, privateKey, "UTF-8", "SHA1withRSA");
        System.out.println(unEncode);*/

        /*String transdata = "{\"exorderno\":\"1462306536016576516\",\"transid\":\"T3118010518321187789\",\"waresid\":2,\"appid\":\"5000009189\",\"feetype\":0,\"money\":100,\"count\":1,\"result\":0,\"transtype\":0,\"transtime\":\"2018-01-05 18:32:41\",\"cpprivate\":\"1462306536016576516\",\"paytype\":403}";
        String sign = "";
        String payKey = "";

        CpTransSyncSignValid.validSign(transdata, sign, payKey);*/
    }

    @Ignore
    @Test
    public void testLog2() {
        /*String s1 = EncryptUtils.md5("6USD2018010418541834249920.991201151506325810024120c949a5d30f1a344ca4b86cf8f781f6da");
        System.out.println(s1);*/

        String sign = "5ab2e01baa21d70dc08edf83c9483fa1";
        String sercerKey = "c949a5d30f1a344ca4b86cf8f781f6da";

        String Cp_orderid = "";
        String Ctext = "";
        String sPcText = "";
        String Ugameid = "10024";
        String Uid = "120";
        String Roleid = "120";
        String Serverid = "1";
        String Pay_channel = "2";
        String Orderid = "20180104175935650910";
        String Time = "1515059975";
        String Amount = "6";
        String Price = "0.99";
        String Currency_type = "USD";


//sign验证

        Map<String, String> params = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return obj1.compareTo(obj2);
                    }
                });

        if (StringUtils.isNotEmpty(Cp_orderid)) {
            params.put("Cp_orderid", Cp_orderid);
        }

        if (StringUtils.isNotEmpty(Ctext)) {
            params.put("Ctext", Ctext);
        }

        if (StringUtils.isNotEmpty(sPcText)) {
            params.put("sPcText", sPcText);
        }

        params.put("Ugameid", Ugameid);
        params.put("Uid", Uid);
        params.put("Roleid", Roleid);
        params.put("Serverid", Serverid);
        params.put("Pay_channel", Pay_channel);
        params.put("Orderid", Orderid);
        params.put("Cp_orderid", Cp_orderid);
        params.put("Time", Time);
        params.put("Amount", Amount);
        params.put("Price", Price);
        params.put("Currency_type", Currency_type);

        String stringData = getStringData(params,sign);
        String signLocal = null;
        try {
            signLocal = MD5Signature.sign(stringData, sercerKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //签名验证
        if (!signLocal.equals(sign.toLowerCase())) {
            System.out.println("false");
        } else {
            System.out.println("true");
        }
        /*Map<String,Object> paramsMap = new TreeMap<>();
        paramsMap.put("order_id","1");
        paramsMap.put("merchant_order_id","1");
        paramsMap.put("amount","1");
        paramsMap.put("app_id","1");
        paramsMap.put("pay_time","1");
        paramsMap.put("attach","1");
        paramsMap.put("status","1");

        StringBuilder sb = new StringBuilder();

        for (Map.Entry param:
                paramsMap.entrySet()) {
            sb.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        sb.append("key").append("=").append("5555");

        System.out.println(sb.toString());*/

                /*append("attachInfo").append("=").append("custominfo=xxxxx#user=xxxx").
                append("failedDesc").append("=").append("").
                append("gameId").append("=").append(123).
                append("orderId").append("=").append("1234567").
                append("orderStatus").append("=").append("S").
                append("payType").append("=").append("999").
                append("tradeId").append("=").append("abcf1330").
                append("tradeTime").append("=").append("20150527130000").
                append("202cb962234w4ers2aaa");*/

        /*String sign = EncryptUtils.md5(sb.toString());
        String sign2 = EncryptUtils.md5("amount=100.00attachInfo=custominfo=xxxxx#user=xxxxfailedDesc=gameId=123orderId=abcf1330orderStatus=SpayType=999tradeId=abcf1330tradeTime=20150527130000202cb962234w4ers2aaa");
        System.out.println(sign);
        System.out.println(sign2);*/
    }

    @Ignore
    @Test
    public void testLog3() {
        //42USD2018010818101222409126.991201151540621210024120AO20180108181012553885550006
        //42USD2018010818101222409126.991201151540621210024120

        String sign = "9eff988be6263e9baf90455e93cd9e6a";
        String sercerKey = "c949a5d30f1a344ca4b86cf8f781f6da";

        String Cp_orderid = "";
        String Ctext = "";
        String sPcText = "AO20180108181012553885550006";
        String Ugameid = "10024";
        String Uid = "120";
        String Roleid = "120";
        String Serverid = "1";
        String Pay_channel = "2";
        String Orderid = "20180108181012224091";
        String Time = "1515406212";
        String Amount = "42";
        String Price = "6.99";
        String Currency_type = "USD";


//sign验证

        Map<String, String> params = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 降序排序
                        return obj1.compareTo(obj2);
                    }
                });

        if (StringUtils.isNotEmpty(Cp_orderid)) {
            params.put("Cp_orderid", Cp_orderid);
        }

        if (StringUtils.isNotEmpty(Ctext)) {
            params.put("Ctext", Ctext);
        }

        if (StringUtils.isNotEmpty(sPcText)) {
            params.put("sPcText", sPcText);
        }

        params.put("Ugameid", Ugameid);
        params.put("Uid", Uid);
        params.put("Roleid", Roleid);
        params.put("Serverid", Serverid);
        params.put("Pay_channel", Pay_channel);
        params.put("Orderid", Orderid);
        params.put("Cp_orderid", Cp_orderid);
        params.put("Time", Time);
        params.put("Amount", Amount);
        params.put("Price", Price);
        params.put("Currency_type", Currency_type);

        String stringData = getStringData(params,sign);
        System.out.println("1111:"+stringData);
        String signLocal = null;
        try {
            signLocal = MD5Signature.sign(stringData, sercerKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //签名验证
        if (!signLocal.equals(sign.toLowerCase())) {
            System.out.println("false");
        } else {
            System.out.println("true");
        }
        /*Map<String,Object> paramsMap = new TreeMap<>();
        paramsMap.put("order_id","1");
        paramsMap.put("merchant_order_id","1");
        paramsMap.put("amount","1");
        paramsMap.put("app_id","1");
        paramsMap.put("pay_time","1");
        paramsMap.put("attach","1");
        paramsMap.put("status","1");

        StringBuilder sb = new StringBuilder();

        for (Map.Entry param:
                paramsMap.entrySet()) {
            sb.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        sb.append("key").append("=").append("5555");

        System.out.println(sb.toString());*/

                /*append("attachInfo").append("=").append("custominfo=xxxxx#user=xxxx").
                append("failedDesc").append("=").append("").
                append("gameId").append("=").append(123).
                append("orderId").append("=").append("1234567").
                append("orderStatus").append("=").append("S").
                append("payType").append("=").append("999").
                append("tradeId").append("=").append("abcf1330").
                append("tradeTime").append("=").append("20150527130000").
                append("202cb962234w4ers2aaa");*/

        /*String sign = EncryptUtils.md5(sb.toString());
        String sign2 = EncryptUtils.md5("amount=100.00attachInfo=custominfo=xxxxx#user=xxxxfailedDesc=gameId=123orderId=abcf1330orderStatus=SpayType=999tradeId=abcf1330tradeTime=20150527130000202cb962234w4ers2aaa");
        System.out.println(sign);
        System.out.println(sign2);*/
    }

    //md5算法生成
    public static String md5Sign(String param) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(param.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//Log.i(StringUtil.Log,"sign= "+re_md5);
        return re_md5;
    }
    /*
    接收所有的参数排序并且拼接成字符串
    */
    public static String getToString(Map<String,String> params){
        StringBuffer buffer=new StringBuffer();
        List<String> keys=new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for(int i=0;i<keys.size();i++){
            String key=keys.get(i);
            if(key.equals("Sign")){
                continue;
            }
            String value=(String)params.get(key);
            if(value!=null&&value.length()>0){
                buffer.append(value);
            }
        }
        return buffer.toString();
    }
    /*
    生成签名的算法
    */
    public static String createSign(String content,String Key){
        String tosign = (content == null ? "" : content) + Key;
        return md5Sign(tosign);
    }

    private String getStringData(Map<String, String> params,String sign) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i <keys.size() ; i++) {
            String key = keys.get(i);
            if (key.equals(sign)) {
                continue;
            }
            String value = params.get(key);
            if (value != null && value.length() > 0) {
                content.append(value);
            }
        }
        return content.toString();

    }

    public static String logOrderData(UOrder order,String cid,String secretKey) {

        JSONArray jsonObject = new JSONArray();

        DecimalFormat df=new DecimalFormat("0.00");
        String orderMoney =df.format(order.getMoney()/100.00);
        Integer platID = 1;

        Map map = new HashMap();

        /*map.put("cid",cid);
        map.put("user_id",1);
        map.put("order_id","10086");
        map.put("pay_num",1.20);
        map.put("pay_time",10086);
        map.put("imei","hiahia");
        map.put("platform_type","0");
        map.put("channel",0);
        map.put("agent","zyl");
        map.put("user_name","kris");
        map.put("user_level",11);
        map.put("vip_level",22);
        map.put("ip","heihei");
        map.put("ua","aaaaa");
        map.put("pay_channel","0");*/

        map.put("cid",cid);
        map.put("user_id",order.getUserID());
        map.put("order_id",order.getOrderID()+"");
        map.put("pay_num",orderMoney);
        map.put("pay_time",System.currentTimeMillis()/1000);
        map.put("imei","");
        map.put("platform_type","0");
        map.put("channel",order.getChannelID());
        map.put("agent","zyl");
        map.put("user_name",order.getUsername());
        map.put("user_level",0);
        map.put("vip_level",0);
        map.put("ip","");
        map.put("ua","");
        map.put("pay_channel",0);

        jsonObject.add(map);

        JSONObject params = new JSONObject();
        params.put("cid", cid);
        params.put("secret_key", secretKey);
        params.put("data", jsonObject);

        System.out.println(params.toString());

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        String orderResult = UHttpAgent.getInstance().post(LOG_ORDER_URL, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));

        JSONObject json = JSONObject.fromObject(orderResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code)&&!"401".equals(code)) {
            return "";
        }

        String infoData = json.getString("info");//token

        return infoData;

    }
}
