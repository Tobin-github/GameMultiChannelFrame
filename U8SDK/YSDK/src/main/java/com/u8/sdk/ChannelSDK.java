package com.u8.sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.pay.PayItem;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.share.impl.IScreenImageCapturer;
import com.tencent.ysdk.module.user.UserApi;
import com.u8.sdk.utils.ResourceHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ChannelSDK {
    public static boolean useLogin = true;
    private static boolean logined = false;
    private static int payType = 1;
    private static int ratio;
    private static String lastLoginResult = null;
    private static String appKey;
    private static String payUrl;
    private static boolean guestLogin = false;
    private static boolean fixedPay;
    private static boolean isCustomLogin = false;
    private static boolean isLandscape = false;
    private static String queryUrl;
    private static String coinIconName;
    private static boolean multiServers;

    private static PayParams payData;
    private static boolean inited = false;


    public static void initSDK(SDKParams params) {
        inited = true;
        parseSDKParams(params);
        U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                Log.d("U8SDK", "onActivityResult call in ysdk");
                YSDKApi.onActivityResult(requestCode, resultCode, data);
            }

            public void onStop() {
                Log.d("U8SDK", "onStop call in ysdk");
                YSDKApi.onStop(U8SDK.getInstance().getContext());
            }

            public void onResume() {
                Log.d("U8SDK", "onResume call in ysdk");
                YSDKApi.onResume(U8SDK.getInstance().getContext());
            }

            public void onRestart() {
                Log.d("U8SDK", "onRestart call in ysdk");
                YSDKApi.onRestart(U8SDK.getInstance().getContext());
            }

            public void onPause() {
                Log.d("U8SDK", "onPause call in ysdk");
                YSDKApi.onPause(U8SDK.getInstance().getContext());
            }

            public void onNewIntent(Intent newIntent) {
                Log.d("U8SDK", "onNewIntent call in ysdk");
                YSDKApi.handleIntent(newIntent);
            }

            public void onDestroy() {
                Log.d("U8SDK", "onDestroy call in ysdk");
                YSDKApi.onDestroy(U8SDK.getInstance().getContext());
            }
        });
        try {
            YSDKApi.onCreate(U8SDK.getInstance().getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            YSDKApi.setUserListener(new YSDKCallback());
            YSDKApi.setBuglyListener(new YSDKCallback());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        YSDKApi.setScreenCapturer(new IScreenImageCapturer() {
            public Bitmap caputureImage() {
                try {
                    String path = U8SDK.getInstance().getContext().getExternalFilesDir("screenshot").getAbsoluteFile() + "/" + SystemClock.currentThreadTimeMillis() + ".png";
                    Log.d("U8SDK", "curr thread:" + Thread.currentThread().getName());
                    Log.d("U8SDK", "ysdk capture img called. path:" + path);
                    U8SDK.getInstance().onResult(55, path);
                    File f = new File(path);
                    long t = SystemClock.currentThreadTimeMillis();
                    while (SystemClock.currentThreadTimeMillis() - t < 3000 && !f.exists()) {
                        Thread.sleep(500);
                    }
                    if (!f.exists()) {
                        return null;
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    options.inSampleSize = ChannelSDK.calculateInSampleSize(options);
                    options.inJustDecodeBounds = false;
                    return BitmapFactory.decodeFile(path, options);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        YSDKApi.handleIntent(U8SDK.getInstance().getContext().getIntent());
        if (!useLogin) {
            UserApi.getInstance().login(ePlatform.Guest);
        }
        U8SDK.getInstance().onResult(1, "init success");
    }
    private static int calculateInSampleSize(BitmapFactory.Options options) {
        float maxW = (float) 668;
        float maxH = (float) 1272;
        if (isLandscape) {
            maxW = (float) 1152;
            maxH = (float) 786;
        }
        float originalWidth = (float) options.outWidth;
        float originalHeight = (float) options.outHeight;
        Log.d("U8SDK", "curr original width:" + originalWidth + ";original height:" + originalHeight);
        int inSampleSize = 1;
        if (originalWidth > maxW || originalHeight > maxH) {
            inSampleSize = (int) Math.ceil((double) Math.max(originalWidth / maxW, originalHeight / maxH));
        }
        Log.d("U8SDK", "curr in sample size:" + inSampleSize);
        return inSampleSize;
    }




    private static void parseSDKParams(SDKParams params) {
        fixedPay = params.getBoolean("WG_FIXEDPAY").booleanValue();
        coinIconName = params.getString("WG_COIN_ICON_NAME");
        multiServers = params.getBoolean("WG_MULTI_SERVERS").booleanValue();
        queryUrl = params.getString("WG_QUERY_URL");
        payUrl = params.getString("WG_PAY_URL");
        ratio = params.getInt("WG_RATIO");
        appKey = params.getString("M_APP_KEY");
        payType = params.getInt("M_PAY_TYPE");
        guestLogin = params.getBoolean("M_GUEST_LOGIN").booleanValue();
        isCustomLogin = params.getBoolean("M_CUSTOM_LOGIN").booleanValue();
        if (params.contains("M_USE_LOGIN")) {
            useLogin = params.getBoolean("M_USE_LOGIN").booleanValue();
        }
        isLandscape = isLandscape();
    }

    public static void switchLogin() {
        Log.d("U8SDK", "switch login...");
//        afterLogout = true;
        lastLoginResult = null;
        login();
    }


    public static void login() {
        Log.d("U8SDK", "ysdk login begin...");
        if (SDKTools.isNetworkAvailable(U8SDK.getInstance().getContext())) {
            try {
                if (lastLoginResult != null) {
                    Log.d("U8SDK", "already auto logined. callback result directly." + lastLoginResult);
                    U8SDK.getInstance().onLoginResult(lastLoginResult);
                    lastLoginResult = null;
                    return;
                }
//                openLoginUI();
                return;
            } catch (Exception e) {
                U8SDK.getInstance().onResult(5, e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        U8SDK.getInstance().onResult(0, "The network now is unavailable");
    }


    public static void pay(PayParams data) {
        if (payType != 1) {
            payForItem(data);
        } else if (useLogin) {
            payForCoin(data);
        } else {
            Log.e("U8SDK", "pay for coin must use login. but now not use login.");
            U8SDK.getInstance().onResult(11, "pay failed");
        }
    }

    public static void payForItem(PayParams data) {
        String serverId;
        PayItem item = new PayItem();
        item.id = data.getProductId();
        item.name = data.getProductName();
        item.desc = data.getProductDesc();
        item.price = data.getPrice() * ratio;
        item.num = 1;
        Bitmap bmp = BitmapFactory.decodeResource(U8SDK.getInstance().getContext().getResources(), ResourceHelper.getIdentifier(U8SDK.getInstance().getContext(), "R.drawable." + coinIconName));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] resData = baos.toByteArray();
        if (multiServers) {
            serverId = payData.getServerId();
        } else {
            serverId = "1";
        }
        YSDKApi.buyGoods(false, serverId, item, appKey, resData, data.getOrderID(), data.getOrderID(), new PayListener() {
            public void OnPayNotify(PayRet ret) {
                Log.d("U8SDK", "pay for item result:" + ret.toString());
                if (ret.ret == 0) {
                    switch (ret.payState) {
                        case -1:
                            U8SDK.getInstance().onResult(34, "pay unknown");
                            return;
                        case 0:
                            U8SDK.getInstance().onResult(10, "pay success");
                            return;
                        case 1:
                            U8SDK.getInstance().onResult(33, "pay cancel");
                            return;
                        case 2:
                            U8SDK.getInstance().onResult(11, "pay failed");
                            return;
                        default:
                            return;
                    }
                }
                switch (ret.flag) {
                    case eFlag.Login_TokenInvalid /*3100*/:
                        Log.d("U8SDK", "local token invalid. you now to login again.");
                        try {
                            YSDKApi.logout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        U8SDK.getInstance().onLogout();
                        return;
                    case 4001:
                        U8SDK.getInstance().onResult(33, "pay cancel");
                        return;
                    default:
                        U8SDK.getInstance().onResult(11, "pay failed");
                        return;
                }
            }
        });
    }

    public static void payForCoin(PayParams data) {
        payData = data;

    }

    public static void logout() {
        Log.d("U8SDK", "logout from sdk");
//        afterLogout = true;
        lastLoginResult = null;
        YSDKApi.logout();
    }


    public static boolean isStartLogined() {
        return logined;
    }

    public static boolean isSupportGuestLogin() {
        return guestLogin;
    }

    public static boolean isCustomLogin() {
        return isCustomLogin;
    }

    public static void showTip(String tip) {

    }



    private static void showProgressDialog(String msg) {

    }


    public static boolean isLandscape() {
        if (U8SDK.getInstance().getContext().getResources().getConfiguration().orientation == 2) {
            return true;
        }
        return false;
    }

}
