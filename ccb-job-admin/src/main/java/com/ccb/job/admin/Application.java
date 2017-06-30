package com.ccb.job.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ccb on 2017/1/20.
 */


@SpringBootApplication
@EnableDiscoveryClient
@ImportResource("classpath*:/spring/*.xml")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
