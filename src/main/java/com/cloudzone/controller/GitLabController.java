package com.cloudzone.controller;

import com.cloudzone.common.constant.GcCloudConstant;
import com.cloudzone.common.constant.ResponseCodes;
import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.gitlab.*;
import com.cloudzone.service.GitLabService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * gitlab相关操作
 *
 * @author rongzhihong@gome.com.cn
 * @since 2018/3/9
 */
@RestController
@RequestMapping("/api/v1/gitlab")
public class GitLabController {
    private static final Logger logger = LoggerFactory.getLogger(GitLabController.class);

    @Autowired
    private GitLabService gitLabService;

    /**
     * 登录gitlab,获得session
     *
     * @param session
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "RZH-gitlab登陆", notes = "gitlab用户登陆获取session")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "gitlab账号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "gitlab密码", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<String> login(HttpSession session, @RequestParam("username") String username, @RequestParam("password") String password) {

        logger.info("gitlab login: username=" + username);
        ResponseResult<String> responseResult;

        if (username == null || "".equals(username) || password == null || "".equals(password)) {
            responseResult = new ResponseResult<>(ResponseCodes.ParamNotValid, "参数异常", "");
            return responseResult;
        }

        try {
            GitlabSessionVo gitlabSessionVo = gitLabService.login(username, password);
            if (gitlabSessionVo != null) {
                session.setAttribute(GcCloudConstant.GITLAB_SESSION_TAG, gitlabSessionVo);
            }

            responseResult = new ResponseResult<>(ResponseCodes.Success, "success", null);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("gitlab login throws:", e);
            responseResult = new ResponseResult<>(ResponseCodes.ServerError, "服务端异常", null);
        }

        return responseResult;
    }

    /**
     * 获得用户所属的分组列表
     *
     * @param session
     * @return
     */
    @ApiOperation(value = "RZH-获得登陆用户的所属分组", notes = "根据登陆用户的privateToken来获取用户所属分组信息")
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<List<GitlabGroupVo>> getGroups(HttpSession session) {

        logger.info("gitlab getGroups");
        ResponseResult<List<GitlabGroupVo>> responseResult;

        Object gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
        if (gitlabSession == null) {
            responseResult = new ResponseResult<>(ResponseCodes.NotAuth, "操作前，请优先登录", null);
            return responseResult;
        }

        GitlabSessionVo gitlabSessionVo = (GitlabSessionVo) gitlabSession;
        try {
            List<GitlabGroupVo> list = gitLabService.getGroupsForUser(gitlabSessionVo.getPrivateToken());
            responseResult = new ResponseResult<>(ResponseCodes.Success, "success", list);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("gitlab getGroups throws:", e);
            responseResult = new ResponseResult<>(ResponseCodes.ServerError, "服务端异常", null);
        }

        return responseResult;
    }

    /**
     * 获得用户所属的某个分组下的所有项目
     *
     * @param session
     * @param groupId
     * @return
     */
    @ApiOperation(value = "RZH-获得某个用户分组下的所有项目", notes = "根据登陆用户的privateToken、groupId来获取用户所属分组下的项目列表信息")
    @ApiImplicitParam(name = "groupId", value = "分组Id", required = true, paramType = "path", dataType = "Long")
    @RequestMapping(value = "/groups/{groupId}/projects", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<List<GitlabProjectVo>> getProjects(HttpSession session, @PathVariable("groupId") int groupId) {

        logger.info("gitlab getProjects：groupId=" + groupId);
        ResponseResult<List<GitlabProjectVo>> responseResult;

        Object gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
        if (gitlabSession == null) {
            // FIXME 当前demo版本，没有登录，所以先默认登录，后续将该登录的过程删除
            // START
            GitlabSessionVo gitlabSessionVo = null;
            try {
                gitlabSessionVo = gitLabService.login("root", "12345678");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (gitlabSessionVo != null) {
                session.setAttribute(GcCloudConstant.GITLAB_SESSION_TAG, gitlabSessionVo);
            }
            gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
            // END
            /*responseResult = new ResponseResult<>(ResponseCodes.NotAuth, "操作前，请优先登录", null);
            return responseResult;*/
        }

        GitlabSessionVo gitlabSessionVo = (GitlabSessionVo) gitlabSession;
        try {
            List<GitlabProjectVo> list = gitLabService.getProjectsByGroupId(gitlabSessionVo.getPrivateToken(), groupId);
            responseResult = new ResponseResult<>(ResponseCodes.Success, "success", list);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("gitlab getProjects:", e);
            responseResult = new ResponseResult<>(ResponseCodes.ServerError, "服务端异常", null);
        }

        return responseResult;
    }

    /**
     * 获得用户所属的某个分组下的某个项目下的所有分支
     *
     * @param session
     * @param projectId
     * @return
     */
    @ApiOperation(value = "RZH-获得某个项目下的所有分支", notes = "根据登陆用户的privateToken、groupId来获取用户所属分组下的项目列表信息")
    @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, paramType = "path", dataType = "Long")
    @RequestMapping(value = "/projects/{projectId}/repository/branches", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<List<GitlabBranchVo>> getBranches(HttpSession session, @PathVariable("projectId") int projectId) {

        logger.info("gitlab getBranches: projectId=" + projectId);
        ResponseResult<List<GitlabBranchVo>> responseResult;

        Object gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
        if (gitlabSession == null) {
            // FIXME 当前demo版本，没有登录，所以先默认登录，后续将该登录的过程删除
            // START
            GitlabSessionVo gitlabSessionVo = null;
            try {
                gitlabSessionVo = gitLabService.login("root", "12345678");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (gitlabSessionVo != null) {
                session.setAttribute(GcCloudConstant.GITLAB_SESSION_TAG, gitlabSessionVo);
            }
            gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
            // END

            /*responseResult = new ResponseResult<>(ResponseCodes.NotAuth, "操作前，请优先登录", null);
            return responseResult;*/
        }

        GitlabSessionVo gitlabSessionVo = (GitlabSessionVo) gitlabSession;
        try {
            List<GitlabBranchVo> list = gitLabService.getBranchesByProjectId(gitlabSessionVo.getPrivateToken(), projectId);
            responseResult = new ResponseResult<>(ResponseCodes.Success, "success", list);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("gitlab getBranches:", e);
            responseResult = new ResponseResult<>(ResponseCodes.ServerError, "服务端异常", null);
        }

        return responseResult;
    }

    /**
     * 获得用户所属的某个分组下的某个项目下的所有Tag
     *
     * @param session
     * @param projectId
     * @return
     */
    @ApiOperation(value = "RZH-获得某个项目下的所有Tag", notes = "根据登陆用户的privateToken、groupId来获取用户所属分组下的项目列表信息")
    @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, paramType = "path", dataType = "Long")
    @RequestMapping(value = "/projects/{projectId}/repository/tags", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<List<GitlabTagVo>> getTags(HttpSession session, @PathVariable("projectId") int projectId) {

        logger.info("gitlab getTags: projectId=" + projectId);
        ResponseResult<List<GitlabTagVo>> responseResult;

        Object gitlabSession = session.getAttribute(GcCloudConstant.GITLAB_SESSION_TAG);
        if (gitlabSession == null) {
            responseResult = new ResponseResult<>(ResponseCodes.NotAuth, "操作前，请优先登录", null);
            return responseResult;
        }

        GitlabSessionVo gitlabSessionVo = (GitlabSessionVo) gitlabSession;
        try {
            List<GitlabTagVo> list = gitLabService.getTagsByProjectId(gitlabSessionVo.getPrivateToken(), projectId);
            responseResult = new ResponseResult<>(ResponseCodes.Success, "success", list);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("gitlab getTags:", e);
            responseResult = new ResponseResult<>(ResponseCodes.ServerError, "服务端异常", null);
        }

        return responseResult;
    }

}
