package com.cloudzone.service;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.Project;
import com.cloudzone.common.entity.project.ProjectVO;
import com.cloudzone.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author yintongjiang
 * @params 项目业务类
 * @since 2018/3/21
 */
public interface ProjectService {

    /**
     * 创建项目
     *
     * @param projectVO
     * @return
     */
    ProjectVO create(ProjectVO projectVO);

    /**
     * 根据名称查询项目列表
     *
     * @param name
     * @return
     */
    List<Project> selectByName(String name);

    /**
     * 查询所有项目列表
     *
     * @return
     */
    PageInfo<Project> selectByParam(String keyword, int pageNum, int pageSize);
}
