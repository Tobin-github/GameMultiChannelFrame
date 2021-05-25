package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannelpayType;
import org.springframework.stereotype.Repository;

/**
 * cp类
 */
@Repository("uChannelPayTypeDao")
public class UChannelPayTypeDao extends UHibernateTemplate<UChannelpayType, Integer>{


}
