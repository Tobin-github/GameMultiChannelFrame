package com.u8.server.web.user;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.cache.CacheManager;
import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UUser;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UUserManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 这个类是模拟游戏服
 * 客户端-》第三方SDK登录成功之后，访问u8server获取token。
 * 当token获取成功之后，就会开始连接游戏服。这个类就是模拟
 * 登录流程的最后一步操作
 *
 * 客户端拿着userID,token等信息连接游戏服务器，游戏服需要去u8server
 * 验证token，验证成功，则登录合法，否则登录失败
 *
 * Created by ant on 2015/4/17.
 */
@Controller
@Namespace("/user/sync")
public class SyncScriptAction extends UActionSupport implements ModelDriven<UChannel> {

    private static Logger log = Logger.getLogger(SyncScriptAction.class.getName());

    private UChannel channel;

    private int currChannelID;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;

    @Action("saveScript")
     public void saveScript(){

        try{
            log.info("channel:" + this.channel.toJSON());

            if(channel == null){
                return;
            }
            SDKCacheManager.getInstance().saveSDKScript(channel);

            renderState(true);
        }catch(Exception e){
            e.printStackTrace();
            renderState(false);
        }

    }

    @Action("removeScript")
    public void removeScript(){

        try{

            log.info("Curr channelID is " +this.currChannelID);


            SDKCacheManager.getInstance().removeSDKScript(this.currChannelID);

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);

    }

    @Override
    public UChannel getModel() {

        if(this.channel == null){
            this.channel = new UChannel();
        }

        return this.channel;
    }

    private void renderState(boolean suc){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", suc? "操作成功" : "操作失败");
        renderText(json.toString());
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }

    public UChannel getChannel() {
        return channel;
    }

    public void setChannel(UChannel channel) {
        this.channel = channel;
    }

    public int getCurrChannelID() {
        return currChannelID;
    }

    public void setCurrChannelID(int currChannelID) {
        this.currChannelID = currChannelID;
    }
}
