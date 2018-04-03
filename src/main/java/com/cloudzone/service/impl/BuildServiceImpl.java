package com.cloudzone.service.impl;

import com.cloudzone.JenkinsServiceApi;
import com.cloudzone.common.PageHelper;
import com.cloudzone.common.PageInfo;
import com.cloudzone.common.constant.BuildStatus;
import com.cloudzone.common.entity.*;
import com.cloudzone.common.entity.jenkins.*;
import com.cloudzone.exception.ServiceException;
import com.cloudzone.mapper.BuildConfigMapper;
import com.cloudzone.mapper.BuildHistoryMapper;
import com.cloudzone.mapper.BuildMetaMapper;
import com.cloudzone.mapper.ProjectMapper;
import com.cloudzone.service.BuildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Build Service 构建服务
 *
 * @author zhoufei
 * @since 2018/3/20
 */
@Service("BuildService")
public class BuildServiceImpl implements BuildService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private BuildMetaMapper buildMetaMapper;

    @Autowired
    private BuildHistoryMapper buildHistoryMapper;

    @Autowired
    private BuildConfigMapper buildConfigMapper;

    @Autowired
    private JenkinsServiceApi jenkinsServiceApi;


    @Override
    public BuildProjectVO create(BuildConfigVO buildConfigVO) throws ServiceException {
        BuildProjectVO buildProject;
        try {

            Project project = projectMapper.selectByPrimaryKey(buildConfigVO.getProjectId());
            if (project == null) {
                throw new ServiceException("项目信息不存在：" + buildConfigVO.getProjectName());
            }

            BuildMeta buildMeta = buildMetaMapper.selectBuildMetaByProjectId(project.getId());
            if (buildMeta != null) {
                throw new ServiceException("构建任务已经存在：" + project.getName());
            }

            BuildConfig buildConfig = buildConfigMapper.selectByLanguageNameAndVersion(buildConfigVO.getBuildLanguage(),
                    buildConfigVO.getLanguageVersion());
            if (buildConfig == null) {
                throw new ServiceException("构建语言或语言版本无效");
            }

            // harbor repository info
            String username = "cloudzone";
            String repositoryUsername = "admin";
            String repositoryPassword = "Harbor12345";

            // 调用Jenkins服务创建任务
            buildConfigVO.setProjectId(project.getId());
            JobConfig config = buildConfigVO.toJobConfig(buildConfig.getImgPath(), username, repositoryUsername, repositoryPassword);
            ResponseResult<JenkinsJob> jenkinsResult = jenkinsServiceApi.createJenkinsJob(config);
            if (jenkinsResult.getCode() != HttpStatus.OK.value()) {
                throw new ServiceException(jenkinsResult.getMsg());
            }

            buildProject = buildConfigVO.toBuildProject(BuildStatus.RUNNABLE.getCode());

            // 插入数据库
            Long buildConfigId = null;
            BuildMeta newBuildMeta = buildConfigVO.toBuildMeta(buildConfigId, BuildStatus.RUNNABLE.getCode());
            newBuildMeta.setBuildConfigId(buildConfig.getId());
            buildMetaMapper.insert(newBuildMeta);

        } catch (ServiceException e) {
            logger.error("创建任务失败: ", e);
            throw new ServiceException("创建任务失败: ", e);
        }

        return buildProject;
    }

    @Override
    public BuildDetailVO detail(Long projectId) throws ServiceException {
        BuildDetailVO buildDetailVO = buildMetaMapper.selectBuildDetailByProjectId(projectId);
        if (buildDetailVO == null) {
            throw new ServiceException("项目信息不存在：" + projectId);
        }

        buildDetailVO.getBuildConfig().setRepositoryType("git");

        return buildDetailVO;
    }

    @Override
    public PageInfo<BuildProjectVO> getProjects(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BuildProjectVO> buildHistories = buildMetaMapper.selectBuildProjects();
        PageInfo<BuildProjectVO> result = new PageInfo<>(buildHistories);

        return result;
    }

    @Override
    public BuildConfigVO getProjectConfigById(Long projectId) throws ServiceException {
        Project project = projectMapper.selectByPrimaryKey(projectId);

        if (project == null) {
            throw new ServiceException("项目信息不存在：" + projectId);
        }

        BuildMeta buildMeta = buildMetaMapper.selectBuildMetaByProjectId(projectId);
        BuildConfigVO buildConfigVO = buildMeta.toBuildConfigVO(project.getName(), "git",
                "Java", "1.8", "fromImage");

        return buildConfigVO;
    }

    @Override
    public PageInfo<BuildHistoryVO> getBuildHistoryList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BuildHistoryVO> buildHistories = buildHistoryMapper.selectBuildHistories();
        PageInfo<BuildHistoryVO> result = new PageInfo<>(buildHistories);

        return result;
    }

    @Override
    public PageInfo<BuildHistoryVO> getBuildHistoryListByProjectId(Long projectId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BuildHistoryVO> buildHistories = buildHistoryMapper.selectBuildHistoriesByProjectId(projectId);
        PageInfo<BuildHistoryVO> result = new PageInfo<>(buildHistories);

        return result;
    }

    @Override
    public String getConsoleLogByProjectId(Long projectId, Long buildId) throws ServiceException {
        String consoleLog;
        try {
            Project project = projectMapper.selectByPrimaryKey(projectId);

            if (project == null) {
                throw new ServiceException("项目信息不存在：" + projectId);
            }

            consoleLog = jenkinsServiceApi.jobBuildInfo(project.getName(), buildId.intValue());
        } catch (Exception e) {
            throw new ServiceException("获取任务日志失败: ", e);
        }

        return consoleLog;
    }

    @Override
    public void start(Long projectId) throws ServiceException {
        try {
            Project project = projectMapper.selectByPrimaryKey(projectId);

            if (project == null) {
                throw new ServiceException("项目信息不存在：" + projectId);
            }

            BuildMeta buildMeta = buildMetaMapper.selectBuildMetaByProjectId(projectId);
            if (buildMeta == null) {
                throw new ServiceException("项目构建配置信息不存在：" + project.getName());
            }

            ResponseResult<Integer> responseResult = jenkinsServiceApi.buildJob(project.getName());

            if (responseResult.getCode() != HttpStatus.OK.value()) {
                throw new ServiceException(responseResult.getMsg());
            }

            Integer buildNumber = responseResult.getData();
            BuildMeta updateBuildMeta = new BuildMeta();
            updateBuildMeta.setId(buildMeta.getId());
            updateBuildMeta.setStatus(BuildStatus.RUNNING.getCode());
            buildMetaMapper.updateByPrimaryKeySelective(updateBuildMeta);

            BuildHistory buildHistory = new BuildHistory();
            buildHistory.setBuildId(Long.valueOf(buildNumber));
            buildHistory.setBuildMetaId(buildMeta.getId());
            buildHistory.setStatus(BuildStatus.RUNNING.getCode());
            buildHistory.setTimeCosuming(0);
            buildHistory.setCreateTime(new Date());
            buildHistoryMapper.insert(buildHistory);
        } catch (Exception e) {
            logger.error("启动任务失败: ", e);
            throw new ServiceException("启动任务失败: ", e);
        }
    }

    @Override
    public void stop(Long projectId) throws ServiceException {
        Project project = projectMapper.selectByPrimaryKey(projectId);

        if (project == null) {
            throw new ServiceException("项目信息不存在：" + projectId);
        }

        BuildMeta buildMeta = buildMetaMapper.selectBuildMetaByProjectId(projectId);
        if (buildMeta == null) {
            // TODO
        }

        // TODO Jenkins服务停止构建

        BuildHistory buildHistory = new BuildHistory();
        buildHistory.setStatus(BuildStatus.TERMINATION.getCode());
        buildHistoryMapper.updateByPrimaryKeySelective(buildHistory);
    }

    @Override
    public void updateBuildStatus(String projectName, Long buildNumber, String status) throws ServiceException {
        int buildStatus = BuildStatus.getCodeByDescription(status);
        if (buildStatus == -1) {
            logger.warn("更新构建状态失败，构建状态无效：" + status);
            return;
        }

        Project project = projectMapper.selectByProjectName(projectName);
        if (project != null) {
            BuildMeta buildMeta = buildMetaMapper.selectBuildMetaByProjectId(project.getId());
            if (buildMeta != null) {
                BuildMeta updateBuildMeta = new BuildMeta();
                updateBuildMeta.setId(buildMeta.getId());
                updateBuildMeta.setStatus(buildStatus);
                buildMetaMapper.updateByPrimaryKeySelective(updateBuildMeta);

                BuildHistory buildHistory = buildHistoryMapper.selectByBuildMetaIdAndBuildId(buildMeta.getId(), buildNumber);
                if (buildHistory != null) {
                    BuildHistory updateBuildHistory = new BuildHistory();
                    updateBuildHistory.setId(buildHistory.getId());
                    updateBuildHistory.setStatus(buildStatus);

                    // 更新构建时长(秒)
                    if (buildHistory.getCreateTime() != null) {
                        Long buildDuration = (System.currentTimeMillis() - buildHistory.getCreateTime().getTime()) / 1000;
                        updateBuildHistory.setTimeCosuming(buildDuration.intValue());
                    }

                    buildHistoryMapper.updateByPrimaryKeySelective(updateBuildHistory);
                }
            }
        }
    }
}
