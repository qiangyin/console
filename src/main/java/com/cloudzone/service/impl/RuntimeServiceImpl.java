package com.cloudzone.service.impl;

import com.cloudzone.common.entity.BuildConfig;
import com.cloudzone.common.entity.RuntimeVo;
import com.cloudzone.mapper.BuildConfigMapper;
import com.cloudzone.mapper.RuntimeConfigMapper;
import com.cloudzone.mapper.ServiceListMapper;
import com.cloudzone.service.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.J;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;
import static sun.security.krb5.SCDynamicStoreConfig.getConfig;

/**
 * @author chenjunjie
 * @since 2018-03-21
 */
@Service("runtimeServiceImpl")
public class RuntimeServiceImpl implements RuntimeService {
    private final Logger logger = LoggerFactory.getLogger(RuntimeServiceImpl.class);
    private final static  long parentId = (long)0;

    @Autowired
    private RuntimeConfigMapper runtimeConfigMapper;

    @Override
    public List<RuntimeVo> get() {
        List<RuntimeVo> list = new ArrayList<RuntimeVo>();

        // 找语言种类
        List<BuildConfig> buildConfigList = runtimeConfigMapper.selectByParentId(parentId);

        int i = 0;
        for(BuildConfig bc: buildConfigList){
            logger.info("runtime language type ={}", bc.getName());
            RuntimeVo temp = new RuntimeVo();
            List<BuildConfig> buildConfigList_cur = runtimeConfigMapper.selectByName(bc.getName());
            temp.setLanguage(bc.getName());
            List<String> configsList = new ArrayList<String>();
            for(BuildConfig bc_cur: buildConfigList_cur){
                if (bc_cur.getParentId() != parentId) {
                    configsList.add(bc_cur.getConfig());
                }
            }
            temp.setConfig(configsList);
            i++;
            list.add(temp);
        }

        return list;
    }
}
