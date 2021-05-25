package com.u8.server.web.user;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.cache.CacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import com.u8.server.data.UUser;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UGameManager;
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
public class SyncGameAction extends UActionSupport implements ModelDriven<UGame> {

    private static Logger log = Logger.getLogger(SyncGameAction.class.getName());

    private UGame game;

    private int currAppID;

    @Autowired
    private UGameManager gameManager;

    @Action("saveGame")
     public void saveChannel(){

        try{
            log.info("game:" + this.game.toJSON());

            if(game == null){
                return;
            }
            CacheManager.getInstance().saveGame(game);

            renderState(true);
        }catch(Exception e){
            e.printStackTrace();
            renderState(false);
        }

    }

    @Action("removeGame")
    public void removeChannel(){

        try{

            log.info("Curr gameID is " +this.currAppID);
            UGame m = this.gameManager.queryGame(this.currAppID);
            if(m == null){
                renderState(false);
                return;
            }

            List<UChannel> lst = this.gameManager.queryChannels(this.currAppID);
            if(lst.size() > 0){
                renderState(false, "请先删除该游戏下面的所有渠道数据");
                return;
            }

            CacheManager.getInstance().removeGame(game.getAppID());

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);

    }

    @Override
    public UGame getModel() {

        if(this.game == null){
            this.game = new UGame();
        }

        return this.game;
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

    public UGame getGame() {
        return game;
    }

    public void setGame(UGame game) {
        this.game = game;
    }

    public int getCurrAppID() {
        return currAppID;
    }

    public void setCurrAppID(int currAppID) {
        this.currAppID = currAppID;
    }
}
