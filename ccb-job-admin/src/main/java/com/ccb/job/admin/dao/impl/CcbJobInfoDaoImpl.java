package com.ccb.job.admin.dao.impl;

import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.admin.dao.ICcbJobInfoDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Repository
public class CcbJobInfoDaoImpl implements ICcbJobInfoDao {
	
	@Resource
	public SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<CcbJobInfo> pageList(int offset, int pagesize, int jobGroup, String executorHandler) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("executorHandler", executorHandler);
		
		return sqlSessionTemplate.selectList("CcbJobInfoMapper.pageList", params);
	}

	@Override
	public int pageListCount(int offset, int pagesize, int jobGroup, String executorHandler) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("executorHandler", executorHandler);
		
		return sqlSessionTemplate.selectOne("CcbJobInfoMapper.pageListCount", params);
	}

	@Override
	public int save(CcbJobInfo info) {
		return sqlSessionTemplate.insert("CcbJobInfoMapper.save", info);
	}

	@Override
	public CcbJobInfo loadById(String id) {
		return sqlSessionTemplate.selectOne("CcbJobInfoMapper.loadById", id);
	}

	@Override
	public int update(CcbJobInfo item) {
		return sqlSessionTemplate.update("CcbJobInfoMapper.update", item);
	}

	@Override
	public int delete(String id) {
		return sqlSessionTemplate.update("CcbJobInfoMapper.delete", id);
	}

	@Override
	public List<CcbJobInfo> getJobsByGroup(int jobGroup) {
		return sqlSessionTemplate.selectList("CcbJobInfoMapper.getJobsByGroup", jobGroup);
	}

	@Override
	public int findAllCount() {
		return sqlSessionTemplate.selectOne("CcbJobInfoMapper.findAllCount");
	}

}
