package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannelExchange;
import org.springframework.stereotype.Repository;

/**
 * cp类
 */
@Repository("uChannelExChangeDao")
public class UChannelExChangeDao extends UHibernateTemplate<UChannelExchange, Integer>{


}
