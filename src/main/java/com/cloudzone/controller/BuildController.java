package com.cloudzone.controller;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.jenkins.BuildConfigVO;
import com.cloudzone.common.entity.jenkins.BuildDetailVO;
import com.cloudzone.common.entity.jenkins.BuildHistoryVO;
import com.cloudzone.common.entity.jenkins.BuildProjectVO;
import com.cloudzone.exception.ServiceException;
import com.cloudzone.service.BuildService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Build Controller 构建服务
 *
 * @author zhoufei
 * @since 2018/3/20
 */
@RestController
@RequestMapping("/api/v1/build")
public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    @Autowired
    private BuildService buildService;

    /**
     * create 创建构建任务
     *
     * @param projectConfig 任务配置信息
     * @return ResponseVo<BuildProjectVO>
     */
    @ApiOperation(value = "ZF-创建任务", notes = "根据任务配置信息创建任务", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "projectConfig", value = "任务配置信息", required = true, dataType = "BuildConfigVO")
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public ResponseResult<BuildProjectVO> create(@RequestBody @Validated BuildConfigVO projectConfig) {
        ResponseResult<BuildProjectVO> result = new ResponseResult<>();

        try {
            BuildProjectVO buildProject = buildService.create(projectConfig);
            result.setData(buildProject);
            result.setCode(HttpStatus.OK.value());
            result.setMsg(HttpStatus.OK.toString());
        } catch (ServiceException e) {
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(e.getErrorMessage());
        }

        return result;
    }

    /**
     * list 构建任务列表
     *
     * @return ResponseVo
     */
    @ApiOperation(value = "ZF-构建任务列表", notes = "构建任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据总数", required = true, dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResult<PageInfo<BuildProjectVO>> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageInfo<BuildProjectVO> pageInfo = buildService.getProjects(pageNum, pageSize);
        ResponseResult<PageInfo<BuildProjectVO>> results = new ResponseResult<>(pageInfo);
        results.setCode(HttpStatus.OK.value());
        results.setMsg(HttpStatus.OK.toString());
        return results;
    }

    /**
     * build 启动构建任务
     *
     * @param projectId 项目Id
     * @return ResponseVo
     */
    @ApiOperation(value = "ZF-启动构建任务", notes = "根据项目Id启动构建任务")
    @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, dataType = "Long", paramType = "path")
    @ResponseBody
    @RequestMapping(value = "/{projectId}/startup", method = RequestMethod.GET)
    public ResponseResult<String> startup(@PathVariable Long projectId) {
        ResponseResult<String> result = new ResponseResult<>();

        try {
            buildService.start(projectId);
            result.setCode(HttpStatus.OK.value());
            result.setMsg(HttpStatus.OK.toString());
        } catch (ServiceException e) {
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(e.getErrorMessage());
        }

        return result;
    }

    /**
     * detail 构建任务详细
     *
     * @param projectId 项目Id
     * @return ResponseVo
     */
    @ApiOperation(value = "ZF-构建任务详细", notes = "根据项目Id获取构建任务详细")
    @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, dataType = "Long", paramType = "path")
    @ResponseBody
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public ResponseResult<BuildDetailVO> detail(@PathVariable Long projectId) {
        ResponseResult<BuildDetailVO> result = new ResponseResult<>();

        try {
            BuildDetailVO buildDetailVO = buildService.detail(projectId);
            result.setData(buildDetailVO);
            result.setCode(HttpStatus.OK.value());
            result.setMsg(HttpStatus.OK.toString());
        } catch (ServiceException e) {
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(e.getErrorMessage());
        }

        return result;
    }

    /**
     * list 构建历史列表
     *
     * @return ResponseVo
     */
    @ApiOperation(value = "ZF-构建历史列表", notes = "构建历史列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据总数", required = true, dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResponseResult<PageInfo<BuildHistoryVO>> buildHistorys(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageInfo<BuildHistoryVO> list = buildService.getBuildHistoryList(pageNum, pageSize);
        ResponseResult<PageInfo<BuildHistoryVO>> results = new ResponseResult<>(list);
        results.setCode(HttpStatus.OK.value());
        results.setMsg(HttpStatus.OK.toString());

        return results;
    }

    /**
     * list 构建历史列表
     *
     * @param projectId 项目Id
     * @return ResponseVo
     */
    @ApiOperation(value = "ZF-构建历史列表", notes = "构建历史列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目Id", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据总数", required = true, dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(value = "/history/{projectId}", method = RequestMethod.GET)
    public ResponseResult<PageInfo<BuildHistoryVO>> buildProjectHistorys(@PathVariable Long projectId,
                                                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageInfo<BuildHistoryVO> list = buildService.getBuildHistoryListByProjectId(projectId, pageNum, pageSize);
        ResponseResult<PageInfo<BuildHistoryVO>> results = new ResponseResult<>(list);
        results.setCode(HttpStatus.OK.value());
        results.setMsg(HttpStatus.OK.toString());

        return results;
    }

    /**
     * consoleLog 获取项目构建日志
     *
     * @param projectId 项目Id
     * @param buildId   构建Id
     * @return ResponseResult<String>
     */
    @ApiOperation(value = "ZF-获取项目构建日志", notes = "获取项目构建日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "buildId", value = "构建Id", required = true, dataType = "Long", paramType = "path")
    })
    @ResponseBody
    @RequestMapping(value = "/{projectId}/{buildId}/log", method = RequestMethod.GET)
    public ResponseResult<String> consoleLog(@PathVariable Long projectId, @PathVariable Long buildId) {
        ResponseResult<String> result = new ResponseResult<>();

        try {
            String log = buildService.getConsoleLogByProjectId(projectId, buildId);
            result.setData(log);
            result.setCode(HttpStatus.OK.value());
            result.setMsg(HttpStatus.OK.toString());
        } catch (ServiceException e) {
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(e.getErrorMessage());
        }

        return result;
    }

    /**
     * 更新构建状态
     *
     * @param projectName 项目Id
     * @param buildNumber 构建Id
     * @param status      构建Id
     * @return ResponseResult<String>
     */
    @ApiOperation(value = "ZF-更新构建状态", notes = "更新构建状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", value = "项目名称", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "buildNumber", value = "构建Id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "构建状态", required = true, dataType = "String")
    })
    @ResponseBody
    @RequestMapping(value = "/{projectName}/{buildNumber}/status", method = RequestMethod.GET)
    public ResponseResult<String> updateBuildStatus(@PathVariable String projectName, @PathVariable Long buildNumber,
                                                    @RequestParam("status") String status) {
        ResponseResult<String> result = new ResponseResult<>();

        try {
            buildService.updateBuildStatus(projectName, buildNumber, status);
            result.setCode(HttpStatus.OK.value());
            result.setMsg(HttpStatus.OK.toString());
        } catch (ServiceException e) {
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(e.getErrorMessage());
        }

        return result;
    }
}
