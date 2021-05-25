package com.u8.server.sdk.quanmingzhushou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.sdk.quanmingzhushou.data.GameSdkJsonChkSessionResponse;
import com.u8.server.sdk.quanmingzhushou.data.GameSdkJsonPackage;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 全民助手SDK
 * Created by ant on 2016/5/25.
 */
public class QMSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject ext = JSONObject.fromObject(extension);
            String sid = ext.getString("sid");


            UHttpAgent httpClient = UHttpAgent.getInstance();


            String id = String.valueOf(System.nanoTime());
            String ver = "1.1";
            String devCode = channel.getCpID();
            String encodedData = encodeData(channel, sid);
            String actionId = "chksession";

            StringBuilder sb = new StringBuilder();
            sb.append("actionId=").append(actionId)
                    .append("data=").append(encodedData)
                    .append("devCode=").append(devCode)
                    .append("id=").append(id)
                    .append("ver=").append(ver).append(channel.getCpAppKey());

            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", id);
            data.put("ver", ver);
            data.put("devCode", devCode);
            data.put("actionId", actionId);
            data.put("data", encodedData);
            data.put("sign", sign);

            String url = channel.getChannelAuthUrl();


            String jsonData = JsonUtils.encodeJson(data);

            Log.d("QMSDK data to send:"+jsonData);

            Map<String,String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF8");

            httpClient.post(url, headers, new ByteArrayEntity(jsonData.getBytes(Charset.forName("UTF-8"))), new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try{

                        GameSdkJsonPackage rsp = (GameSdkJsonPackage) JsonUtils.decodeJson(result, GameSdkJsonPackage.class);
                        if(rsp != null){
                            String respStr = ThreeDesUtil.thressDesDecrypt(channel.getCpAppSecret(), rsp.getData());
                            Log.d("QMSDK resp data:"+respStr);
                            GameSdkJsonChkSessionResponse sessionResponse = (GameSdkJsonChkSessionResponse)JsonUtils.decodeJson(respStr, GameSdkJsonChkSessionResponse.class);
                            if(sessionResponse != null){
                                if(sessionResponse.getState().getCode() == 2000){
                                    SDKVerifyResult verifyResult = new SDKVerifyResult(true, sessionResponse.getData().getAccountId(), sessionResponse.getData().getAccountId(), sessionResponse.getData().getNickName());
                                    callback.onSuccess(verifyResult);
                                    return;
                                }
                            }
                        }


                    }catch (Exception e){
                        Log.e("QMSDK parse response error", e);
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
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            Log.e(e.getMessage());
        }
    }

    private String encodeData(UChannel channel, String sid){
        try{

            JSONObject json = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("sId", sid);
            json.put("data", data);
            JSONObject gameInfo = new JSONObject();
            gameInfo.put("gameId", channel.getCpAppID());
            json.put("gameInfo", gameInfo);
            JSONObject extData = new JSONObject();
            extData.put("dataA", "null");
            extData.put("dataB", "null");
            extData.put("dataC", "null");
            extData.put("dataD", "null");
            extData.put("dataE", "null");
            extData.put("dataF", "null");
            json.put("extData", extData);

            String finalData = json.toString();

            return ThreeDesUtil.thressDesEncrypt(channel.getCpAppSecret(), finalData);

        }catch(Exception e){
            Log.e("exception when encode data", e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }


}
