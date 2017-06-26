package com.ccb.job.admin.controller;

import com.ccb.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.ccb.job.admin.core.model.CcbJobGroup;
import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.ccb.job.admin.dao.ICcbJobGroupDao;
import com.ccb.job.admin.service.ICcbJobService;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.enums.ExecutorBlockStrategyEnum;
import com.ccb.job.core.glue.GlueTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {

	@Resource
	private ICcbJobGroupDao ccbJobGroupDao;
	@Resource
	private ICcbJobService ccbJobService;
	
	@RequestMapping
	public String index(Model model) {
		
		
		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	// 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	// 阻塞处理策略-字典
		model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());		// 失败处理策略-字典
	
		// 任务组
		List<CcbJobGroup> jobGroupList =  ccbJobGroupDao.findAll();
		model.addAttribute("JobGroupList", jobGroupList);
		return "jobinfo/jobinfo.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										int jobGroup, String executorHandler, String filterTime) {
		
		return ccbJobService.pageList(start, length, jobGroup, executorHandler, filterTime);
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(CcbJobInfo jobInfo) {
		return ccbJobService.add(jobInfo);
	}
	
	@RequestMapping("/reschedule")
	@ResponseBody
	public ReturnT<String> reschedule(CcbJobInfo jobInfo) {
		return ccbJobService.reschedule(jobInfo);
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String id) {
		return ccbJobService.remove(id);
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(String id) {
		return ccbJobService.pause(id);
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(String id) {
		return ccbJobService.resume(id);
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(String id) {
		return ccbJobService.triggerJob(id);
	}
	
}
