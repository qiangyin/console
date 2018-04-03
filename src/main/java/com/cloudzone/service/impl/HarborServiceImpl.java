package com.cloudzone.service.impl;

import com.cloudzone.HarborServiceAPI;
import com.cloudzone.common.entity.harbor.*;
import com.cloudzone.service.HarborService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * harbor Service 实现
 *
 * @author gaoyanlei
 * @since 2018/3/12
 */
@Service
public class HarborServiceImpl implements HarborService {
    @Autowired
    private HarborServiceAPI harborServiceAPI;

    @Override
    public HttpHeaders login(LoginUserVo loginUserVo) {
        return harborServiceAPI.login(loginUserVo);
    }

    @Override
    public List<Projects> projects(String projectName, String isPublic, int page, int pageSize) {
        return harborServiceAPI.projects(projectName, isPublic, page, pageSize);
    }

    @Override
    public List<Repositories> repositories(String projectId,
                                           String q,
                                           String detail,
                                           int page,
                                           int pageSize) {
        return harborServiceAPI.repositories(projectId, q, detail, page, pageSize);
    }

    @Override
    public List<Tag> tags(String projectName, String repositoryName) {
        return harborServiceAPI.tags(projectName, repositoryName);
    }

    @Override
    public String saveProjects(ProjectVO projectVO) {
        return harborServiceAPI.saveProjects(projectVO);
    }
}
