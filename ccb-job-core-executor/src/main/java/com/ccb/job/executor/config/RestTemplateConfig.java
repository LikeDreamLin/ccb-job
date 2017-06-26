package com.ccb.job.executor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * 初始化配置RestTemplate客户端
 * (不需要直接引用,后续使用,只需要使用RestTemplate)
 * @author LL
 */
@Configuration
public class RestTemplateConfig {
	
	private static  Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);
	
	@Value("${client.readTimeout: 5000}")
	private int readTimeout;
	
	@Value("${client.connTimeout: 5000}")
	private int connTimeout;

	@Bean
    @LoadBalanced
	public RestTemplate restTemplate () {
		log.info("初始化restTemplate对象....");
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connTimeout);
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        return restTemplate;
        
//        添加转换器
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
//        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
//        messageConverters.add(new FormHttpMessageConverter());
//        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
//        messageConverters.add(new MappingJackson2HttpMessageConverter());
//        restTemplate = new RestTemplate(messageConverters);
//        restTemplate.setRequestFactory(factory);
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
	}
}
