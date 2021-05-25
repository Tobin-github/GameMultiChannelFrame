package com.u8.server.sdk.demo;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;

/**
 * 用户Demo测试的SDK处理类
 * Created by ant on 2016/4/8.
 */
public class DemoSDK implements ISDKScript {


    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

        //始终返回成功，相同的测试用户
        callback.onSuccess(new SDKVerifyResult(true, "demo0000001", extension, "demo_user"));

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
