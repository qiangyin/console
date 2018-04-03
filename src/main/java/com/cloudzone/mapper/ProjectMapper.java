package com.cloudzone.mapper;

import com.cloudzone.common.entity.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

    /**
     * 查询所有工程列表
     *
     * @param keyword 关键字查询
     * @return
     */
    List<Project> selectByParam(@Param("keyword") String keyword);

    /**
     * 查询名称相同的列表
     *
     * @param name
     * @return
     */
    List<Project> seletctByName(@Param("name") String name);

    /**
     * 根据项目名称查询项目
     *
     * @param name 项目名称
     * @return Project
     */
    Project selectByProjectName(String name);
}