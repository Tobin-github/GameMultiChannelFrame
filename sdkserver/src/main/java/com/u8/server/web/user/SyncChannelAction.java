package com.u8.server.web.user;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.cache.CacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UUser;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UChannelMasterManager;
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
public class SyncChannelAction extends UActionSupport implements ModelDriven<UChannel> {

    private static Logger log = Logger.getLogger(SyncChannelAction.class.getName());

    private UChannel channel;

    private int currChannelID;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;

    @Action("saveChannel")
     public void saveChannel(){

        try{
            log.info("channel:" + this.channel.toJSON());

            if(channel == null){
                return;
            }
            CacheManager.getInstance().saveChannel(channel);

            renderState(true);
        }catch(Exception e){
            e.printStackTrace();
            renderState(false);
        }

    }

    @Action("removeChannel")
    public void removeChannel(){

        try{

            log.info("Curr channelID is " +this.currChannelID);
            UChannel c = this.channelManager.queryChannel(this.currChannelID);
            if(c == null){
                renderState(false);
                return;
            }

            List<UUser> lst = this.userManager.getUsersByChannel(this.currChannelID);
            if(lst.size() > 0){
                renderState(false, "请先删除该渠道下面的所有用户数据");
                return;
            }

            CacheManager.getInstance().removeChannel(this.currChannelID);

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
