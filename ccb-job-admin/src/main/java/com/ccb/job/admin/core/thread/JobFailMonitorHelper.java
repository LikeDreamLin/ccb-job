package com.ccb.job.admin.core.thread;

import com.ccb.job.admin.core.model.CcbJobGroup;
import com.ccb.job.admin.core.model.CcbJobInfo;
import com.ccb.job.admin.core.model.CcbJobLog;
import com.ccb.job.admin.core.schedule.CcbJobDynamicScheduler;
import com.ccb.job.admin.core.util.MailUtil;
import com.ccb.job.admin.core.util.SmsUtil;
import com.ccb.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
	private static  Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

	//单例
	private static  JobFailMonitorHelper instance = new JobFailMonitorHelper();
	public static  JobFailMonitorHelper getInstance(){
		return instance;
	}

	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(0xfff8);

	private Thread monitorThread;
	private boolean toStop = false;
	public void start(){
		monitorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!toStop) {
					try {
						logger.debug(">>>>>>>>>>> job monitor beat ... ");
						String jobLogId = JobFailMonitorHelper.instance.queue.take();
						if (jobLogId != null && !"".equals(jobLogId)) {
							logger.debug(">>>>>>>>>>> job monitor heat success, JobLogId:{}", jobLogId);
							CcbJobLog log = CcbJobDynamicScheduler.ccbJobLogDao.load(jobLogId);
							if (log!=null) {
								if (ReturnT.SUCCESS_CODE==log.getTriggerCode() && log.getHandleCode()==0) {
									// running
									try {
										TimeUnit.SECONDS.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									JobFailMonitorHelper.monitor(jobLogId);
								}
								if (ReturnT.SUCCESS_CODE==log.getTriggerCode() && ReturnT.SUCCESS_CODE==log.getHandleCode()) {
									// pass
								}
								if (ReturnT.FAIL_CODE == log.getTriggerCode()|| ReturnT.FAIL_CODE==log.getHandleCode()) {
									CcbJobInfo info = CcbJobDynamicScheduler.ccbJobInfoDao.loadById(log.getJobId());
									if (info!=null && info.getAlarmEmail()!=null && info.getAlarmEmail().trim().length()>0) {
										Set<String> phoneSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
											CcbJobGroup group = CcbJobDynamicScheduler.ccbJobGroupDao.load(Integer.valueOf(info.getJobGroup()));
											String content = "《调度监控报警》任务调度失败, 执行器名称"+ group!=null?group.getTitle():"null"+", 任务描述: "+info.getJobDesc()+".";
											//任务失败发送邮件换成发送短信
											//MailUtil.sendMail(email, title, content, false, null);
											SmsUtil.sendMulitMsg(phoneSet,content);

									}
								}
							}
						}
					} catch (Exception e) {
						logger.error("job monitor error:{}", e);
					}
				}
			}
		});
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void toStop(){
		toStop = true;
		//monitorThread.interrupt();
	}
	
	// producer
	public static  void monitor(String jobLogId){
		getInstance().queue.offer(jobLogId);
	}
	
}
