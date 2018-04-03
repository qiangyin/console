package com.cloudzone.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yintongjiang
 * @params ssh服务
 * @since 2018/3/20
 */
public interface SshService {

    /**
     * 创建外部负载
     *
     * @param svcName 服务名称
     * @param port    服务端口
     * @return
     */
    boolean createHA(String svcName, int port);
}
