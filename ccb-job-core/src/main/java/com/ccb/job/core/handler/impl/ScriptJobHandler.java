package com.ccb.job.core.handler.impl;

import com.ccb.job.core.biz.model.ReturnT;
import com.ccb.job.core.executor.CcbJobExecutor;
import com.ccb.job.core.glue.GlueTypeEnum;
import com.ccb.job.core.handler.IJobHandler;
import com.ccb.job.core.log.CcbJobFileAppender;
import com.ccb.job.core.log.CcbJobLogger;
import com.ccb.job.core.util.ScriptUtil;

/**
 * Created by xuxueli on 17/4/27.
 */
public class ScriptJobHandler extends IJobHandler {

    private String jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(String jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;
    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public ReturnT<String> execute(String... params) throws Exception {

        // cmd + script-file-name
        String cmd = "bash";
        String scriptFileName = null;
        if (GlueTypeEnum.GLUE_SHELL == glueType) {
            cmd = "bash";
            scriptFileName = CcbJobExecutor.logPath.concat("gluesource/").concat(String.valueOf(jobId)).concat("_").concat(String.valueOf(glueUpdatetime)).concat(".sh");
        } else if (GlueTypeEnum.GLUE_PYTHON == glueType) {
            cmd = "python";
            scriptFileName = CcbJobExecutor.logPath.concat("gluesource/").concat(String.valueOf(jobId)).concat("_").concat(String.valueOf(glueUpdatetime)).concat(".py");
        }

        // make script file
        ScriptUtil.markScriptFile(scriptFileName, gluesource);

        // log file
        String logFileName = CcbJobExecutor.logPath.concat(CcbJobFileAppender.contextHolder.get());

        // invoke
        CcbJobLogger.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, params);
        ReturnT<String> result = (exitValue==0)?ReturnT.SUCCESS:new ReturnT<String>(ReturnT.FAIL_CODE, "script exit value("+exitValue+") is failed");
        return result;
    }

}
