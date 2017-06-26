package com.ccb.job.executor.model;

/**
 * Created by ccb on 2017/6/20.
 */

/**
 *   RestTemplate 调用响应信息
 */
public class RestResponse {


    private String respCode;

    private String respMsg;


    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
