package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UChannel;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UAdminManager;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理主页
 * Created by ant on 2015/8/22.
 */
@Controller
@Namespace("/admin")
public class AdminIndexAction extends UActionSupport implements ModelDriven<UAdmin>{

    private UAdmin admin;

    @Autowired
    private UAdminManager adminManager;

    @Action(value = "login", results = {@Result(name = "success", location = "/WEB-INF/admin/login.jsp")})
    public String showLogin(){

        return "success";
    }

    @Action("doLogin")
    public void login(){
        try{

            Log.d("The username is "+admin.getUsername()+";password:"+admin.getPassword());

            UAdmin exists = adminManager.getAdminByUsername(admin.getUsername());

            if(exists == null){
                renderState(false, "用户名错误");
                return;
            }

            if(!exists.getPassword().equals(admin.getPassword())){
                renderState(false, "用户密码错误");
                return;
            }

            this.session.put("adminName", exists.getUsername());
            renderState(true, "登录成功");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("exit")
    public void exit(){
        this.session.remove("adminName");

        renderState(true, "exit success");
    }

    @Action(value = "index", results = {@Result(name = "success", location = "/WEB-INF/admin/index.jsp")})
    public String index(){


        return "success";
    }

    @Action(value = "adminRoleManage", results = {@Result(name = "success", location = "/WEB-INF/admin/adminRoles.jsp")})
    public String showAdminRoleManage(){


        return "success";
    }


    @Action("getAllAdmins")
    public void getAllAdmins(){
        try{

            List<UAdmin> lst = adminManager.getAllAdmins();
            JSONObject json = new JSONObject();
            json.put("total", lst.size());
            JSONArray masterArray = new JSONArray();
            for(UAdmin m : lst){
                masterArray.add(m.toJSON());
            }
            json.put("rows", masterArray);


            renderJson(json.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //添加或者编辑
    @Action("saveAdmin")
    public void saveAdmin(){

        try{

            if(TextUtils.isEmpty(this.admin.getUsername()) || TextUtils.isEmpty(this.admin.getPassword())){
                renderState(false, "用户名和密码不能为空");
                return;
            }

            if(this.admin.getId() != null){
                UAdmin exist = adminManager.getAdmin(this.admin.getId());
                if(exist != null ){

                    if(!exist.getPassword().equals(this.admin.getPassword())){
                        //说明修改了密码，重新md5
                        exist.setPassword(EncryptUtils.md5(this.admin.getPassword()).toLowerCase());
                    }

                    this.admin = exist;
                }
            }else{
                this.admin.setPassword(EncryptUtils.md5(this.admin.getPassword()).toLowerCase());
            }


            adminManager.saveAdmin(this.admin);
            renderState(true, "保存成功");

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "保存失败");
    }

    @Action("removeAdmin")
    public void removeAdmin(){
        try{
            Log.d("Curr userID is " + this.admin.getId());

            UAdmin user = adminManager.getAdmin(this.admin.getId());

            if(user == null){
                renderState(false, "角色不存在");
                return;
            }

            adminManager.deleteAdmin(user);

            renderState(true, "删除成功");

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "删除失败");
    }

    @Action(value = "adminPermissionManage", results = {@Result(name = "success", location = "/WEB-INF/admin/adminPermissions.jsp")})
    public String showPermissionManage(){

        return "success";
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }

    @Override
    public UAdmin getModel() {
        if(this.admin == null){
            this.admin = new UAdmin();
        }
        return this.admin;
    }
}
