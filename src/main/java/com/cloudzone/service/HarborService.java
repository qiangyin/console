package com.cloudzone.service;

import com.cloudzone.common.entity.harbor.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Harborservice接口
 *
 * @author gaoyanlei
 * @since 2018/3/21 0021
 */
@Service
public interface HarborService {

    /**
     * 登录
     *
     * @author gaoyanlei
     * @since 2018/3/21
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    HttpHeaders login(@RequestBody LoginUserVo loginUserVo);

    /**
     * 获取项目信息
     *
     * @author gaoyanlei
     * @since 2018/3/21
     */
    List<Projects> projects(String projectName, String isPublic, int page, int pageSize);

    /**
     * 获取镜像
     *
     * @author gaoyanlei
     * @since 2018/3/21
     */
    List<Repositories> repositories(String projectId,
                                    String q,
                                    String detail,
                                    int page,
                                    int pageSize);

    /**
     * 获取镜像标签
     *
     * @author gaoyanlei
     * @params projectName 项目名称
     * @params repositorieName  镜像名称
     * @since 2018/3/21
     */
    List<Tag> tags(String projectName, String repositoryName);


    /**
     * 保存project
     *
     * @author gaoyanlei
     * @params projectVO 项目vo
     * @since 2018/3/21
     */
    String saveProjects(ProjectVO projectVO);
}