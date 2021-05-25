package com.u8.server.dao;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UStatistics;
import org.springframework.stereotype.Repository;


/**
 * 数据后台配置对象数据访问类
 */
@Repository("statisticsDao")
public class UStatisticsDao extends UHibernateTemplate<UStatistics, Integer>{

}
