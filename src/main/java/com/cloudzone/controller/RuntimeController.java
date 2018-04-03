package com.cloudzone.controller;

import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.RuntimeVo;
import com.cloudzone.service.RuntimeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjunjie
 * @since 2018-03-21
 */
@RestController
@RequestMapping("/api/v1/runtime")
public class RuntimeController {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeController.class);

    @Autowired
    private RuntimeService runtimeService;

    /**
     * get 获取运行时环境
     *
     * @return List<RuntimeVo>
     */
    @ApiOperation(value = "查询运行时by cjj", notes = "查询运行时列表" , produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({@ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "服务器内部异常"),
            @ApiResponse(code = 403, message = "权限不足")})
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<List<RuntimeVo>> get(){
        List<RuntimeVo> list = new ArrayList<RuntimeVo>();
        list = runtimeService.get();
        ResponseResult<List<RuntimeVo>> result = new ResponseResult<List<RuntimeVo>>(list);
        return result;
    }
}
