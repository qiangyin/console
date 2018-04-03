package com.cloudzone.service.impl;

import com.cloudzone.K8sServiceAPI;
import com.cloudzone.common.PageHelper;
import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.Service;
import com.cloudzone.common.entity.ServiceConfig;
import com.cloudzone.common.entity.k8s.ContainerVo;
import com.cloudzone.common.entity.k8s.DeploymentVo;
import com.cloudzone.common.entity.k8s.ServiceInfo;
import com.cloudzone.common.entity.k8s.ServiceListVo;
import com.cloudzone.mapper.ServiceConfigMapper;
import com.cloudzone.mapper.ServiceListMapper;
import com.cloudzone.mapper.ServiceMapper;
import com.cloudzone.service.K8sService;
import com.cloudzone.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * @author leiyuanjie
 * @date 2018/03/21
 */
@org.springframework.stereotype.Service
public class K8sServiceImpl implements K8sService {

    private final Logger logger = LoggerFactory.getLogger(K8sServiceImpl.class);
    private static final Integer BASE_PORT = 8500;
    private static final Integer END_PORT = 9000;

    @Value("${k8s.api.server.haproxy}")
    private String externalIp;

    @Autowired
    private K8sServiceAPI k8sServiceAPI;

    @Autowired
    private ServiceListMapper serviceListMapper;

    @Autowired
    private ServiceConfigMapper serviceConfigMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private SshService sshService;

    @Override
    public PageInfo<ServiceListVo> serviceList(int pageNum, int pageSize) {
        logger.info("start to query all services");
        PageHelper.startPage(pageNum, pageSize);
        List<ServiceListVo> list = serviceListMapper.selectAll();

        PageInfo<ServiceListVo> result = new PageInfo<ServiceListVo>(list);
        return result;
    }

    @Override
    public ServiceInfo getInfoByServiceName(String serviceName) {
        logger.info("start to query service detail, serviceName={}", serviceName);
        return serviceListMapper.selectByName(serviceName);
    }

    @Override
    public void start(String serviceName) {
        logger.info("start服务时开始查询服务参数");

        // 通过服务名查询得到启动服务所需参数
        DeploymentVo deploymentVo = serviceListMapper.selectService(serviceName);
        logger.info("start服务查询结束, serviceName={}, image={}, cpu={}", deploymentVo.getServiceName(),
                deploymentVo.getImageName(), deploymentVo.getCpu());
        // 更改数据库服务状态
        Service service = new Service();
        service.setStatus(0);
        service.setName(serviceName);
        serviceMapper.updateStatusByName(service);

        // 启动服务
        k8sServiceAPI.startService(deploymentVo);
    }

    @Override
    public void stop(String serviceName) {
        // 更改数据库服务状态
        Service service = new Service();
        service.setStatus(1);
        service.setName(serviceName);
        serviceMapper.updateStatusByName(service);
        // 删除服务
        k8sServiceAPI.deleteService(serviceName);
    }

    @Override
    public void create(DeploymentVo deploymentVo) throws IOException {
        // 校验服务名是否已经存在
        ServiceInfo serviceInfo = serviceListMapper.selectByName(deploymentVo.getServiceName());
        if (serviceInfo != null) {
            throw new IOException("该服务名已经存在，请更换服务名");
        }

        logger.info("开始查询port");
        // 根据项目ID拿到内部启动端口(需要校验，以免查不到数据导致空指针异常)
        Integer port = serviceListMapper.selectPort(deploymentVo.getProjectId());
        if (port == null) {
            throw new IOException("查询的内部服务端口为空");
        }

        // 查询数据库拿到最大端口
        Integer maxPort = serviceListMapper.selectMaxPort();
        if (maxPort == null || maxPort == 0) {
            maxPort = BASE_PORT;
        }

        logger.info("开始校验端口HA");
        // 对外服务暴露的的端口, 对端口做HA校验
        int nodePort = maxPort + 1;
        for (int startPort = nodePort; startPort < END_PORT; startPort++) {
            boolean success = sshService.createHA(deploymentVo.getServiceName(), startPort);
            if (success) {
                nodePort = startPort;
                break;
            }
            logger.info("startPort={}", startPort);
        }

        // 将port和nodePort存入deploymentVo
        deploymentVo.setPort(port);
        deploymentVo.setNodePort(nodePort);

        logger.info("校验完毕, nodePort={}", nodePort);
        logger.info("controller开始创建服务*************deploymentName={}", deploymentVo.getServiceName());
        // 创建服务
        k8sServiceAPI.createDeploymentAndService(deploymentVo);

        logger.info("开始向service表插入数据");
        // 插入service 数据表
        Service service = new Service();
        service.setProjectId(Long.valueOf(deploymentVo.getProjectId()));
        service.setServiceConfigId(Long.valueOf(deploymentVo.getServiceConfigId()));
        service.setName(deploymentVo.getServiceName());
        service.setStatus(0);
        service.setMaxInstance(Long.valueOf(deploymentVo.getMaxReplicas()));
        service.setMinInstance(Long.valueOf(deploymentVo.getMinReplicas()));
        service.setImgUrl(deploymentVo.getImageName());
        service.setImgVer(deploymentVo.getImageVersion());
        service.setDepKind(deploymentVo.getDeployMode());
        service.setExternalIp(externalIp);
        service.setExternalPort(nodePort);
        service.setInsideDomain(deploymentVo.getServiceName());
        service.setInsidePort(port);
        service.setSvcType(deploymentVo.getServiceType());
        service.setCreateTime(new java.sql.Date(System.currentTimeMillis()));
        service.setTargetCpuUtilization(deploymentVo.getTargetCPUUtilizationPercentage());
        serviceMapper.insert(service);

    }

    @Override
    public List<ContainerVo> getInstanceListByServiceName(String serviceName) {
        return k8sServiceAPI.getAllContainers(serviceName);
    }

    @Override
    public String getPodLogs(String nameSpace, String podName) {
        return k8sServiceAPI.getLogWithNamespaceAndPod(nameSpace, podName);
    }

    @Override
    public List<ServiceConfig> listServiceConfig() {
        return serviceConfigMapper.selectAllService();
    }
}
