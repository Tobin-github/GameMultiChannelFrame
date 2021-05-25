package com.u8.server.service;

import com.u8.server.dao.UAdminDao;
import com.u8.server.data.UAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ant on 2015/8/29.
 */

@Service("adminManager")
public class UAdminManager {

    @Autowired
    private UAdminDao adminDao;

    public UAdmin getAdmin(int id){

        return adminDao.get(id);
    }

    public void saveAdmin(UAdmin admin){
        adminDao.save(admin);
    }

    public void deleteAdmin(UAdmin admin){
        adminDao.delete(admin);
    }

    public UAdmin getAdminByUsername(String username){
        String hql = "from UAdmin where username = ?";

        return (UAdmin)adminDao.findUnique(hql, username);
    }

    public List<UAdmin> getAllAdmins(){

        return adminDao.findAll();
    }
}
