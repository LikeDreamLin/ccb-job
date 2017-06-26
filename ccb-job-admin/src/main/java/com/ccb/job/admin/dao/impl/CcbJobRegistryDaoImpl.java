package com.ccb.job.admin.dao.impl;

import com.ccb.job.admin.core.model.CcbJobRegistry;
import com.ccb.job.admin.dao.ICcbJobRegistryDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public class CcbJobRegistryDaoImpl implements ICcbJobRegistryDao {

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int removeDead(int timeout) {
        return sqlSessionTemplate.delete("CcbJobRegistryMapper.removeDead", timeout);
    }

    @Override
    public List<CcbJobRegistry> findAll(int timeout) {
        return sqlSessionTemplate.selectList("CcbJobRegistryMapper.findAll", timeout);
    }

    @Override
    public int registryUpdate(String registryGroup, String registryKey, String registryValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("registryGroup", registryGroup);
        params.put("registryKey", registryKey);
        params.put("registryValue", registryValue);

        return sqlSessionTemplate.update("CcbJobRegistryMapper.registryUpdate", params);
    }

    @Override
    public int registrySave(String registryGroup, String registryKey, String registryValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("registryGroup", registryGroup);
        params.put("registryKey", registryKey);
        params.put("registryValue", registryValue);

        return sqlSessionTemplate.update("CcbJobRegistryMapper.registrySave", params);
    }

}
