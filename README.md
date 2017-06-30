ccb-job 是在开源框架xxl-job（https://github.com/xuxueli/xxl-job） 上进行的改动 ，适用于本公司的分布式定时任务需求

改动情况：


1.ccb-job 由原来的（xxl-job） Maven web 工程改成 spring boot工程，作为本公司微服务框架的一部分，注册在consul上面，通过consul对服务进行监控管理

2.数据库脚本有由原来的mysql数据库改成informix

3.数据库连接池由原来c3p0改成了alibaba ， informix与c3p0搭配使用报一堆的提醒

4.ccb-job中的执行器只是具有服务转发请求的功能，没有具体的业务逻辑


5.原来的配置文件通过spring cloud consul config 迁移到consul 的控制台

6.。。。




consul 上面的配置信息：


config/ccb-job-admin/configuration:
#登陆信息
ccb:
  job:
    log: /home/ap/ssbp/log/ccb-job/ccb-job-admin.log
    login:
      username: admin
      password: 123456


config/ccb-job-core-executor/configuration
### xxl-job executor address
ccb:
 job:
  admin:
   addresses: http://127.0.0.1:5555
  executor:
   appname: ccb-job-core-executor
   port: 8888
   logpath: /home/ap/ssbp/log/ccb-job/ccb-job-executor
   #ip:


config/application,dev/configuration
spring:
  datasource:
    ssbp:
      type: com.alibaba.druid.pool.DruidDataSource
      url: jdbc:informix-sqli://localhost:28888/ssbpkf:INFORMIXSERVER=middb;NEWLOACLE=en_us,zh_cn,zh_tw;NEWCODESET=GBK,8859-1,819,Big5;IFX_USE_STRENC=true
      username: ssbpkf
      password: ssbpkf
      driver-class-name: com.informix.jdbc.IfxDriver
      max-active: 10
      max-idle: 3
      min-idle: 3
      initial-size: 5
      filters: stat,slf4j
      connectionInitSqls: "SET LOCK MODE TO WAIT 5"
