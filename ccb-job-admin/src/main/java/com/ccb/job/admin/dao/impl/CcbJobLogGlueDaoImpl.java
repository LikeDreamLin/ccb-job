package com.ccb.job.admin.dao.impl;

import com.ccb.job.admin.core.model.CcbJobLogGlue;
import com.ccb.job.admin.dao.ICcbJobLogGlueDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:17:52
 */
@Repository
public class CcbJobLogGlueDaoImpl implements ICcbJobLogGlueDao {

	@Resource
	public SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public int save(CcbJobLogGlue xxlJobLogGlue) {
		return sqlSessionTemplate.insert("CcbJobLogGlueMapper.save", xxlJobLogGlue);
	}

	@Override
	public List<CcbJobLogGlue> findByJobId(String jobId) {
		return sqlSessionTemplate.selectList("CcbJobLogGlueMapper.findByJobId", jobId);
	}

	@Override
	public int removeOld(String jobId, int limit) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("jobId", jobId);
		params.put("limit", limit);
		return sqlSessionTemplate.delete("CcbJobLogGlueMapper.removeOld", params);
	}

	@Override
	public int deleteByJobId(String jobId) {
		return sqlSessionTemplate.delete("CcbJobLogGlueMapper.deleteByJobId", jobId);
	}
	
}
