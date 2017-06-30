package com.ccb.job.admin.core.util;

import com.alibaba.fastjson.JSON;
import com.ccb.job.admin.core.model.ConsulKV;


import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ccb on 2017/6/27.
 */
@Configuration
public class SmsUtil {

    private static Logger logger = LoggerFactory.getLogger(SmsUtil.class);

    /**
     * 批量多笔短信发送
     *
     * @param phoneNoList
     * @param content
     * @return
     * @throws
     * @throws
     */
    public  static  ArrayList<HashMap<String, String>> sendMulitMsg(Set<String> phoneNoList, String content) {
        logger.debug("下行单笔短信群发：" + "phoneNoList = [" + phoneNoList + "],content = [" + content + "],url = [" + ConsulKV.smsUrl + "]");
        ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < phoneNoList.size(); i++) {
            for (String phoneNo : phoneNoList) {
                //List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("phoneNo", phoneNo);
                map.put("retMsg", sendSingleMsg(phoneNo, content));
                res.add(map);
            }

        }
        return res;
     }

/*
    public static  String sendSingleMsg(String  phoneNo, String content){
        Map<String,String> map = new HashMap<>();
        map.put(phoneNo,"phoneNo");
        map.put(content,"content");
        return HttpClientUtil.postRequest(smsUrl,map);
    }
*/

  /**
            * 单笔短信发送
    *
            * @param phoneNo
    * @param content
    * @param
    * @return
            * @throws ClientProtocolException
    * @throws IOException
    */
    public static  String sendSingleMsg(String phoneNo, String content) {
        logger.info("下行单笔短信发送：" + "phoneNo = [" + phoneNo + "],content = [" + content + "],url = [" + ConsulKV.smsUrl + "]");
        String res = "";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("phoneNo", phoneNo);
        map.put("content", content);
        String sndJsonMsg = null;
        try {
            sndJsonMsg = java.net.URLEncoder.encode(JSON.toJSONString(map),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("编码不支持",e);
        }
        if(!StringUtils.isBlank(sndJsonMsg)) {
            try {
                HttpResponse response = HttpRequest.post(ConsulKV.smsUrl ).form("sndJsonMsg", sndJsonMsg).queryEncoding("UTF-8").timeout(6 * 1000).send();
                logger.info("HttpResponse:{}", response);
                res = response.bodyText();
            } catch (Exception e) {
                logger.error("下行短信发送失败", e);
            }
        }

        return res;
    }





}
