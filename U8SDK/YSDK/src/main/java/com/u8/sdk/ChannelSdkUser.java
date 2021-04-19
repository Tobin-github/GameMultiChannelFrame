package com.u8.sdk;

import android.app.Activity;

import com.tencent.connect.common.Constants;
import com.u8.sdk.utils.Arrays;

public class ChannelSdkUser extends U8UserAdapter {
    private String[] supportedMethods = new String[]{"login", "loginCustom", "switchLogin", "logout"};

    public ChannelSdkUser(Activity context) {
        try {
            ChannelSDK.initSDK(U8SDK.getInstance().getSDKParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {
        if (ChannelSDK.useLogin) {
            ChannelSDK.login();
        }
    }

    public void loginCustom(String customData) {
        if (!ChannelSDK.useLogin) {
            return;
        }
        if (Constants.SOURCE_QQ.equalsIgnoreCase(customData)) {
//            ChannelSDK.login(1);
        } else {
//            ChannelSDK.login(2);
        }
    }

    public void switchLogin() {
        if (ChannelSDK.useLogin) {
            ChannelSDK.switchLogin();
        }
    }

    public void logout() {
        try {
            if (ChannelSDK.useLogin) {
                ChannelSDK.logout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSupportMethod(String methodName) {
        return Arrays.contain(this.supportedMethods, methodName);
    }
}
