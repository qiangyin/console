package com.cloudzone.mapper;

import com.cloudzone.common.entity.BuildHistory;
import com.cloudzone.common.entity.jenkins.BuildHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BuildHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuildHistory record);

    int insertSelective(BuildHistory record);

    BuildHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuildHistory record);

    int updateByPrimaryKey(BuildHistory record);

    List<BuildHistoryVO> selectBuildHistories();

    List<BuildHistoryVO> selectBuildHistoriesByProjectId(Long projectId);

    int updateStatusByBuildMetaIdAndBuildId(BuildHistory record);

    /**
     * 根据构建配置Id和构建编号查询构建历史
     *
     * @param buildMetaId 构建配置Id
     * @param buildId     构建编号
     * @return BuildHistory
     */
    BuildHistory selectByBuildMetaIdAndBuildId(@Param("buildMetaId") Long buildMetaId, @Param("buildId") Long buildId);
}