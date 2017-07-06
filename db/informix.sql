CREATE TABLE XXL_JOB_QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) ,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BYTE,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);


    
    CREATE TABLE XXL_JOB_QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) ,
    NEXT_FIRE_TIME decimal(13),
    PREV_FIRE_TIME decimal(13),
    PRIORITY INTEGER ,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME decimal(13) NOT NULL,
    END_TIME decimal(13) ,
    CALENDAR_NAME VARCHAR(200) ,
    MISFIRE_INSTR INTEGER  ,
    JOB_DATA BYTE ,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES XXL_JOB_QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);
    
    
    


CREATE TABLE XXL_JOB_QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT decimal(7) NOT NULL,
    REPEAT_INTERVAL decimal(12) NOT NULL,
    TIMES_TRIGGERED decimal(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);



CREATE TABLE XXL_JOB_QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_SIMPROP_TRIGGERS
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 LVARCHAR(512) ,
    STR_PROP_2 LVARCHAR(512) ,
    STR_PROP_3 LVARCHAR(512) ,
    INT_PROP_1 INT ,
    INT_PROP_2 INT ,
    LONG_PROP_1 decimal(20) ,
    LONG_PROP_2 decimal(20) ,
    DEC_PROP_1 decimal(13,4) ,
    DEC_PROP_2 decimal(13,4) ,
    BOOL_PROP_1 VARCHAR(1) ,
    BOOL_PROP_2 VARCHAR(1) ,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);



CREATE TABLE XXL_JOB_QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BYTE ,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);


CREATE TABLE XXL_JOB_QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BYTE NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);


CREATE TABLE XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);


CREATE TABLE XXL_JOB_QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME decimal(20) NOT NULL,
    SCHED_TIME decimal(20) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) ,
    JOB_GROUP VARCHAR(200) ,
    IS_NONCONCURRENT VARCHAR(1) ,
    REQUESTS_RECOVERY VARCHAR(1) ,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
)





CREATE TABLE XXL_JOB_QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME decimal(20) NOT NULL,
    CHECKIN_INTERVAL decimal(20) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);





CREATE TABLE XXL_JOB_QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);


CREATE TABLE XXL_JOB_QRTZ_TRIGGER_INFO(
  id varchar(32) NOT NULL,
  job_group int NOT NULL ,
  job_cron varchar(128) NOT NULL  ,
   job_desc  varchar(255) NOT NULL,
   add_time  datetime  year to second,
   update_time  datetime year to second,
   author  varchar(64) ,
   alarm_email  varchar(255) ,
   executor_route_strategy  varchar(50),
   executor_handler  varchar(255) ,
   executor_param  varchar(255) ,
   glue_type  varchar(50) NOT NULL,
    glue_source LVARCHAR(20000),
   glue_remark  varchar(128) ,
   glue_updatetime  datetime year to second ,
   child_jobkey  varchar(255) ,
   executor_block_strategy varchar(50),
   executor_fail_strategy varchar(50),
  PRIMARY KEY ( id )
);



CREATE TABLE   XXL_JOB_QRTZ_TRIGGER_LOG   (
    id    varchar(16)  NOT NULL ,
    job_group   int NOT NULL ,
    job_id   varchar(32)  NOT NULL ,
    glue_type varchar(50),
    executor_address   varchar(255) ,
    executor_handler   varchar(255) ,
    executor_param   varchar(255) ,
    trigger_time   datetime year to second ,
    trigger_code   varchar(255) ,
    trigger_msg   lvarchar(2048) ,
    handle_time   datetime  year to second ,
    handle_code   varchar(255) ,
    handle_msg   lvarchar(2048),
  PRIMARY KEY (  id  )
);





-- Ĭ���޸ĵ�ʱ����� update_time    ��Ҫ�޸Ĵ���
CREATE TABLE     XXL_JOB_QRTZ_TRIGGER_LOGGLUE     (
      id  serial ,
      job_id     VARCHAR(32) NOT NULL ,
      glue_type     varchar(50) ,
      glue_source     lvarchar(20000) ,
      glue_remark     varchar(128) NOT NULL ,
      add_time         datetime  year to second,
      update_time      datetime  year to second  , 
  PRIMARY KEY (    id    )
)

CREATE TABLE XXL_JOB_QRTZ_TRIGGER_REGISTRY (
     id    SERIAL NOT NULL ,
     registry_group    varchar(255) NOT NULL,
     registry_key    varchar(255) NOT NULL,
     registry_value    varchar(255) NOT NULL,
      update_time    datetime  year to second default current year to  second ,
  PRIMARY KEY (   id   )
) ;

CREATE TABLE     XXL_JOB_QRTZ_TRIGGER_GROUP     (
      id   serial   NOT NULL ,
      app_name     varchar(64) NOT NULL ,
      title     varchar(12) NOT NULL,
      order    int   DEFAULT 0 ,
      address_type    int   DEFAULT  0,
      address_list     varchar(200) DEFAULT NULL ,
  PRIMARY KEY (    id    )
);


INSERT INTO      XXL_JOB_QRTZ_TRIGGER_GROUP      (      app_name     ,      title     ,      order     ,      address_type     ,      address_list     ) values ( 'xxl-job-executor-example', 'ʾ��ִ����', '1', '0', null);



alter table XXL_JOB_QRTZ_JOB_DETAILS  lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGERS  lock mode(row);
alter table  XXL_JOB_QRTZ_SIMPLE_TRIGGERS  lock mode(row);
alter table  XXL_JOB_QRTZ_CRON_TRIGGERS  lock mode(row);
alter table  XXL_JOB_QRTZ_SIMPROP_TRIGGERS  lock mode(row);
alter table  XXL_JOB_QRTZ_BLOB_TRIGGERS lock mode(row);
alter table  XXL_JOB_QRTZ_CALENDARS lock mode(row);
alter table  XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS lock mode(row);
alter table  XXL_JOB_QRTZ_FIRED_TRIGGERS lock mode(row);
alter table  XXL_JOB_QRTZ_SCHEDULER_STATE lock mode(row);
alter table  XXL_JOB_QRTZ_LOCKS lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGER_INFO lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGER_LOG lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGER_LOGGLUE lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGER_REGISTRY lock mode(row);
alter table  XXL_JOB_QRTZ_TRIGGER_GROUP lock mode(row);