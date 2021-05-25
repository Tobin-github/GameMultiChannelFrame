package com.u8.server.service;

import com.u8.server.dao.UStatisticsDao;
import com.u8.server.data.*;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service("logDataManager")
public class LogDataManager {

    @Autowired
    private UStatisticsDao mStatisticsDao;
    /*@Autowired
    private UInterfaceConfigDao mInterfaceConfigDao;*/

    @Autowired
    private UPayTypeManager mPayTypeManager;

    @Value("${LOG_TOKEN_URL}")
    private String LOG_TOKEN_URL;
    @Value("${LOG_REGISTER_URL}")
    private String LOG_REGISTER_URL;
    @Value("${LOG_LOGIN_URL}")
    private String LOG_LOGIN_URL;
    @Value("${LOG_ORDER_URL}")
    private String LOG_ORDER_URL;
    /*@Value("${LOG_CHANNEL_URL}")
    private String LOG_CHANNEL_URL;*/

    private static String token;

    private static Logger log = Logger.getLogger(LogDataManager.class.getName());

    public UStatistics getStatisticsByAppId(int appId) {
        String hql = "from UStatistics where GameId = ?";
        return (UStatistics) mStatisticsDao.findUnique(hql, appId);
    }

    /*public UInterfaceConfig getInterfaceConfigByUrlName(String urlName) {
        String hql = "from UInterfaceConfig where UrlName = ?";

        return (UInterfaceConfig)mInterfaceConfigDao.findUnique(hql, urlName);
    }*/

    public String getLogToken(String cid, String username, String password) {

        JSONObject params = new JSONObject();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("cid", cid);
        jsonObject.put("username", username);
        jsonObject.put("passwd", password);

        params.put("data", jsonObject);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        log.info("---------->LOG_TOKEN_URL getToken params:" + params.toString());

        String tokenResult = UHttpAgent.getInstance().post(LOG_TOKEN_URL, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));

        log.info("---------->LOG_TOKEN_URL getToken result:" + tokenResult);
        JSONObject json = JSONObject.fromObject(tokenResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code)) {
            log.error("---------->LOG_TOKEN_URL getToken fail:" + tokenResult);
            return "";
        }

        String token = json.getString("info");//token

        return token;
    }

    public String logChannelData(String cid, UChannel channel, String secretKey) {

        /*for (UChannel channel :
                uChannels) {*/
        log.info("----------------->logChannelData start");

        JSONArray jsonObject = new JSONArray();

        Map map = new HashMap();

        map.put("cid", cid);
        map.put("channel_id", channel.getChannelID());
        map.put("channel_name", channel.getMaster().getMasterName()+"_"+channel.getCpConfig());
        //map.put("desc", channel.getMaster().getSdkName());

        jsonObject.add(map);

        JSONObject params = new JSONObject();
        params.put("cid", cid);
        params.put("secret_key", secretKey);
        params.put("data", jsonObject);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        log.info("----------------->logChannelData :" + params.toString());

        String s = JsonUtils.encodeJson(params);

         String orderResult = UHttpAgent.getInstance().post("", headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));
        //String orderResult = "";

        log.info("------------->logChannelData result:" + orderResult);
        JSONObject json = JSONObject.fromObject(orderResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code) && !"401".equals(code)) {
            log.error("--------------> logRegister fail:" + orderResult);
            return "401";
        }

        return code;
        //}

    }

    public String logRegisterData(String cid, UUser user, String secretKey) {

        log.info("----------------->logRegisterData start");

        JSONArray jsonObject = new JSONArray();

        Map map = new HashMap();

        Integer platID = user.getChannel().getPlatID();

        map.put("cid", cid);
        map.put("user_id", user.getId());
        map.put("register_time", System.currentTimeMillis() / 1000 + "");
        map.put("imei", "");
        map.put("platform_type", platID == 1 ? "0" : "1");
        map.put("channel", user.getChannelID());
        map.put("agent", user.getChannel().getMaster().getMasterName());
        map.put("ua", "");
        map.put("ip", "");

        jsonObject.add(map);

        JSONObject params = new JSONObject();
        params.put("cid", cid);
        params.put("secret_key", secretKey);
        params.put("data", jsonObject);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        log.info("----------------->logRegisterData :" + params.toString());

        String orderResult = UHttpAgent.getInstance().post(LOG_REGISTER_URL, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));

        log.info("------------->LOG_REGISTER_URL logRegister result:" + orderResult);
        JSONObject json = JSONObject.fromObject(orderResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code) && !"401".equals(code)) {
            log.error("-------------->LOG_REGISTER_URL logRegister fail:" + orderResult);
            return "401";
        }

        return code;

    }

    public String logLoginData(String cid, UUser user, String secretKey) {

        log.info("----------------->logLoginData start");

        JSONArray jsonObject = new JSONArray();

        Map map = new HashMap();

        Integer platID = user.getChannel().getPlatID();

        map.put("cid", cid);
        map.put("user_id", user.getId());
        map.put("login_time", System.currentTimeMillis() / 1000 + "");
        map.put("imei", "");
        map.put("platform_type", platID == 1 ? "0" : "1");
        map.put("channel", user.getChannelID());
        map.put("agent", user.getChannel().getMaster().getMasterName());
        map.put("user_name", user.getChannelUserName() == null ? "" : user.getChannelUserName());
        map.put("user_level", 0);
        map.put("vip_level", 0);
        map.put("ua", "");
        map.put("ip", "");

        jsonObject.add(map);

        JSONObject params = new JSONObject();
        params.put("cid", cid);
        params.put("secret_key", secretKey);
        params.put("data", jsonObject);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        log.info("----------------->logLoginData :" + params.toString());

        String orderResult = UHttpAgent.getInstance().post(LOG_LOGIN_URL, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));

        log.info("------------->LOG_LOGIN_URL logLogin result:" + orderResult);
        JSONObject json = JSONObject.fromObject(orderResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code) && !"401".equals(code)) {
            log.error("-------------->LOG_LOGIN_URL logOrder fail:" + orderResult);
            return "401";
        }

        return code;

    }

    public String logOrderData(String cid, UOrder order, String secretKey) {

        /*ApplicationContext applicationContext = UApplicationContext.getApplicationContext();
        UPayTypeManager payTypeManage= (UPayTypeManager) applicationContext.getBean("payTypeManager");*/
        log.info("----------------->uPayType logOrder start");
        UPayType uPayType = mPayTypeManager.getPayTypeByTypeId(order.getPayType());

        if (null == uPayType) {
            log.error("----------------->uPayType is null,maybe order's payType is null");
        } else {
            log.info("----------------->uPayType logOrder result:" + uPayType.toString());
        }

        JSONArray jsonObject = new JSONArray();

        DecimalFormat df = new DecimalFormat("0.00");
        String orderMoney = df.format(order.getMoney() / 100.00);
        Integer platID = order.getChannel().getPlatID();

        Map map = new HashMap();

        map.put("cid", cid);
        map.put("user_id", order.getUserID());
        map.put("order_id", order.getOrderID() + "");
        map.put("pay_num", orderMoney);
        map.put("pay_time", System.currentTimeMillis() / 1000);
        map.put("imei", "");
        map.put("platform_type", platID == 1 ? "0" : "1");
        map.put("channel", order.getChannelID());
        map.put("agent", order.getChannel().getMaster().getMasterName());
        map.put("user_name", order.getUsername());
        map.put("user_level", 0);
        map.put("vip_level", 0);
        map.put("ip", "");
        map.put("ua", "");
        map.put("pay_channel", uPayType.getPayTypeName());

        jsonObject.add(map);

        JSONObject params = new JSONObject();
        params.put("cid", cid);
        params.put("secret_key", secretKey);
        params.put("data", jsonObject);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        log.info("----------------->logOrderData :" + params.toString());

        String orderResult = UHttpAgent.getInstance().post(LOG_ORDER_URL, headers, new ByteArrayEntity(params.toString().getBytes(Charset.forName("UTF-8"))));

        log.info("LOG_ORDER_URL logOrder result:" + orderResult);
        JSONObject json = JSONObject.fromObject(orderResult);
        String code = json.getString("code");//状态码

        if (!"200".equals(code) && !"401".equals(code)) {
            log.error("LOG_ORDER_URL logOrder fail:" + orderResult);
            return "401";
        }

        return code;

    }

    public static String getToken() {
        return token;
    }

    public synchronized static void setToken(String token2) {
        token = token2;
    }
}
