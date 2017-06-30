package com.ccb.job.admin.core.model;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by ccb on 2017/6/27.
 */

/**
 *  处理consul上面的KV
 */
@Component
@Configuration
public class ConsulKV {

    public static String smsUrl;

    public static String username;

    public static  String password;

    @Value("${ssbp.smsUrl}")
    public  void setSmsUrl(String smsUrl) {
        ConsulKV.smsUrl = smsUrl;
    }

    @Value("${ccb.job.login.username}")
    public  void setUsername(String username) {
        ConsulKV.username = username;
    }

    @Value("${ccb.job.login.password}")
    public  void setPassword(String password) {
        ConsulKV.password = password;
    }
}
