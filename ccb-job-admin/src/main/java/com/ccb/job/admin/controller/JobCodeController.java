package com.ccb.job.admin.controller;

import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.admin.core.model.CcbJobLogGlue;
import com.ccb.job.admin.dao.ICcbJobInfoDao;
import com.ccb.job.admin.dao.ICcbJobLogGlueDao;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.glue.GlueTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * job code controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController {
	
	@Resource
	private ICcbJobInfoDao ccbJobInfoDao;
	@Resource
	private ICcbJobLogGlueDao ccbJobLogGlueDao;

	@RequestMapping
	public String index(Model model, String jobId) {
		CcbJobInfo jobInfo = ccbJobInfoDao.loadById(jobId);
		List<CcbJobLogGlue> jobLogGlues = ccbJobLogGlueDao.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException("抱歉，任务不存在.");
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException("该任务非GLUE模式.");
		}

		// Glue类型-字典
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Model model, String id, String glueSource, String glueRemark) {
		// valid
		if (glueRemark==null) {
			return new ReturnT<String>(500, "请输入备注");
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return new ReturnT<String>(500, "备注长度应该在4至100之间");
		}
		CcbJobInfo exists_jobInfo = ccbJobInfoDao.loadById(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, "参数异常");
		}
		
		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());
		ccbJobInfoDao.update(exists_jobInfo);

		// log old code
		CcbJobLogGlue ccbJobLogGlue = new CcbJobLogGlue();
		ccbJobLogGlue.setJobId(exists_jobInfo.getId());
		ccbJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		ccbJobLogGlue.setGlueSource(glueSource);
		ccbJobLogGlue.setGlueRemark(glueRemark);
		ccbJobLogGlueDao.save(ccbJobLogGlue);

		// remove code backup more than 30
		ccbJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);

		return ReturnT.SUCCESS;
	}
	
}
