package com.u8.server.sdk.baidu;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.U8OrderIDHexUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * 百度SDK，采用keep-alive方式，发送一个请求之后，过一段时间再次发送请求，会抛出一个SocketException异常：
 *     java.net.SocketException: Connection reset by peer: socket write error
 * 初步判断，是因为使用了Keep-Alive方式，服务器端有超时时间。过一段时间访问，服务器判断链接失效，无法继续写入数据
 * 所以，在UHttpClient中重试机制中，增加了这个异常类型，这样出现这个异常时，自动重试一次
 * Created by ant on 2015/2/28.
 */
public class SingleBaiduSDK implements ISDKScript {

    private static Logger log = Logger.getLogger(SingleBaiduSDK.class.getName());

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{
            log.info("------------>auth result:"+extension);

            if (callback != null) {
                SDKVerifyResult vResult = new SDKVerifyResult(true, extension + "", "", "");

                callback.onSuccess(vResult);
                return;
            }

        }catch (Exception e){
            log.error("------------>auth exception-2:"+e.getMessage());
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback){
        if(callback != null){

            JSONObject json = new JSONObject();

            String encodeOrderId = U8OrderIDHexUtils.encode(order.getOrderID());

            json.put("orderID", encodeOrderId);
            log.info("------------>getOrderId json:"+json.toString());

            callback.onSuccess(json.toString());
        }
    }

    private boolean isValid(UChannel channel, BaiduResponse res) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(channel.getCpAppID()).append(res.getResultCode())
                .append(URLDecoder.decode(res.getContent(), "utf-8"))
                .append(channel.getCpAppSecret());
        log.info("------------>auth unSignStr:"+sb.toString());

        String sign1 = EncryptUtils.md5(sb.toString()).toLowerCase();
        log.info("------------>auth mySign:"+sign1+", channelSign:"+res.getSign().toLowerCase());
        return sign1.equals(res.getSign().toLowerCase());
    }

    private ByteArrayEntity encodeParams(UChannel channel, String accessToken){

        String sign = EncryptUtils.md5(channel.getCpAppID()+accessToken+channel.getCpAppSecret());

        StringBuilder param = new StringBuilder();
        param.append("AppID=");
        param.append(channel.getCpAppID());
        param.append("&AccessToken=");
        param.append(accessToken);
        param.append("&Sign=");
        param.append(sign.toLowerCase());

        String s = param.toString();

        return new ByteArrayEntity(s.getBytes(Charset.forName("UTF-8")));

    }
}
