package com.cloudzone.service.impl;

import com.cloudzone.GitLabServiceAPI;
import com.cloudzone.common.entity.gitlab.*;
import com.cloudzone.service.GitLabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * Gitlab Service
 *
 * @author tongqiangying@gmail.com
 * @since 2018/3/12
 */
@Service
public class GitLabServiceImpl implements GitLabService {

    @Autowired
    private GitLabServiceAPI gitLabServiceAPI;

    @Override
    public GitlabSessionVo login(String username, String password) throws IOException {
        return gitLabServiceAPI.login(username, password);
    }

    @Override
    public List<GitlabGroupVo> getGroupsForUser(String privateToken) throws IOException {
        return gitLabServiceAPI.getGroupsForUser(privateToken);
    }

    @Override
    public List<GitlabProjectVo> getProjectsByGroupId(String privateToken, int groupId) throws IOException {
        return gitLabServiceAPI.getProjectsByGroupId(privateToken, groupId);
    }

    @Override
    public List<GitlabBranchVo> getBranchesByProjectId(String privateToken, int projectId) throws IOException {
        return gitLabServiceAPI.getBranchesByProjectId(privateToken, projectId);
    }

    @Override
    public List<GitlabTagVo> getTagsByProjectId(String privateToken, int projectId) throws IOException {
        return gitLabServiceAPI.getTagsByProjectId(privateToken, projectId);
    }
}
