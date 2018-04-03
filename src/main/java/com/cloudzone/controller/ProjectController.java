package com.cloudzone.controller;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.constant.ResponseCodes;
import com.cloudzone.common.entity.Project;
import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.project.ProjectVO;
import com.cloudzone.service.ProjectService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yintongjiang
 * @params
 * @since 2018/3/21
 */
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    /**
     * create 创建项目
     *
     * @param projectVO 任务配置信息
     * @return ResponseVo<ProjectVO>
     */
    @ApiOperation(value = "YTQ-创建项目", notes = "创建项目添加项目基本信息", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "projectVO", value = "添加项目基本信息", required = true, dataType = "ProjectVO")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "服务器内部异常"),
            @ApiResponse(code = 403, message = "权限不足")})
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public ResponseResult<ProjectVO> create(@RequestBody @Validated ProjectVO projectVO) {
        if (projectService.selectByName(projectVO.getName()).isEmpty()) {
            projectVO.setUserName("CloudZone");
            projectService.create(projectVO);
            ResponseResult<ProjectVO> result = new ResponseResult<ProjectVO>(projectVO);
            return result;
        } else {
            return new ResponseResult(501, "项目名称已存在");
        }
    }

    /**
     * projects 获取项目列表
     *
     * @return ResponseVo<Project>
     */
    @ApiOperation(value = "YTQ-项目列表", notes = "项目列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, paramType = "query", dataType = "int")
    })
    @ApiResponses({@ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "服务器内部异常"),
            @ApiResponse(code = 403, message = "权限不足")})
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseResult<PageInfo<Project>> projects(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "100000") int pageSize) {
        return new ResponseResult<PageInfo<Project>>(projectService.selectByParam(keyword, pageNum, pageSize));

    }
}
