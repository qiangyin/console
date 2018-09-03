package com.cloudzone.controller;

import com.cloudzone.common.entity.pod.log.PodLogContent;
import com.cloudzone.common.entity.pod.stomp.StompPullLog;
import com.cloudzone.common.utils.JsonUtil;
import com.cloudzone.common.utils.UtilAll;
import com.cloudzone.service.LogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tongqiangying@gmail.com
 * @since 2018/3/22
 */
@RestController
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private static final int maxLine = 5;

    private static final int maxLength = 1000;

    private static final String maxLineFlag = "<br>";

    private static final String websocketPushPeriod = "websocket.push.period";

    private static final String websocketPushDelay = "websocket.push.delay";

    private static final String websocketLogEnable = "websocket.log.enable";

    private static final AtomicLong count = new AtomicLong(0L);


    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private LogService logService;

    @Autowired
    private Environment env;

    /**
     * 用来记录当前websocket连接了几次 <br/>
     * 第一次创建连接后放入scheduledParamMap，后续如果刷新页面，则不需要再次创建链接
     */
    final ConcurrentMap<String, Boolean> scheduledParamMap = new ConcurrentHashMap<>();

    /**
     * 每次拉取新数据的最新一条日志的最大时间戳
     */
    final ConcurrentMap<String, Long> lastLogTimestampMap = new ConcurrentHashMap<>();


    final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    /**
     * 内部维护N个线程的线程池
     */
    final ExecutorService exec = Executors.newFixedThreadPool(501);

    /**
     * 容量为10的阻塞队列
     */
    final BlockingQueue<Future<ConcurrentMap<String, List<String>>>> queue = new LinkedBlockingDeque<>(500);


    /**
     * 实例化CompletionService
     */
    final CompletionService<ConcurrentMap<String, List<String>>> completionService = new ExecutorCompletionService<>(exec, queue);


    public LogController() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (exec != null) {
                exec.shutdown();
            }
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
        }, "scheduledExecutorServiceShutdown"));
    }

    /**
     * 服务端接收消息接口
     *
     * @param jsonData
     * @return
     * @throws Exception
     * @MessageMapping 表示服务端接收客户端通过“/app/log”发送过来的消息
     */
    @MessageMapping("/log")
    public void pullPodLogContent(String jsonData) throws Exception {
        logger.info("websocket send ... jsonData");

        StompPullLog stompLog = JsonUtil.toObject(jsonData, StompPullLog.class);
        if (stompLog == null) {
            throw new Exception("the websocket client send() param is invalid.  " + jsonData);
        }

        String namespace = UtilAll.getValueTrim(stompLog.getNamespace());
        String podName = UtilAll.getValueTrim(stompLog.getPodName());
        if (UtilAll.isBlank(namespace)) {
            throw new Exception("the 'namespace' field is empty.");
        }
        if (UtilAll.isBlank(podName)) {
            throw new Exception("the 'podName' field is empty.");
        }

        String key = String.format("%s/%s", namespace, podName);
        Boolean ok = scheduledParamMap.putIfAbsent(key, true);
        if (ok != null && ok) {
            logger.warn("scheduled service is already convertAndSend. key=" + key);
            return;
        }

        long period = Long.parseLong(env.getProperty(websocketPushPeriod, "10"));
        long delay = Long.parseLong(env.getProperty(websocketPushDelay, "5"));
        Boolean queryLogEnable = Boolean.valueOf(env.getProperty(websocketLogEnable, "false"));

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                // 提交拉取日志任务
                for (Map.Entry<String, Boolean> entry : scheduledParamMap.entrySet()) {
                    completionService.submit(() -> {
                        String namespaceAndPodName = entry.getKey();
                        String[] tmp = namespaceAndPodName.trim().split("/");
                        List<PodLogContent> podLogContents = logService.pullPodLog(tmp[0], tmp[1]);
                        ConcurrentMap<String, List<String>> completionParamMap = new ConcurrentHashMap<>(1);

                        if (!queryLogEnable) {
                            List<String> tmpLogs = new ArrayList<>();
                            String time = UtilAll.formatDefaultDate(System.currentTimeMillis());
                            String logData = time + " 任意日志数据. count = " + String.valueOf(count.incrementAndGet());
                            tmpLogs.add(logData);
                            lastLogTimestampMap.put(key, System.currentTimeMillis());
                            completionParamMap.put(key, tmpLogs);
                        } else {
                            if (podLogContents != null && podLogContents.size() > 0) {
                                // 获取当前拉取日志列表最末尾(时间戳最大、最小)的那条日志
                                long fetchLogMaxTimestamp = podLogContents.get(podLogContents.size() - 1).getFormatTime();
                                Long lastTimestamp = lastLogTimestampMap.get(namespaceAndPodName);

                                List<String> tmpLogs = null;
                                if (lastTimestamp == null) {
                                    // 第一次比较时间戳
                                    tmpLogs = new ArrayList<>();
                                    for (PodLogContent logContent : podLogContents) {
                                        tmpLogs.add(logContent.getContent());
                                    }
                                    lastLogTimestampMap.put(key, fetchLogMaxTimestamp);
                                    completionParamMap.put(key, tmpLogs);
                                } else {
                                    // 后续多次比较时间戳,防止重复推送
                                    tmpLogs = new ArrayList<>();
                                    long maxTimestamp = 0;
                                    for (PodLogContent logContent : podLogContents) {
                                        if (logContent.getFormatTime() > lastTimestamp) {
                                            tmpLogs.add(logContent.getContent());
                                            if (maxTimestamp < logContent.getFormatTime()) {
                                                maxTimestamp = logContent.getFormatTime();
                                            }
                                        }
                                    }
                                    lastLogTimestampMap.put(key, maxTimestamp);
                                    completionParamMap.put(key, tmpLogs);
                                }
                            }
                        }
                        return completionParamMap;
                    });
                }
            } catch (Exception e) {
                logger.error("completionService submit error.", e);
            }


            try {
                // 获取每个任务的执行结果, 每个map只有一条数据
                // 注意此时的ConcurrentMap里面的key值，并非恰好就是当前参数传递过来的namespace与podName
                ConcurrentMap<String, List<String>> completionParam = completionService.take().get();
                if (completionParam != null && !completionParam.isEmpty()) {
                    String destination = null;
                    List<String> vals = null;
                    for (Map.Entry<String, List<String>> entry : completionParam.entrySet()) {
                        // destination 是前端订阅消息的标记，订阅了对应的topic才可以收到相应的消息,convertAndSend() 属于广播消息
                        destination = String.format("/topic/%s", entry.getKey());
                        String allLog = StringUtils.join(entry.getValue(), maxLineFlag);
                        // 取消vals遍历循环，组合在一个，统一发送
                        messagingTemplate.convertAndSend(destination, allLog);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("InterruptedException ... ", e);
            } catch (ExecutionException e) {
                logger.error("ExecutionException ... ", e);
            }
        }, period, delay, TimeUnit.SECONDS);


    }


}
