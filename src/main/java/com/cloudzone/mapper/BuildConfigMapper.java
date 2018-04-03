package com.cloudzone.mapper;

import com.cloudzone.common.entity.BuildConfig;
import com.cloudzone.common.entity.jenkins.BuildProjectVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BuildConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BuildConfig record);

    int insertSelective(BuildConfig record);

    BuildConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BuildConfig record);

    int updateByPrimaryKey(BuildConfig record);

    /**
     * 根据语言名称和语言版本查询构建配置
     *
     * @param languageName rule id
     * @param version page number
     * @return BuildConfig
     */
    BuildConfig selectByLanguageNameAndVersion(@Param("name") String languageName, @Param("config") String version);
}