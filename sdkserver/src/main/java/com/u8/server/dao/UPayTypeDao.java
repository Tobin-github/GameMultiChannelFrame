package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UUser;
import org.springframework.stereotype.Repository;

/**
 * 支付方式访问类
 */
@Repository("payTypeDao")
public class UPayTypeDao extends UHibernateTemplate<UUser, Integer>{



}
