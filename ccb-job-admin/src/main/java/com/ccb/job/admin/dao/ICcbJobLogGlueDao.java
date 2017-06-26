package com.ccb.job.admin.dao;


import com.ccb.job.admin.core.model.CcbJobLogGlue;

import java.util.List;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface ICcbJobLogGlueDao {
	
	public int save(CcbJobLogGlue xxlJobLogGlue);
	
	public List<CcbJobLogGlue> findByJobId(String jobId);

	public int removeOld(String jobId, int limit);

	public int deleteByJobId(String jobId);
	
}
