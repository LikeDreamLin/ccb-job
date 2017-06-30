ccb-job 是在开源框架xxl-job（https://github.com/xuxueli/xxl-job） 上进行的改动 ，适用于本公司的分布式定时任务需求

改动情况：


1.ccb-job 由原来的（xxl-job） Maven web 工程改成 spring boot工程，作为本公司微服务框架的一部分，注册在consul上面，通过consul对服务进行监控管理

2.数据库脚本有由原来的mysql数据库改成informix

3.数据库连接池由原来c3p0改成了alibaba ， informix与c3p0搭配使用报一堆的提醒

4.ccb-job中的执行器只是具有服务转发请求的功能，没有具体的业务逻辑


5.。。。