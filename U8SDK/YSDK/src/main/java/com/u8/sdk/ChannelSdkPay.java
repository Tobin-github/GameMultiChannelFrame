package com.u8.sdk;

import android.app.Activity;

public class ChannelSdkPay  implements IPay {
    private Activity context;

    public ChannelSdkPay(Activity context) {
        this.context = context;
    }

    public void pay(PayParams data) {
        ChannelSDK.pay(data);
    }

    public boolean isSupportMethod(String methodName) {
        return true;
    }
}