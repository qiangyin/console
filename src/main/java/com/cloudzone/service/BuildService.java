package com.cloudzone.service;

import com.cloudzone.common.PageInfo;
import com.cloudzone.common.entity.jenkins.BuildConfigVO;
import com.cloudzone.common.entity.jenkins.BuildDetailVO;
import com.cloudzone.common.entity.jenkins.BuildHistoryVO;
import com.cloudzone.common.entity.jenkins.BuildProjectVO;
import com.cloudzone.exception.ServiceException;

import java.util.List;

/**
 * Build Service 构建服务
 *
 * @author zhoufei
 * @since 2018/3/20
 */
public interface BuildService {
    /**
     * create 创建构建任务
     *
     * @param projectConfig 构建项目配置信息
     * @return BuildProjectVO
     * @throws ServiceException
     */
    BuildProjectVO create(BuildConfigVO projectConfig) throws ServiceException;

    /**
     * detail 获取任务详情
     *
     * @param projectId 构建项目配置信息
     * @return BuildDetailVO
     * @throws ServiceException
     */
    BuildDetailVO detail(Long projectId) throws ServiceException;

    /**
     * list 构建任务列表
     *
     * @param pageNum  每页数量
     * @param pageSize 总页数
     * @return PageInfo<BuildProjectVO>
     */
    PageInfo<BuildProjectVO> getProjects(int pageNum, int pageSize);

    /**
     * getProjectConfigById 根据项目Id获取项目配置信息
     *
     * @param projectId 项目Id
     * @return BuildConfigVO
     * @throws ServiceException
     */
    BuildConfigVO getProjectConfigById(Long projectId) throws ServiceException;

    /**
     * getBuildHistoryList 获取所有项目构建历史
     *
     * @param pageNum  每页数量
     * @param pageSize 总页数
     * @return PageInfo<BuildHistoryVO>
     */
    PageInfo<BuildHistoryVO> getBuildHistoryList(int pageNum, int pageSize);

    /**
     * getBuildHistoryListByProjectId 根据项目Id获取项目构建历史
     *
     * @param projectId 项目Id
     * @param pageNum   每页数量
     * @param pageSize  总页数
     * @return PageInfo<BuildHistoryVO>
     */
    PageInfo<BuildHistoryVO> getBuildHistoryListByProjectId(Long projectId, int pageNum, int pageSize);

    /**
     * getConsoleLogByProjectId 根据项目Id获取控制台日志
     *
     * @param projectId 项目Id
     * @param buildId   构建Id
     * @return String
     * @throws ServiceException
     */
    String getConsoleLogByProjectId(Long projectId, Long buildId) throws ServiceException;

    /**
     * start 启动构建任务
     *
     * @param projectId 项目Id
     * @throws ServiceException
     */
    void start(Long projectId) throws ServiceException;

    /**
     * stop 停止构建任务
     *
     * @param projectId 项目Id
     * @throws ServiceException
     */
    void stop(Long projectId) throws ServiceException;

    /**
     * 更新构建状态
     *
     * @param projectName   项目名称
     * @param buildNumber 构建编号
     * @param status      构建状态
     * @return
     */
    void updateBuildStatus(String projectName, Long buildNumber, String status) throws ServiceException;
}
