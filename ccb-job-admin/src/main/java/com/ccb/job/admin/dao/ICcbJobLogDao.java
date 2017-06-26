package com.ccb.job.admin.dao;


import com.ccb.job.admin.core.model.CcbJobLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface ICcbJobLogDao {
	
	public List<CcbJobLog> pageList(int offset, int pagesize, int jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd);
	
	public int pageListCount(int offset, int pagesize, int jobGroup, String jobId, Date triggerTimeStart, Date triggerTimeEnd);
	
	public CcbJobLog load(String id);

	public int save(CcbJobLog xxlJobLog);

	public int updateTriggerInfo(CcbJobLog xxlJobLog);

	public int updateHandleInfo(CcbJobLog xxlJobLog);
	
	public int delete(String jobId);

	public int triggerCountByHandleCode(int handleCode);

	public List<Map<String, Object>> triggerCountByDay(Date from, Date to, int handleCode);
	
	public int clearLog(int jobGroup, String jobId, Date clearBeforeTime, int clearBeforeNum);


}
