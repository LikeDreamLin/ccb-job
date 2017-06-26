package com.ccb.job.admin.dao;


import com.ccb.job.admin.core.model.CcbJobInfo;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface ICcbJobInfoDao {

	public List<CcbJobInfo> pageList(int offset, int pagesize, int jobGroup, String executorHandler);
	public int pageListCount(int offset, int pagesize, int jobGroup, String executorHandler);
	
	public int save(CcbJobInfo info);

	public CcbJobInfo loadById(String id);
	
	public int update(CcbJobInfo item);
	
	public int delete(String id);

	public List<CcbJobInfo> getJobsByGroup(int jobGroup);

	public int findAllCount();

}
