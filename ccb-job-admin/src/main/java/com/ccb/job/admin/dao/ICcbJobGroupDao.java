package com.ccb.job.admin.dao;


import com.ccb.job.admin.core.model.CcbJobGroup;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Configuration
public interface ICcbJobGroupDao {

    public List<CcbJobGroup> findAll();

    public int save(CcbJobGroup xxlJobGroup);

    public int update(CcbJobGroup xxlJobGroup);

    public int remove(int id);

    public CcbJobGroup load(int id);
}
