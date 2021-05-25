package com.u8.server.service;

import com.u8.server.dao.UAdminDao;
import com.u8.server.dao.USwitchDao;
import com.u8.server.data.UAdmin;
import com.u8.server.data.USwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ant on 2015/8/29.
 */

@Service("switchManager")
public class USwitchManager {

    @Autowired
    private USwitchDao switchDao;

    public USwitch getSwitchById(int id){

        return switchDao.get(id);
    }

    public USwitch getSwitchByAllIndex(int gameID, int masterID, String channelID){

        String hql = "from USwitch where gameID = ? and masterID = ? and channelID = ?";

        return (USwitch)switchDao.findUnique(hql, gameID, masterID, channelID);

    }

    public USwitch getSwitchByIndex(int gameID, int channelID){

        String hql = "from USwitch where gameID = ? and channelID = ?";

        return (USwitch)switchDao.findUnique(hql, gameID, channelID);

    }

    public USwitch getSwitchByGameId(int gameID){

        String hql = "from USwitch where gameID = ?";

        return (USwitch)switchDao.findUnique(hql, gameID);

    }

    public USwitch getSwitchByMasterID(int masterID){

        String hql = "from USwitch where masterID = ?";

        return (USwitch)switchDao.findUnique(hql, masterID);

    }

    public USwitch getSwitchByChannelID(int channelID){

        String hql = "from USwitch where channelID = ?";

        return (USwitch)switchDao.findUnique(hql, channelID);

    }
}
