package com.ccb.job.core.biz.impl;


import com.ccb.job.core.biz.ExecutorBiz;
import com.ccb.job.core.biz.model.LogResult;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.biz.model.TriggerParam;
import com.ccb.job.core.enums.ExecutorBlockStrategyEnum;
import com.ccb.job.core.executor.CcbJobExecutor;
import com.ccb.job.core.glue.GlueFactory;
import com.ccb.job.core.glue.GlueTypeEnum;
import com.ccb.job.core.handler.IJobHandler;
import com.ccb.job.core.handler.impl.GlueJobHandler;
import com.ccb.job.core.handler.impl.ScriptJobHandler;
import com.ccb.job.core.log.CcbJobFileAppender;
import com.ccb.job.core.thread.JobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> kill(String jobId) {
        // kill handlerThread, and create new one
        JobThread jobThread = CcbJobExecutor.loadJobThread(jobId);
        if (jobThread != null) {
        	CcbJobExecutor.removeJobThread(jobId, "人工手动终止");
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.SUCCESS_CODE, "job thread aleady killed.");
    }

    @Override
    public ReturnT<LogResult> log(long logDateTim, String logId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
        String logFileName = CcbJobFileAppender.makeLogFileName(new Date(logDateTim), logId);

        LogResult logResult = CcbJobFileAppender.readLog(logFileName, fromLineNum);
        return new ReturnT<LogResult>(logResult);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // load old thread
        JobThread jobThread = CcbJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread!=null?jobThread.getHandler():null;
        String removeOldReason = null;
        
        if (GlueTypeEnum.BEAN==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // valid exists job thread：change handler, need kill old thread
            // valid old jobThread
            if (jobThread != null && jobHandler!=null && jobThread.getHandler() != jobHandler) {
                // change handler, need kill old thread
                removeOldReason = "更新JobHandler或更换任务模式,终止旧任务线程";
                jobThread = null;
                jobHandler = null;
            }

         // valid handler
            if (jobHandler == null) {
                jobHandler = CcbJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
                if (jobHandler == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }
        } else if (GlueTypeEnum.GLUE_GROOVY==GlueTypeEnum.match(triggerParam.getGlueType())) {

        	 // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                        && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "更新任务逻辑或更换任务模式,终止旧任务线程";

                jobThread = null;
                jobHandler = null;
            }

         // valid handler
            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error("", e);
                    return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                }
            }
        } else if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(triggerParam.getGlueType())
                || GlueTypeEnum.GLUE_PYTHON==GlueTypeEnum.match(triggerParam.getGlueType()) ) {


            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "更新任务逻辑或更换任务模式,终止旧任务线程";

                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略-生效："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "阻塞处理策略-生效：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = CcbJobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

}
