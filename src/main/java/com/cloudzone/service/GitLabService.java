package com.cloudzone.service;

import com.cloudzone.common.entity.gitlab.*;

import java.io.IOException;
import java.util.List;

/**
 * Gitlab Service
 *
 * @author rongzhihong@gome.com.cn
 * @since 2018/3/9
 */
public interface GitLabService {
    /**
     * gitlab login
     *
     * @param username username
     * @param password pwd
     * @return GitlabSessionVo
     * @throws IOException
     * @author rongzhihong
     * @since 2018/3/9
     */
    GitlabSessionVo login(String username, String password) throws IOException;

    /**
     * 获得用户所属分组
     *
     * @param privateToken 用户私钥
     * @return List<GitlabGroupVo>
     * @throws IOException
     * @author rongzhihong
     * @since 2018/3/9
     */
    List<GitlabGroupVo> getGroupsForUser(String privateToken) throws IOException;

    /**
     * 获得某个用户分组下的所有项目
     *
     * @param privateToken 用户私钥
     * @param groupId      分组ID
     * @return List<GitlabProjectVo>
     * @throws IOException
     * @author rongzhihong
     * @since 2018/3/9
     */
    List<GitlabProjectVo> getProjectsByGroupId(String privateToken, int groupId) throws IOException;

    /**
     * 获得某个项目下的所有分支
     *
     * @param privateToken 用户私钥
     * @param projectId    项目Id
     * @return List<GitlabBranchVo>
     * @throws IOException
     * @author rongzhihong
     * @since 2018/3/9
     */
    List<GitlabBranchVo> getBranchesByProjectId(String privateToken, int projectId) throws IOException;

    /**
     * 获得某个项目下的所有Tag
     *
     * @param privateToken 用户私钥
     * @param projectId    项目Id
     * @return List<GitlabBranchVo>
     * @throws IOException
     * @author rongzhihong
     * @since 2018/3/9
     */
    List<GitlabTagVo> getTagsByProjectId(String privateToken, int projectId) throws IOException;
}
