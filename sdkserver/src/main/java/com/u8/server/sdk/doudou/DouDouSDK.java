package com.u8.server.sdk.doudou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 逗逗SDK
 * Created by ant on 2015/12/8.
 */

public class DouDouSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String userID = json.getString("userID");
            final String ticket = json.getString("ticket");

            String timestamp = (System.currentTimeMillis() / 1000)+"";
            String sequence = System.currentTimeMillis()+userID;

            Map<String,String> params = new HashMap<String, String>();
            params.put("appid", channel.getCpAppID());
            params.put("timestamp", timestamp);
            params.put("sequence", sequence);
            params.put("ticket_id", ticket);

            StringBuilder sb = new StringBuilder();
            sb.append("appid=").append(channel.getCpAppID()).append("&sequence=").append(sequence)
                    .append("&ticket_id=").append(ticket).append("&timestamp=").append(timestamp)
                    .append(channel.getCpAppSecret());

            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            Log.d("the sign is : " + sign);

            params.put("sign", sign);

            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        Log.d("The result of lewan verify is :" + result);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jr = JSONObject.fromObject(result);
                            int code = jr.getInt("code");

                            if(code == 0){

                                JSONObject data = jr.getJSONObject("data");
                                callback.onSuccess(new SDKVerifyResult(true, data.getString("userid"), data.getString("phone"), ""));
                                return;
                            }


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
                }

                @Override
                public void failed(String err) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
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
            callback.onSuccess("");
        }
    }
}
