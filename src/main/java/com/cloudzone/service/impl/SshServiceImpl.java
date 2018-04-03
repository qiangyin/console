package com.cloudzone.service.impl;

import com.cloudzone.common.constant.SshRespConstant;
import com.cloudzone.common.entity.HaProxyConfig;
import com.cloudzone.common.utils.SshUtil;
import com.cloudzone.mapper.HaProxyConfigMapper;
import com.cloudzone.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author yintongjiang
 * @params
 * @since 2018/3/20
 */
@Service("SshService")
public class SshServiceImpl implements SshService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshServiceImpl.class);

    @Autowired
    private HaProxyConfigMapper haProxyConfigMapper;

    @Override
    public synchronized boolean createHA(String svcName, int port) {

        boolean createFlag = false;
        loop:
        for (HaProxyConfig haProxyConfig : haProxyConfigMapper.selectAll()) {
            SshUtil.SshExecutor sshExecutor = null;
            try {
                sshExecutor = SshUtil.newInstance(haProxyConfig.getUser(), haProxyConfig.getPassword(), haProxyConfig.getHost(), haProxyConfig.getPort());
                String resp = sshExecutor.exec(haProxyConfig.getCmd() + " " + svcName + " " + port);
                switch (resp.trim()) {
                    case SshRespConstant.FASLE:
                        createFlag = false;
                        break loop;
                    case SshRespConstant.SUCCESS:
                        createFlag = true;
                        break;
                    default:
                        createFlag = false;
                        break loop;
                }

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("###createHa error={}", e);
            } finally {
                if (null != sshExecutor) {
                    sshExecutor.close();
                }
            }
        }
        return createFlag;
    }
}
