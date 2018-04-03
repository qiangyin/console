package com.cloudzone.service.impl;

import com.cloudzone.common.PageHelper;
import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.Project;
import com.cloudzone.common.entity.project.ProjectVO;
import com.cloudzone.mapper.ProjectMapper;
import com.cloudzone.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author yintongjiang
 * @params
 * @since 2018/3/21d
 */
@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public ProjectVO create(ProjectVO projectVO) {
        projectVO.setCreateTime(new Date());
        Project project = new Project();
        BeanUtils.copyProperties(projectVO, project);
        projectMapper.insert(project);
        BeanUtils.copyProperties(project, projectVO);
        return projectVO;
    }

    @Override
    public List<Project> selectByName(String name) {
        return projectMapper.seletctByName(name);
    }

    @Override
    public PageInfo<Project> selectByParam(String keyword, int pageNum, int pageSize) {
        logger.info("pageNum={},pageSize={}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<Project>(projectMapper.selectByParam(keyword));
    }
}
