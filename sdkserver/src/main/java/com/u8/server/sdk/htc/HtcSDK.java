package com.u8.server.sdk.htc;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import net.sf.json.JSONObject;

public class HtcSDK implements ISDKScript {
    private static String CHARSET = "utf-8";

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try {

            JSONObject json = JSONObject.fromObject(extension);

            final String account = json.getString("account");
            final String accountSign = json.getString("accountSign");
            final String userId = json.getString("userId");
            final String userName = json.getString("userName");

            Log.d("The result of HtcSDK verify is success");
            callback.onSuccess(new SDKVerifyResult(true, userId, userName, ""));

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        try {
            String Amount= order.getMoney()+"";
            String GameCode = order.getChannel().getCpAppID();
            String GameName = order.getChannel().getCpConfig();
            String GameOrderid = order.getOrderID()+"";
            String NotifyUrl = UHttpAgent.ServerHost + "/pay/htc/payCallback";
            String ProductDes = order.getProductName();
            String ProductID = order.getProductID();
            String ProductName = order.getProductName();

            JSONObject json = new JSONObject();
            json.put("Amount",Amount);
            json.put("GameCode",GameCode);
            json.put("GameName",GameName);
            json.put("GameOrderid",GameOrderid);
            json.put("NotifyUrl",NotifyUrl);
            json.put("ProductDes",ProductDes);
            json.put("ProductID",ProductID);
            json.put("ProductName",ProductName);

            callback.onSuccess(json.toString());
            Log.i("get order extension success:" + json.toString());


        } catch (Exception e) {
            callback.onFailed(e.getMessage());
            Log.e("get order extension fail:" + e.getMessage());
            e.printStackTrace();
        }
    }


}
