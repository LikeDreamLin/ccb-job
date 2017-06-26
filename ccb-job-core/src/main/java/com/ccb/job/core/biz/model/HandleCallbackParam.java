package com.ccb.job.core.biz.model;

import java.io.Serializable;

/**
 * Created by xuxueli on 17/3/2.
 */
public class HandleCallbackParam implements Serializable {
    private static  final long serialVersionUID = 42L;

    private String logId;
    private ReturnT<String> executeResult;

    public HandleCallbackParam(){}
    public HandleCallbackParam(String logId, ReturnT<String> executeResult) {
        this.logId = logId;
        this.executeResult = executeResult;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public ReturnT<String> getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(ReturnT<String> executeResult) {
        this.executeResult = executeResult;
    }
    @Override
    public String toString() {
        return "HandleCallbackParam{" +
                "logId=" + logId +
                ", executeResult=" + executeResult +
                '}';
    }
}
