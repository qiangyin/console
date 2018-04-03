package com.cloudzone.service.impl;

import com.cloudzone.common.constant.WebsocketLogConstant;
import com.cloudzone.common.entity.pod.list.PodList;
import com.cloudzone.common.entity.pod.list.PodListRoot;
import com.cloudzone.common.entity.pod.list.PodListVo;
import com.cloudzone.common.entity.pod.log.PodLogContent;
import com.cloudzone.common.entity.pod.log.PodLogResp;
import com.cloudzone.common.exception.HttpException;
import com.cloudzone.common.utils.HttpUtil;
import com.cloudzone.common.utils.JsonUtil;
import com.cloudzone.common.utils.UtilAll;
import com.cloudzone.service.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.http.HTTPException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tianyuliang
 * @since 2018/3/15
 */
@Service
public class LogServiceImpl implements LogService {

    @Value("${pod.http.prefix}")
    private String podHttpPrefix;

    @Override
    public PodLogResp pullLog(String namespace, String podName) throws HTTPException {
        String url = WebsocketLogConstant.POD_LOG_CONTENT.replace("{0}", namespace).replace("{1}", podName);
        String respData = HttpUtil.get(podHttpPrefix + url, null);
        PodLogResp podLog = JsonUtil.toObject(respData, PodLogResp.class);
        if (podLog != null && podLog.getLogs() != null && !podLog.getLogs().isEmpty()) {
            for (PodLogContent plc : podLog.getLogs()) {
                if (!UtilAll.isBlank(plc.getTimestamp())) {
                    // 2018-03-15T10:28:37.724230191Z ----> 2018-03-15 18:28:37.724
                    // 2018-03-15T10:28:37.7242301Z ----> 2018-03-15 18:28:37.724
                    // 2018-03-15T10:28:37.7242Z ----> 2018-03-15 18:28:37.724
                    plc.setTimestamp(UtilAll.utcToLocalNanosecond(plc.getTimestamp()));
                    plc.setFormatTime(UtilAll.parseDateToLong(plc.getTimestamp()));
                }
            }
        }
        return podLog;
    }

    @Override
    public List<PodLogContent> pullPodLog(String namespace, String podName) throws HTTPException {
        List<PodLogContent> logs = new ArrayList<>();
        String url = WebsocketLogConstant.POD_LOG_CONTENT.replace("{0}", namespace).replace("{1}", podName);
        String respData = HttpUtil.get(podHttpPrefix + url, null);
        PodLogResp podLog = JsonUtil.toObject(respData, PodLogResp.class);
        if (podLog != null && podLog.getLogs() != null && !podLog.getLogs().isEmpty()) {
            for (PodLogContent plc : podLog.getLogs()) {
                if (!UtilAll.isBlank(plc.getTimestamp())) {
                    // 2018-03-15T10:28:37.724230191Z ----> 2018-03-15 18:28:37.724
                    // 2018-03-15T10:28:37.7242301Z ----> 2018-03-15 18:28:37.724
                    // 2018-03-15T10:28:37.7242Z ----> 2018-03-15 18:28:37.724
                    // logs.add(plc.toString()); // 此时部分日志content已经带有时间，不需要额外添加

                    plc.setTimestamp(UtilAll.utcToLocalNanosecond(plc.getTimestamp()));
                    plc.setFormatTime(UtilAll.parseDateToLong(plc.getTimestamp()));
                    logs.add(plc);
                }
            }
        }
        Collections.sort(logs);
        return logs;
    }

    @Override
    public List<PodListVo> pullPodLists() throws HttpException {
        List<PodListVo> podLists = new ArrayList<>();
        String requestUrl = podHttpPrefix + WebsocketLogConstant.POD_LIST;
        String respData = HttpUtil.get(requestUrl, null);
        PodListRoot podListRoot = JsonUtil.toObject(respData, PodListRoot.class);
        if (podListRoot == null || podListRoot.getPods() == null || podListRoot.getPods().isEmpty()) {
            return podLists;
        }

        PodListVo podListVo = null;
        for (PodList pod : podListRoot.getPods()) {
            if (pod.getObjectMeta() != null) {
                podListVo = new PodListVo();
                podListVo.setNamespace(pod.getObjectMeta().getNamespace());
                podListVo.setPodName(pod.getObjectMeta().getName());
                podListVo.setNodeName(pod.getNodeName());
                podLists.add(podListVo);
            }
        }
        return podLists;
    }
}
