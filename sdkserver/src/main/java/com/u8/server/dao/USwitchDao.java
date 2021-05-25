package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UAdmin;
import com.u8.server.data.USwitch;
import org.springframework.stereotype.Repository;

/**
 * Admin数据访问
 * Created by ant on 2015/8/29.
 */
@Repository("switchDao")
public class USwitchDao extends UHibernateTemplate<USwitch, Integer>{
}
