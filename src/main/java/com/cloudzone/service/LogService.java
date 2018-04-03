package com.cloudzone.service;

import com.cloudzone.common.entity.pod.list.PodListVo;
import com.cloudzone.common.entity.pod.log.PodLogContent;
import com.cloudzone.common.entity.pod.log.PodLogResp;
import com.cloudzone.common.exception.HttpException;
import org.springframework.stereotype.Service;

import javax.xml.ws.http.HTTPException;
import java.util.List;

/**
 * @author tianyuliang
 * @since 2018/3/15
 */
@Service
public interface LogService {

    /**
     * 拉取Pod吐出来的日志
     *
     * @param namespace pod命名空间
     * @param podName   pod名称
     * @return
     * @throws HTTPException
     */
    List<PodLogContent> pullPodLog(String namespace, String podName) throws HTTPException;

    /**
     * 拉取Pod吐出来的日志
     *
     * @param namespace pod命名空间
     * @param podName   pod名称
     * @return
     * @throws HTTPException
     */
    PodLogResp pullLog(String namespace, String podName) throws HTTPException;

    /**
     * 拉取Pod列表
     *
     * @return
     * @throws HttpException
     */
    List<PodListVo> pullPodLists() throws HttpException;

}
