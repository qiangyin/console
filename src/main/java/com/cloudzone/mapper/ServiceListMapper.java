package com.cloudzone.mapper;

import com.cloudzone.common.entity.k8s.DeploymentVo;
import com.cloudzone.common.entity.k8s.ServiceInfo;
import com.cloudzone.common.entity.k8s.ServiceListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leiyuanjie
 * @date 2018/03/21
 */
public interface ServiceListMapper {
    /**
     * 查询所有的服务
     *
     * @return
     * @author leiyuanjie
     * @date 2018/3/21
     */
    List<ServiceListVo> selectAll();

    /**
     * 查询指定服务的详情
     *
     * @param serviceName
     * @return
     * @author leiyuanjie
     * @date 2018/3/21
     */
    ServiceInfo selectByName(String serviceName);

    /**
     * 通过项目ID查询应用启动端口
     *
     * @param projectId
     * @return
     * @author leiyuanjie
     * @date 2018/3/21
     */
    Integer selectPort(Integer projectId);

    /**
     * 重启服务时通过服务名查询服务
     *
     * @param serviceName
     * @return
     */
    DeploymentVo selectService(String serviceName);

    /**
     * 查询最大端口
     *
     * @return
     */
    Integer selectMaxPort();

}
