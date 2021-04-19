package com.u8.sdk;

import android.util.Log;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;

public class YSDKCallback implements UserListener, BuglyListener, PayListener {
    public void OnPayNotify(PayRet ret) {
        if (ret.ret == 0) {
            switch (ret.payState) {
                case -1:
                    ChannelSDK.showTip("未知的支付状态," + ret.msg);
                    U8SDK.getInstance().onResult(34, "pay unknown");
                    return;
                case 0:
                    Log.e("U8SDK", "pay success. now to req charge to u8server");
//                    ChannelSDK.chargeWhenPaySuccess();
                    return;
                case 1:
                    ChannelSDK.showTip("支付被取消");
                    U8SDK.getInstance().onResult(33, "pay cancel");
                    return;
                case 2:
                    ChannelSDK.showTip("支付失败：" + ret.msg);
                    U8SDK.getInstance().onResult(11, "pay failed:" + ret.msg);
                    return;
                default:
                    return;
            }
        }
        switch (ret.flag) {
            case eFlag.Login_TokenInvalid /*3100*/:
                ChannelSDK.showTip("登录token失效，请重新登录");
                U8SDK.getInstance().onLogout();
                return;
            case 4001:
                ChannelSDK.showTip("支付被取消");
                U8SDK.getInstance().onResult(33, "pay cancel");
                return;
            case eFlag.Pay_Param_Error /*4002*/:
                ChannelSDK.showTip("支付参数错误," + ret.toString());
                U8SDK.getInstance().onResult(11, "支付失败，参数错误" + ret.toString());
                return;
            default:
                ChannelSDK.showTip("支付异常，" + ret.toString());
                U8SDK.getInstance().onResult(11, "支付异常" + ret.toString());
                return;
        }
    }

    public byte[] OnCrashExtDataNotify() {
        return null;
    }

    public String OnCrashExtMessageNotify() {
        return null;
    }

    public void OnLoginNotify(UserLoginRet ret) {
        Log.d("U8SDK", "login notify:" + ret.flag);
//        ChannelSDK.afterLogout = false;
        if (ChannelSDK.useLogin) {
            switch (ret.flag) {
                case 0:
//                    ChannelSDK.letUserLogin(false);
                    return;
                case 1001:
                    U8SDK.getInstance().onResult(5, "user cancel");
                    return;
                case 1002:
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case 1003:
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case 1004:
                    ChannelSDK.showTip("手机未安装手Q，请安装后重试");
                    Log.d("U8SDK", "QQ not install . login failed.");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case eFlag.QQ_NotSupportApi /*1005*/:
                    ChannelSDK.showTip("手机手Q版本太低，请升级后重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case 2000:
                    ChannelSDK.showTip("手机未安装微信，请安装后重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case 2001:
                    ChannelSDK.showTip("手机微信版本太低，请升级后重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case eFlag.WX_UserCancel /*2002*/:
                    return;
                case eFlag.WX_UserDeny /*2003*/:
                    ChannelSDK.showTip("用户拒绝了授权，请重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case eFlag.WX_LoginFail /*2004*/:
                    ChannelSDK.showTip("微信登录失败，请重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                case eFlag.Login_TokenInvalid /*3100*/:
                    ChannelSDK.showTip("您尚未登录或者之前的登录已过期，请重试");

                case eFlag.Login_NotRegisterRealName /*3101*/:
                    ChannelSDK.showTip("您的账号没有进行实名认证，请实名认证后重试");
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
                default:
                    U8SDK.getInstance().onResult(5, "login failed");
                    return;
            }
        }
        Log.d("U8SDK", "curr config no login need. OnLoginNotify just return.");
    }

    public void OnRelationNotify(UserRelationRet arg0) {
    }

    public void OnWakeupNotify(WakeupRet ret) {
        if (eFlag.Wakeup_YSDKLogining == ret.flag) {
            return;
        }
        if (ret.flag == eFlag.Wakeup_NeedUserSelectAccount) {
//            ChannelSDK.showDiffLogin();
        } else if (ret.flag == eFlag.Wakeup_NeedUserLogin) {
            Log.d("U8SDK", "need login");
            U8SDK.getInstance().onLogout();
        } else {
            U8SDK.getInstance().onLogout();
        }
    }
}