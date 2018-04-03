package com.cloudzone.mapper;

import com.cloudzone.common.entity.HaProxyConfig;

import java.util.List;

public interface HaProxyConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HaProxyConfig record);

    int insertSelective(HaProxyConfig record);

    HaProxyConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HaProxyConfig record);

    int updateByPrimaryKey(HaProxyConfig record);

    /**
     * @return List<HaProxyConfig> 返回配置列表
     * @author yintongqiang
     * @params 查询所有的haproxy的配置
     * @since 2018/3/20
     */
    List<HaProxyConfig> selectAll();
}