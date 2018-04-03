package com.cloudzone.mapper;

import com.cloudzone.common.entity.BuildConfig;

import java.util.List;

/**
 * @author chenjunjie
 * @since 2018-03-22
 */
public interface RuntimeConfigMapper {

    /**
     * selectByParentId 查询运行时信息
     * @param parentId 通过ParentId=0确定使用语言,通过ParentId为非0确定版本
     * @return List<BuildConfig>
     */
    List<BuildConfig> selectByParentId(Long parentId);

    /**
     * selectByName 查询同一个语言的所有版本信息
     * @param name 语言名
     * @return List<BuildConfig>
     */
    List<BuildConfig> selectByName(String name);
}
