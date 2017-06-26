package com.ccb.job.admin.dao.impl;

import com.ccb.job.admin.core.model.CcbJobGroup;
import com.ccb.job.admin.dao.ICcbJobGroupDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public class CcbJobGroupDaoImpl implements ICcbJobGroupDao {

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<CcbJobGroup> findAll() {
        return sqlSessionTemplate.selectList("CcbJobGroupMapper.findAll");
    }

    @Override
    public int save(CcbJobGroup xxlJobGroup) {
        return sqlSessionTemplate.update("CcbJobGroupMapper.save", xxlJobGroup);
    }

    @Override
    public int update(CcbJobGroup xxlJobGroup) {
        return sqlSessionTemplate.update("CcbJobGroupMapper.update", xxlJobGroup);
    }

    @Override
    public int remove(int id) {
        return sqlSessionTemplate.delete("CcbJobGroupMapper.remove", id);
    }

    @Override
    public CcbJobGroup load(int id) {
        return sqlSessionTemplate.selectOne("CcbJobGroupMapper.load", id);
    }


}
