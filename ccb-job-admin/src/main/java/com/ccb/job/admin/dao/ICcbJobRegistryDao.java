package com.ccb.job.admin.dao;


import com.ccb.job.admin.core.model.CcbJobRegistry;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface ICcbJobRegistryDao {
    public int removeDead(int timeout);

    public List<CcbJobRegistry> findAll(int timeout);

    public int registryUpdate(String registryGroup, String registryKey, String registryValue);

    public int registrySave(String registryGroup, String registryKey, String registryValue);

}
