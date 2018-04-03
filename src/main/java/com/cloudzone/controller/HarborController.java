package com.cloudzone.controller;

import com.cloudzone.common.constant.ResponseCodes;
import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.harbor.*;
import com.cloudzone.service.HarborService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HarborController harbor对外暴露接口
 *
 * @author gaoyanlei
 * @since 2018/3/21
 */
@RestController
@RequestMapping("/api/v1/harbor")
public class HarborController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HarborService harborService;

    @ApiOperation(value = "GYL:获取harbor项目", notes = "获取项目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project_name", value = "项目名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "is_public", value = "是否公开", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page_size", value = "每页数量", required = true, paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ResponseResult<List<Projects>> projects(@RequestParam("project_name") String projectName,
                                                   @RequestParam("is_public") String isPublic,
                                                   @RequestParam("page") int page,
                                                   @RequestParam("page_size") int pageSize) {
        return new ResponseResult(ResponseCodes.Success, "success", harborService.projects(projectName, isPublic, page, pageSize));
    }

    @ApiOperation(value = "GYL:获取项目下镜像", notes = "获取镜像信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project_id", value = "项目名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "q", value = "查询条件", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "detail", value = "0", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page_size", value = "每页数量", required = true, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/repositories", method = RequestMethod.GET)
    public ResponseResult<List<Repositories>> projects(@RequestParam("project_id") String projectId,
                                                       @RequestParam("q") String q,
                                                       @RequestParam("detail") String detail,
                                                       @RequestParam("page") int page,
                                                       @RequestParam("page_size") int pageSize) {
        return new ResponseResult(ResponseCodes.Success, "success", harborService.repositories(projectId, q, detail, page, pageSize));
    }

    @ApiOperation(value = "GYL:获取镜像下标签", notes = "获取镜像标签信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", value = "项目名称", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "repositoryName", value = "镜像名称", required = true, paramType = "path", dataType = "String"),
    })
    @RequestMapping(value = "/tags/{projectName}/{repositoryName}", method = RequestMethod.GET)
    public ResponseResult<List<Tag>> tags(@PathVariable("projectName") String projectName, @PathVariable("repositoryName") String repositorieName) {
        return new ResponseResult(ResponseCodes.Success, "success", harborService.tags(projectName, repositorieName));
    }

    @ApiOperation(value = "GYL:保存项目信息", notes = "保存项目信息")
    @ApiImplicitParam(name = "projectVO", value = "项目信息", required = true, dataType = "ProjectVO")
    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    public ResponseResult<String> saveProjet(@RequestBody ProjectVO projectVO) {
        logger.info("save project : ProjectName=" + projectVO.getProjectName());
        return new ResponseResult(ResponseCodes.Success, "success", harborService.saveProjects(projectVO));
    }

}
