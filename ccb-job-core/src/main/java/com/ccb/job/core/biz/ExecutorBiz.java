package com.ccb.job.core.biz;


import com.ccb.job.core.biz.model.LogResult;
import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.biz.model.TriggerParam;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     * @return
     */
    public ReturnT<String> beat();

    /**
     * kill
     * @param jobId
     * @return
     */
    public ReturnT<String> kill(String jobId);

    /**
     * log
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
    public ReturnT<LogResult> log(long logDateTim, String logId, int fromLineNum);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<String> run(TriggerParam triggerParam);

}
