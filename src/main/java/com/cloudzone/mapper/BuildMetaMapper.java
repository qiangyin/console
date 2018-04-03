package com.cloudzone.mapper;

import com.cloudzone.common.entity.BuildMeta;
import com.cloudzone.common.entity.jenkins.BuildDetailVO;
import com.cloudzone.common.entity.jenkins.BuildProjectVO;

import java.util.List;

public interface BuildMetaMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuildMeta record);

    int insertSelective(BuildMeta record);

    BuildMeta selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuildMeta record);

    int updateByPrimaryKey(BuildMeta record);

    BuildMeta selectBuildMetaByProjectId(Long projectId);

    List<BuildProjectVO> selectBuildProjects();

    BuildDetailVO selectBuildDetailByProjectId(Long projectId);

}