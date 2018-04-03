package com.cloudzone.service;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.ServiceConfig;
import com.cloudzone.common.entity.k8s.ContainerVo;
import com.cloudzone.common.entity.k8s.DeploymentVo;
import com.cloudzone.common.entity.k8s.ServiceInfo;
import com.cloudzone.common.entity.k8s.ServiceListVo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * k8s-api server
 *
 * @author leiyuanjie
 * @date 2018/03/15
 */

@Service
public interface K8sService {

    /**
     * serviceList 服务列表
     *
     * @return
     */
    PageInfo<ServiceListVo> serviceList(int pageNum, int pageSize);

    /**
     * getInfoByServiceName 服务详情
     *
     * @param serviceName
     * @return
     */
    ServiceInfo getInfoByServiceName(String serviceName);

    /**
     * start 启动服务
     *
     * @param serviceName
     */
    void start(String serviceName);

    /**
     * stop 关闭服务
     *
     * @param serviceName
     */
    void stop(String serviceName);

    /**
     * create 创建服务
     *
     * @param deploymentVo
     * @throws IOException
     */
    void create(DeploymentVo deploymentVo) throws IOException;

    /**
     * getInstanceListByServiceName 服务实例列表
     *
     * @param serviceName
     * @return List<ContainerVo>
     */
    List<ContainerVo> getInstanceListByServiceName(String serviceName);

    /**
     * getPodLogs pod日志
     *
     * @param nameSpace
     * @param podName
     * @return String
     */
    String getPodLogs(String nameSpace, String podName);

    /**
     * 查询所有服务配置
     *
     * @return
     */
    List<ServiceConfig> listServiceConfig();
}
