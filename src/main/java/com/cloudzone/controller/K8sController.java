package com.cloudzone.controller;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.constant.ResponseCodes;
import com.cloudzone.common.entity.ResponseResult;
import com.cloudzone.common.entity.ServiceConfig;
import com.cloudzone.common.entity.k8s.ContainerVo;
import com.cloudzone.common.entity.k8s.DeploymentVo;
import com.cloudzone.common.entity.k8s.ServiceInfo;
import com.cloudzone.common.entity.k8s.ServiceListVo;
import com.cloudzone.service.K8sService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author luocheng
 * @date 2018/03/20
 */

@RestController
@RequestMapping("/api/v1/k8s")
public class K8sController {
    private static final Logger logger = LoggerFactory.getLogger(K8sController.class);

    @Autowired
    private K8sService k8sService;

    /**
     * list 服务列表
     *
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-服务列表", notes = "服务列表")
    @ResponseBody
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ResponseResult<PageInfo<ServiceListVo>> serviceList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        PageInfo<ServiceListVo> pageInfo = k8sService.serviceList(pageNum, pageSize);

        ResponseResult<PageInfo<ServiceListVo>> results = new ResponseResult<>(pageInfo);
        return results;
    }

    /**
     * start 启动服务
     *
     * @param serviceName 服务名称
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-启动服务", notes = "启动服务")
    @ResponseBody
    @RequestMapping(value = "/{serviceName}/start", method = RequestMethod.GET)
    public ResponseResult<String> start(@PathVariable("serviceName") String serviceName) {
        logger.info("controller层开始进入启动服务， serviceName={}", serviceName);

        k8sService.start(serviceName);
        return new ResponseResult(ResponseCodes.Success, "success", null);
    }

    /**
     * stop 关闭服务
     *
     * @param serviceName 服务名称
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-关闭服务", notes = "关闭服务")
    @ResponseBody
    @RequestMapping(value = "/{serviceName}/stop", method = RequestMethod.GET)
    public ResponseResult<String> stop(@PathVariable("serviceName") String serviceName) {
        k8sService.stop(serviceName);
        return new ResponseResult(ResponseCodes.Success, "success", null);
    }

    /**
     * create 创建服务
     *
     * @param deploymentVo 服务配置信息
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-创建服务", notes = "创建服务")
    @ApiImplicitParam(name = "deploymentVo", value = "服务配置信息", required = true, dataType = "DeploymentVo")
    @ResponseBody
    @RequestMapping(value = "/service/create", method = RequestMethod.POST)
    public ResponseResult<String> create(@RequestBody DeploymentVo deploymentVo) {
        logger.info("controller层进入创建服务");

        // 校验服务名是否合法
        String regex = "^[a-z][a-z0-9_-]*$";
        boolean flag = deploymentVo.getServiceName().matches(regex);
        if (!flag) {
            return new ResponseResult(ResponseCodes.ParamNotValid, "服务名必须以小写字母开头，且服务名只能是数字，小写字母，下划线和横线组成", null);
        }

        // 校验实例数
        if (deploymentVo.getMaxReplicas() <= 0 || deploymentVo.getMinReplicas() <= 0 ||
                deploymentVo.getMaxReplicas() < deploymentVo.getMinReplicas()) {
            return new ResponseResult<>(ResponseCodes.ParamNotValid, "服务实例个数不能小于零，且最小实例不能大于最大实例", null);
        }

        if (deploymentVo.getMaxReplicas() > 10) {
            return new ResponseResult<>(ResponseCodes.ParamNotValid, "服务实例个数不能超过10个", null);
        }

        if (deploymentVo.getProjectId() <= 0 || deploymentVo.getServiceConfigId() <= 0 ||
                deploymentVo.getTargetCPUUtilizationPercentage() <= 0) {
            return new ResponseResult<>(ResponseCodes.ParamNotValid, "参数不合法", null);
        }

        try {
            k8sService.create(deploymentVo);
        } catch (IOException e) {
            return new ResponseResult<>(ResponseCodes.ParamNotValid, e.getMessage(), null);
        }

        return new ResponseResult(ResponseCodes.Success, "success", null);
    }

    /**
     * info 服务详情
     *
     * @param serviceName 服务名称
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-服务详情", notes = "服务详情")
    @ResponseBody
    @RequestMapping(value = "/{serviceName}/info", method = RequestMethod.GET)
    public ResponseResult<ServiceInfo> info(@PathVariable("serviceName") String serviceName) {
        return new ResponseResult(ResponseCodes.Success, "success", k8sService.getInfoByServiceName(serviceName));
    }

    /**
     * instanceList 服务实例列表
     *
     * @param serviceName 服务名称
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-服务实例列表", notes = "服务实例列表")
    @ResponseBody
    @RequestMapping(value = "/{serviceName}/instanceList", method = RequestMethod.GET)
    public ResponseResult<List<ContainerVo>> instanceList(@PathVariable("serviceName") String serviceName) {
        List<ContainerVo> containerVos = k8sService.getInstanceListByServiceName(serviceName);
        return new ResponseResult<>(ResponseCodes.Success, "success", containerVos);
    }

    /**
     * logs pod日志
     *
     * @param podName   pod名
     * @param nameSpace 命名空间
     * @return ResponseVo
     */
    @ApiOperation(value = "LYJ-pod日志", notes = "pod日志")
    @ResponseBody
    @RequestMapping(value = "/{nameSpace}/{podName}/logs", method = RequestMethod.GET)
    public ResponseResult<String> logs(@PathVariable("nameSpace") String nameSpace, @PathVariable("podName") String podName) {
        return new ResponseResult(ResponseCodes.Success, "success", k8sService.getPodLogs(nameSpace, podName));
    }

    @ApiOperation(value = "LYJ-配置服务列表", notes = "配置服务列表")
    @ResponseBody
    @RequestMapping(value = "/serviceConfig/list", method = RequestMethod.GET)
    public ResponseResult<List<ServiceConfig>> listServiceConfig() {
        return new ResponseResult(ResponseCodes.Success, "success", k8sService.listServiceConfig());
    }

}
