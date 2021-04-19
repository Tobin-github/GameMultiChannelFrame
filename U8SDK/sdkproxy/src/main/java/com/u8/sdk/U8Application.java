package com.u8.sdk;

import com.u8.sdk.log.Log;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;


/**
 * 游戏中的AndroidManifest.xml中application节点的android:name属性一定要设置为com.u8.sdk.U8Application
 * 如果游戏需要在application生命周期方法中执行部分操作，那么需要定义一个类去实现IApplicationListener接口
 * 在该接口的实现方法中去完成。
 * 然后在application节点下面建立一个meta-data节点，meta-data节点的name属性为U8_Game_Application
 * value属性就是刚刚实现的类的完整类名
 * 
 * @author xiaohei
 *
 */
public class U8Application extends Application{

    @Override
	public void onCreate(){
		super.onCreate();
		U8SDK.getInstance().onAppCreate(this);
        Log.d("u8sdk", "U8Application onCreate finish");
	}

	
	/**
	 * 注意：这个attachBaseContext方法是在onCreate方法之前调用的
	 */
    @Override
	public void attachBaseContext(Context base){
		super.attachBaseContext(base);
		U8SDK.getInstance().onAppAttachBaseContext(this, base);
        MultiDex.install(this);
        Log.d("u8sdk", "U8Application attachBaseContext finish");
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		
		U8SDK.getInstance().onAppConfigurationChanged(this, newConfig);
        Log.d("u8sdk", "U8Application onConfigurationChanged finish");
	}


    @Override
	public void onTerminate(){
        super.onTerminate();
		U8SDK.getInstance().onTerminate();
        Log.d("u8sdk", "U8Application onTerminate finish");

	}
	
}
