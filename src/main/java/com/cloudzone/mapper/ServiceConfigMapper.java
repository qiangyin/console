package com.cloudzone.mapper;

import com.cloudzone.common.entity.ServiceConfig;

import java.util.List;

public interface ServiceConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ServiceConfig record);

    int insertSelective(ServiceConfig record);

    ServiceConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ServiceConfig record);

    int updateByPrimaryKey(ServiceConfig record);

    /**
     * 查询service_config表的所有数据
     *
     * @return
     */
    List<ServiceConfig> selectAllService();
}