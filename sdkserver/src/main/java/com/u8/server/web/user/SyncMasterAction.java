package com.u8.server.web.user;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.cache.CacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelMasterManager;
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
public class SyncMasterAction extends UActionSupport implements ModelDriven<UChannelMaster> {

    private static Logger log = Logger.getLogger(SyncMasterAction.class.getName());

    private UChannelMaster master;

    private int currMasterID;

    @Autowired
    private UChannelMasterManager channelMasterManager;

    @Action("saveMaster")
     public void saveChannel(){

        try{
            log.info("master:" + this.master.toJSON());

            if(master == null){
                return;
            }
            CacheManager.getInstance().saveMaster(master);

            renderState(true);
        }catch(Exception e){
            e.printStackTrace();
            renderState(false);
        }

    }

    @Action("removeMaster")
    public void removeMaster(){

        try{

            log.info("Curr masterID is " + this.currMasterID);
            UChannelMaster m = this.channelMasterManager.queryChannelMaster(this.currMasterID);
            if(m == null){
                renderState(false);
                return;
            }

            List<UChannel> lst = this.channelMasterManager.queryChannels(this.currMasterID);
            if(lst.size() > 0){
                renderState(false, "请先删除该渠道商下面的所有渠道数据");
                return;
            }

            CacheManager.getInstance().removeMaster(currMasterID);

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);

    }

    @Override
    public UChannelMaster getModel() {

        if(this.master == null){
            this.master = new UChannelMaster();
        }
        return this.master;
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

    public UChannelMaster getMaster() {
        return master;
    }

    public void setMaster(UChannelMaster master) {
        this.master = master;
    }

    public int getCurrMasterID() {
        return currMasterID;
    }

    public void setCurrMasterID(int currMasterID) {
        this.currMasterID = currMasterID;
    }
}
