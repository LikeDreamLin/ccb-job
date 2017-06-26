package com.ccb.job.admin.service;


import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.core.biz.model.ReturnT;

import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface ICcbJobService {
	
	
	public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime);
	
	public ReturnT<String> add(CcbJobInfo jobInfo);
	
	public ReturnT<String> reschedule(CcbJobInfo jobInfo);
	
	public ReturnT<String> remove(String id);
	
	public ReturnT<String> pause(String id);
	
	public ReturnT<String> resume(String id);
	
	public ReturnT<String> triggerJob(String id);

	public Map<String,Object> dashboardInfo();

	public ReturnT<Map<String,Object>> triggerChartDate();

}
