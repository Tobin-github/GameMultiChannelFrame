package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UUserLoginLog;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问类
 */
@Repository("userLoginLogDao")
public class UUserLoginLogDao extends UHibernateTemplate<UUserLoginLog, Integer>{


}
