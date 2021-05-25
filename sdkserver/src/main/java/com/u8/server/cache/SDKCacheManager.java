package com.u8.server.cache;

import com.u8.server.data.UChannel;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKScript;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个渠道SDK都有一个实现了ISDKScript接口的SDK逻辑处理类，登录认证和获取订单号接口中，通过反射的方式来
 * 实例化对应渠道的类，可能会导致一定的性能瓶颈。所以，这里我们增加一个缓存，第一次实例化之后，将对应渠道的处理类，缓存起来。
 * 后面使用的时候，直接从缓存中取
 * Created by xiaohei on 15/12/23.
 */
public class SDKCacheManager {

    private static SDKCacheManager instance;

    private Map<Integer, ISDKScript> sdkCaches;
    private Map<Integer, String> cachedClassNames;

    private SDKCacheManager(){

        sdkCaches = new ConcurrentHashMap<Integer, ISDKScript>();
        cachedClassNames = new ConcurrentHashMap<Integer, String>();
    }

    public synchronized static SDKCacheManager getInstance(){
        if(instance == null){
            instance = new SDKCacheManager();

        }

        return instance;
    }

    /***
     * 获取指定渠道的ISDKScript的实例
     * @param channel
     * @return
     */
    public synchronized ISDKScript getSDKScript(UChannel channel){

        if(channel == null){
            return  null;
        }

        if(sdkCaches.containsKey(channel.getChannelID())){

            String className = cachedClassNames.get(channel.getChannelID());
            if(className.equals(channel.getChannelVerifyClass())){
                return sdkCaches.get(channel.getChannelID());
            }

            sdkCaches.remove(channel.getChannelID());
            cachedClassNames.remove(channel.getChannelID());
        }

        try {
            ISDKScript script = (ISDKScript)Class.forName(channel.getChannelVerifyClass()).newInstance();
            sdkCaches.put(channel.getChannelID(), script);
            cachedClassNames.put(channel.getChannelID(), channel.getChannelVerifyClass());
            return script;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized void saveSDKScript(UChannel channel){

        if(sdkCaches.containsKey(channel.getChannelID())){
            sdkCaches.remove(channel.getChannelID());
            cachedClassNames.remove(channel.getChannelID());
        }

        Log.d("the saveSDKScript is "+channel);
        try {
            ISDKScript script = (ISDKScript)Class.forName(channel.getChannelVerifyClass()).newInstance();
            sdkCaches.put(channel.getChannelID(), script);
            cachedClassNames.put(channel.getChannelID(), channel.getChannelVerifyClass());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public synchronized void removeSDKScript(int channelID){

        if(sdkCaches.containsKey(channelID)){
            sdkCaches.remove(channelID);
            cachedClassNames.remove(channelID);
        }

    }
}
