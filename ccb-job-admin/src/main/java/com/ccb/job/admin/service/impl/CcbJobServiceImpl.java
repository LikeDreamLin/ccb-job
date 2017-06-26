package com.ccb.job.admin.service.impl;

import com.ccb.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.ccb.job.admin.core.model.CcbJobGroup;
import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.ccb.job.admin.core.schedule.CcbJobDynamicScheduler;
import com.ccb.job.admin.core.thread.JobRegistryMonitorHelper;
import com.ccb.job.admin.core.util.UUIDGenerator;
import com.ccb.job.admin.dao.ICcbJobGroupDao;
import com.ccb.job.admin.dao.ICcbJobInfoDao;
import com.ccb.job.admin.dao.ICcbJobLogDao;
import com.ccb.job.admin.dao.ICcbJobLogGlueDao;
import com.ccb.job.admin.service.ICcbJobService;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.enums.ExecutorBlockStrategyEnum;
import com.ccb.job.core.enums.RegistryConfig;
import com.ccb.job.core.glue.GlueTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * core job action for ccb-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class CcbJobServiceImpl implements ICcbJobService {
	private static  Logger logger = LoggerFactory.getLogger(CcbJobServiceImpl.class);

	@Resource
	private ICcbJobGroupDao ccbJobGroupDao;
	@Resource
	private ICcbJobInfoDao ccbJobInfoDao;
	@Resource
	public ICcbJobLogDao ccbJobLogDao;
	@Resource
	private ICcbJobLogGlueDao ccbJobLogGlueDao;
	
	@Override
	public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime) {

		// page list
		List<CcbJobInfo> list = ccbJobInfoDao.pageList(start, length, jobGroup, executorHandler);
		int list_count = ccbJobInfoDao.pageListCount(start, length, jobGroup, executorHandler);
		
		// fill job info
		if (list!=null && list.size()>0) {
			for (CcbJobInfo jobInfo : list) {
				CcbJobDynamicScheduler.fillJobInfo(jobInfo);
			}
		}
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}

	@Override
	public ReturnT<String> add(CcbJobInfo jobInfo) {
		// valid
		CcbJobGroup group = ccbJobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请选择“执行器”");
		}
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“负责人”");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "路由策略非法");
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "失败处理策略非法");
		}
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "运行模式非法非法");
		}
		if (GlueTypeEnum.BEAN==GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“JobHandler”");
		}

		// fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource()!=null) {
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
			String[] childJobKeys = jobInfo.getChildJobKey().split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
				CcbJobInfo childJobInfo = ccbJobInfoDao.loadById(childJobKeyArr[1]);
				if (childJobInfo==null) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// add in db
		try {
			String uuid = UUIDGenerator.getUUID();
			jobInfo.setId(uuid);
			ccbJobInfoDao.save(jobInfo);
		} catch (Exception e2) {
			 return new ReturnT<String>(ReturnT.FAIL_CODE, "新增任务失败:" + e2.getMessage());
		}
		

		// add in quartz
        String qz_group = String.valueOf(jobInfo.getJobGroup());
        String qz_name = String.valueOf(jobInfo.getId());
        try {
            CcbJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
           // CcbJobDynamicScheduler.pauseJob(qz_name, qz_group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error("", e);
            try {
                ccbJobInfoDao.delete(jobInfo.getId());
                CcbJobDynamicScheduler.removeJob(qz_name, qz_group);
            } catch (SchedulerException e1) {
                logger.error("", e1);
            }
            return new ReturnT<String>(ReturnT.FAIL_CODE, "新增任务失败:" + e.getMessage());
        }
	}

	@Override
	public ReturnT<String> reschedule(CcbJobInfo jobInfo) {

		// valid
		if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入格式正确的“Cron”");
		}
		if (StringUtils.isBlank(jobInfo.getJobDesc())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(jobInfo.getAuthor())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“负责人”");
		}
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "路由策略非法");
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
		}
		if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "失败处理策略非法");
		}

		// childJobKey valid
		if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
			String[] childJobKeys = jobInfo.getChildJobKey().split(",");
			for (String childJobKeyItem: childJobKeys) {
				String[] childJobKeyArr = childJobKeyItem.split("_");
				if (childJobKeyArr.length!=2) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
				}
                CcbJobInfo childJobInfo = ccbJobInfoDao.loadById(childJobKeyArr[1]);
				if (childJobInfo==null) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
				}
			}
		}

		// stage job info
		CcbJobInfo exists_jobInfo = ccbJobInfoDao.loadById(jobInfo.getId());
        String old_cron = exists_jobInfo.getJobCron();
		if (exists_jobInfo == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "参数异常");
		}

		exists_jobInfo.setJobCron(jobInfo.getJobCron());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		exists_jobInfo.setExecutorFailStrategy(jobInfo.getExecutorFailStrategy());
		exists_jobInfo.setChildJobKey(jobInfo.getChildJobKey());
        ccbJobInfoDao.update(exists_jobInfo);
        
    	// fresh quartz
    	String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
		String qz_name = String.valueOf(exists_jobInfo.getId());
        try {
            boolean ret = CcbJobDynamicScheduler.rescheduleJob(String.valueOf(exists_jobInfo.getJobGroup()), String.valueOf(exists_jobInfo.getId()), exists_jobInfo.getJobCron());
            return ret?ReturnT.SUCCESS:ReturnT.FAIL;
        } catch (SchedulerException e) {
            logger.error("", e);
        }
		
    	return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> remove(String id) {
		CcbJobInfo ccbJobInfo = ccbJobInfoDao.loadById(id);
        String group = String.valueOf(ccbJobInfo.getJobGroup());
        String name = String.valueOf(ccbJobInfo.getId());

		try {
			CcbJobDynamicScheduler.removeJob(name, group);
			ccbJobInfoDao.delete(id);
			ccbJobLogDao.delete(id);
			ccbJobLogGlueDao.deleteByJobId(id);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}

	@Override
	public ReturnT<String> pause(String id) {
        CcbJobInfo ccbJobInfo = ccbJobInfoDao.loadById(id);
        String group = String.valueOf(ccbJobInfo.getJobGroup());
        String name = String.valueOf(ccbJobInfo.getId());

		try {
            boolean ret = CcbJobDynamicScheduler.pauseJob(name, group);	// jobStatus do not store
            return ret?ReturnT.SUCCESS:ReturnT.FAIL;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> resume(String id) {
        CcbJobInfo ccbJobInfo = ccbJobInfoDao.loadById(id);
        String group = String.valueOf(ccbJobInfo.getJobGroup());
        String name = String.valueOf(ccbJobInfo.getId());

		try {
			boolean ret = CcbJobDynamicScheduler.resumeJob(name, group);
			return ret?ReturnT.SUCCESS:ReturnT.FAIL;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public ReturnT<String> triggerJob(String id) {
        CcbJobInfo ccbJobInfo = ccbJobInfoDao.loadById(id);
        String group = String.valueOf(ccbJobInfo.getJobGroup());
        String name = String.valueOf(ccbJobInfo.getId());

		try {
			CcbJobDynamicScheduler.triggerJob(name, group);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@Override
	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = ccbJobInfoDao.findAllCount();
		int jobLogCount = ccbJobLogDao.triggerCountByHandleCode(-1);
		int jobLogSuccessCount = ccbJobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);

		// executor count
		Set<String> executerAddressSet = new HashSet<String>();
		List<CcbJobGroup> groupList = ccbJobGroupDao.findAll();
		if (CollectionUtils.isNotEmpty(groupList)) {
			for (CcbJobGroup group: groupList) {
				List<String> registryList = null;
				if (group.getAddressType() == 0) {
					registryList = JobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
				} else {
					if (StringUtils.isNotBlank(group.getAddressList())) {
						registryList = Arrays.asList(group.getAddressList().split(","));
					}
				}
				if (CollectionUtils.isNotEmpty(registryList)) {
					executerAddressSet.addAll(registryList);
				}
			}
		}
		int executorCount = executerAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		dashboardMap.put("jobInfoCount", jobInfoCount);
		dashboardMap.put("jobLogCount", jobLogCount);
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		dashboardMap.put("executorCount", executorCount);
		return dashboardMap;
	}

	@Override
	public ReturnT<Map<String, Object>> triggerChartDate() {
		Date from = DateUtils.addDays(new Date(), -30);
		Date to = new Date();

		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<Map<String, Object>> triggerCountMapAll = ccbJobLogDao.triggerCountByDay(from, to, -1);
		List<Map<String, Object>> triggerCountMapSuc = ccbJobLogDao.triggerCountByDay(from, to, ReturnT.SUCCESS_CODE);
		if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
			for (Map<String, Object> item: triggerCountMapAll) {
				String day = String.valueOf(item.get("triggerday"));
				int dayAllCount = Integer.valueOf(String.valueOf(item.get("triggercount")));
				int daySucCount = 0;
				int dayFailCount = dayAllCount - daySucCount;

				if (CollectionUtils.isNotEmpty(triggerCountMapSuc)) {
					for (Map<String, Object> sucItem: triggerCountMapSuc) {
						String daySuc = String.valueOf(sucItem.get("triggerday"));
						if (day.equals(daySuc)) {
							daySucCount = Integer.valueOf(String.valueOf(sucItem.get("triggercount")));
							dayFailCount = dayAllCount - daySucCount;
						}
					}
				}

				triggerDayList.add(day);
				triggerDayCountSucList.add(daySucCount);
				triggerDayCountFailList.add(dayFailCount);
				triggerCountSucTotal += daySucCount;
				triggerCountFailTotal += dayFailCount;
			}
		} else {
            for (int i = 4; i > -1; i--) {
                triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerDayList", triggerDayList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);
		result.put("triggerCountSucTotal", triggerCountSucTotal);
		result.put("triggerCountFailTotal", triggerCountFailTotal);
		return new ReturnT<Map<String, Object>>(result);
	}

}
