package com.ccb.job.admin.dao.impl;

import com.ccb.job.admin.core.model.CcbJobLog;
import com.ccb.job.admin.dao.ICcbJobLogDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
@Repository
public class CcbJobLogDaoImpl implements ICcbJobLogDao {
	
	@Resource
	public SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<CcbJobLog> pageList(int offset, int pagesize, int jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("jobId", jobId);
		params.put("triggerTimeStart", triggerTimeStart);
		params.put("triggerTimeEnd", triggerTimeEnd);
		
		return sqlSessionTemplate.selectList("CcbJobLogMapper.pageList", params);
	}

	@Override
	public int pageListCount(int offset, int pagesize, int jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("pagesize", pagesize);
		params.put("jobGroup", jobGroup);
		params.put("jobId", jobId);
		params.put("triggerTimeStart", triggerTimeStart);
		params.put("triggerTimeEnd", triggerTimeEnd);
		
		return sqlSessionTemplate.selectOne("CcbJobLogMapper.pageListCount", params);
	}

	@Override
	public CcbJobLog load(String id) {
		return sqlSessionTemplate.selectOne("CcbJobLogMapper.load", id);
	}

	@Override
	public int save(CcbJobLog xxlJobLog) {
		return sqlSessionTemplate.insert("CcbJobLogMapper.save", xxlJobLog);
	}

	@Override
	public int updateTriggerInfo(CcbJobLog xxlJobLog) {
		if (xxlJobLog.getTriggerMsg()!=null && xxlJobLog.getTriggerMsg().length()>2000) {
			xxlJobLog.setTriggerMsg(xxlJobLog.getTriggerMsg().substring(0, 2000));
		}
		return sqlSessionTemplate.update("CcbJobLogMapper.updateTriggerInfo", xxlJobLog);
	}

	@Override
	public int updateHandleInfo(CcbJobLog xxlJobLog) {
		if (xxlJobLog.getHandleMsg()!=null && xxlJobLog.getHandleMsg().length()>2000) {
			xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg().substring(0, 2000));
		}
		return sqlSessionTemplate.update("CcbJobLogMapper.updateHandleInfo", xxlJobLog);
	}
	@Override
	public int delete(String jobId) {
		return sqlSessionTemplate.delete("CcbJobLogMapper.delete", jobId);
	}

	@Override
	public int triggerCountByHandleCode(int handleCode) {
		return sqlSessionTemplate.selectOne("CcbJobLogMapper.triggerCountByHandleCode", handleCode);
	}

	@Override
	public List<Map<String, Object>> triggerCountByDay(Date from, Date to, int handleCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("from", from);
		params.put("to", to);
		params.put("handleCode", handleCode);
		return sqlSessionTemplate.selectList("CcbJobLogMapper.triggerCountByDay", params);
	}
	
	@Override
	public int clearLog(int jobGroup, String jobId, Date clearBeforeTime, int clearBeforeNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobGroup", jobGroup);
		params.put("jobId", jobId);
		params.put("clearBeforeTime", clearBeforeTime);
		params.put("clearBeforeNum", clearBeforeNum);
		return sqlSessionTemplate.delete("CcbJobLogMapper.clearLog", params);
	}

}
