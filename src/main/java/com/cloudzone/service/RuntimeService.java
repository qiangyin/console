package com.cloudzone.service;

import com.cloudzone.common.entity.Project;
import com.cloudzone.common.entity.RuntimeVo;
import com.cloudzone.common.entity.project.ProjectVO;

import java.util.List;

/**
 * @author chenjunjie
 * @since 2018-03-21
 */
public interface RuntimeService {
    /**
     * 获取运行配置
     * @return RuntimeVo
     */
    List<RuntimeVo> get();
}
