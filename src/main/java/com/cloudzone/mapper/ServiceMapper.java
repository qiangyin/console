package com.cloudzone.mapper;

import com.cloudzone.common.entity.Service;

public interface ServiceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Service record);

    int insertSelective(Service record);

    Service selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Service record);

    int updateByPrimaryKey(Service record);

    /**
     * 通过服务名去更改状态
     *
     * @param service
     * @return
     */
    int updateStatusByName(Service service);
}