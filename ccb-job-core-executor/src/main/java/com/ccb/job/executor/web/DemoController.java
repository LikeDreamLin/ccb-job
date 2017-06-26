package com.ccb.job.executor.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ccb on 2017/6/20.
 */
@RestController
public class DemoController {

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public boolean hello(){
        boolean ret = restTemplate.getForObject("http://server-sms/ccbJob",boolean.class);
        return ret;
    }
}
