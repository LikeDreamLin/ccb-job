package com.ccb.job.admin.controller;

import com.ccb.job.admin.core.model.CcbJobGroup;
import com.ccb.job.admin.core.thread.JobRegistryMonitorHelper;
import com.ccb.job.admin.dao.ICcbJobGroupDao;
import com.ccb.job.admin.dao.ICcbJobInfoDao;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.enums.RegistryConfig;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	private static  Logger logger = LoggerFactory.getLogger(JobGroupController.class);

	@Resource
	public ICcbJobInfoDao ccbJobInfoDao;
	@Resource
	public ICcbJobGroupDao ccbJobGroupDao;

	@Autowired
	private ConsulClient consulClient;

	@Value("${spring.application.name}")
	private String serverName;



	@RequestMapping
	public String index(Model model) {
		// job admin
		Set<String> adminAddressList = new HashSet<String>();
		List<HealthService>  response = consulClient.getHealthServices(serverName, true, null).getValue();
		for (HealthService service : response) {
			logger.info(service.toString());
			String address = service.getService().getAddress();
			int port =  service.getService().getPort();
			adminAddressList.add(address +":"+port);

		}
		// job group (executor)
		List<CcbJobGroup> list = ccbJobGroupDao.findAll();

		if (CollectionUtils.isNotEmpty(list)) {
			for (CcbJobGroup group: list) {
				List<String> registryList = null;
				if (group.getAddressType() == 0) {
					registryList = JobRegistryMonitorHelper.discover(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppName());
				} else {
					if (StringUtils.isNotBlank(group.getAddressList())) {
						registryList = Arrays.asList(group.getAddressList().split(","));
					}
				}
				group.setRegistryList(registryList);
			}
		}
		model.addAttribute("adminAddressList", adminAddressList);
		model.addAttribute("list", list);
		return "jobgroup/jobgroup.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(CcbJobGroup ccbJobGroup){

		// valid
		if (ccbJobGroup.getAppName()==null || StringUtils.isBlank(ccbJobGroup.getAppName())) {
			return new ReturnT<String>(500, "请输入AppName");
		}
		if (ccbJobGroup.getAppName().length()>64) {
			return new ReturnT<String>(500, "AppName长度限制为4~64");
		}
		if (ccbJobGroup.getTitle()==null || StringUtils.isBlank(ccbJobGroup.getTitle())) {
			return new ReturnT<String>(500, "请输入名称");
		}
		if (ccbJobGroup.getAddressType()!=0) {
			if (StringUtils.isBlank(ccbJobGroup.getAddressList())) {
				return new ReturnT<String>(500, "手动录入注册方式，机器地址不可为空");
			}
			String[] addresss = ccbJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringUtils.isBlank(item)) {
					return new ReturnT<String>(500, "机器地址非法");
				}
			}
		}

		int ret = ccbJobGroupDao.save(ccbJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(CcbJobGroup ccbJobGroup){
		// valid
		if (ccbJobGroup.getAppName()==null || StringUtils.isBlank(ccbJobGroup.getAppName())) {
			return new ReturnT<String>(500, "请输入AppName");
		}
		if (ccbJobGroup.getAppName().length()>64) {
			return new ReturnT<String>(500, "AppName长度限制为4~64");
		}
		if (ccbJobGroup.getTitle()==null || StringUtils.isBlank(ccbJobGroup.getTitle())) {
			return new ReturnT<String>(500, "请输入名称");
		}
		if (ccbJobGroup.getAddressType()!=0) {
			if (StringUtils.isBlank(ccbJobGroup.getAddressList())) {
				return new ReturnT<String>(500, "手动录入注册方式，机器地址不可为空");
			}
			String[] addresss = ccbJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (StringUtils.isBlank(item)) {
					return new ReturnT<String>(500, "机器地址非法");
				}
			}
		}

		int ret = ccbJobGroupDao.update(ccbJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id){

		// valid
		int count = ccbJobInfoDao.pageListCount(0, 10, id, null);
		if (count > 0) {
			return new ReturnT<String>(500, "该分组使用中, 不可删除");
		}

		List<CcbJobGroup> allList = ccbJobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<String>(500, "删除失败, 系统需要至少预留一个默认分组");
		}

		int ret = ccbJobGroupDao.remove(id);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
