package com.ccb.job.core.handler.impl;

import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.handler.IJobHandler;
import com.ccb.job.core.log.CcbJobLogger;

/**
 * glue job handler
 * @author xuxueli 2016-5-19 21:05:45
 */
public class GlueJobHandler extends IJobHandler {

	private long glueUpdatetime;
	private IJobHandler jobHandler;
	public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
		this.jobHandler = jobHandler;
		this.glueUpdatetime = glueUpdatetime;
	}
	public long getGlueUpdatetime() {
		return glueUpdatetime;
	}

	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		CcbJobLogger.log("----------- glue.version:"+ glueUpdatetime +" -----------");
		return jobHandler.execute(params);
	}

}
