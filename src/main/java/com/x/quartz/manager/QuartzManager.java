package com.x.quartz.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.x.framework.Base;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.util.StringUtils;

/**
 * Quartz管理
 * <p>
 * 
 * @note 1. 每次reschedule trigger都要 pauseTrigger -> reschedule -> resumeTrigger，
 *       防止在reschedule的过程中发生连续调用（或在 <b>startTime之前</b> 按照cronExpression
 *       <b>执行多次</b>job）。 猜测reschedule的过程是一个耗时的过程。
 *       <p>
 *       2. 如果想要修改JobDetail，可以尝试自定义数据库结构。
 * 
 * @author Jinpeng Cui
 *
 */
public class QuartzManager extends Base {

	private final static Logger logger = LogManager.getLogger(QuartzManager.class);

	public static final String DEFAULT_DATEFORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private Scheduler scheduler;

	public QuartzManager() {
		logger.info(getClass() + " init.");
	}
	// 如果trigger已存在，视为修改trigger

	// 如果trigger不存在，视为添加trigger

	public Map<String, Object> getJob(String jobGroup, String jobName, String triggerGroup, String triggerName)
			throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);

		if (!scheduler.checkExists(jobKey)) {
			throw new SchedulerException("job 不存在");
		}
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		CronTrigger trigger = null;
		if (!(StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(triggerGroup)
				|| !scheduler.checkExists(triggerKey))) {
			trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("jobGroup", jobGroup);
		data.put("jobName", jobName);
		data.put("jobDescription", jobDetail.getDescription());
		data.put("jobClass", jobDetail.getJobClass().getName());
		if (null != trigger) {
			data.put("triggerGroup", triggerGroup);
			data.put("triggerName", triggerName);
			data.put("cronExpression", trigger.getCronExpression());
			data.put("triggerDescription", trigger.getDescription());
			data.put("startTime", this.getDate(trigger.getStartTime(), DEFAULT_DATEFORMAT_PATTERN));
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	public void add(String jobGroup, String jobName, String jobDescription, String jobClass, String triggerGroup,
			String triggerName, String triggerDescription, String cronExpression, String startTime)
			throws ClassNotFoundException, SchedulerException {
		Class<? extends Job> targetClass = (Class<? extends Job>) Class.forName(jobClass);

		// 设置默认值
		if (StringUtils.isEmpty(jobName)) {
			jobName = targetClass.getSimpleName();
		}
		if (StringUtils.isEmpty(triggerGroup)) {
			triggerGroup = jobGroup;
		}
		if (StringUtils.isEmpty(triggerName)) {
			triggerName = jobName;
		}
		Date triggerStartTime = null;
		if (StringUtils.isEmpty(startTime)) {
			triggerStartTime = new Date();
		} else {
			triggerStartTime = this.getDate(startTime, DEFAULT_DATEFORMAT_PATTERN);
		}

		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);

		// 判断job是否已存在
		if (scheduler.checkExists(jobKey)) {
			// 更新
			// 该操作修改job信息很难，后面可以自定义数据库结构
		} else {
			this.addJob(jobKey, targetClass, jobDescription);
		}

		// 判断trigger是否已存在，若不存在表示新加trigger，否则更新
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
		triggerBuilder.withIdentity(triggerKey);
		triggerBuilder.forJob(jobKey);
		triggerBuilder.startAt(triggerStartTime);
		triggerBuilder.withDescription(triggerDescription);
		if (scheduler.checkExists(triggerKey)) {
			this.updateCronTrigger(triggerKey, triggerBuilder.build());
		} else {
			this.scheduleJob(jobKey, targetClass, triggerBuilder.build());
		}

	}

	/**
	 * @deprecated
	 * @param jobKey
	 * @throws SchedulerException
	 */
	public void addJob(JobKey jobKey) throws SchedulerException {
		this.addJob(jobKey, null, "");
	}

	public void addJob(JobKey jobKey, Class<? extends Job> job, String jobDescription) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobKey).withDescription(jobDescription)
				.storeDurably(true).build();
		scheduler.addJob(jobDetail, true);
	}

	public void triggerJob(JobKey jobKey) throws SchedulerException {
		scheduler.triggerJob(jobKey);
	}

	/**
	 * 修改trigger,修改qrtz_triggers表数据
	 * 
	 * @param triggerKey
	 * @param trigger
	 * @return
	 */
	public boolean updateTrigger(TriggerKey triggerKey, Trigger trigger) {
		boolean flag = false;
		try {
			// 先暂停
			scheduler.pauseTrigger(triggerKey);
			// 重新部署
			scheduler.rescheduleJob(triggerKey, trigger);
			// 先暂停
			scheduler.resumeTrigger(triggerKey);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public void scheduleJob(JobKey jobKey, Class<? extends Job> job, Trigger trigger) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobKey).build();
		HashSet<Trigger> triggers = new HashSet<Trigger>();
		triggers.add(trigger);
		scheduler.scheduleJob(jobDetail, triggers, true);
	}

	/**
	 * 修改Cron Trigger，涉及表qrtz_cron_triggers，更改执行计划等
	 * 
	 * @param triggerKey
	 * @param cronTrigger
	 * @return
	 * @throws SchedulerException
	 */
	public void updateCronTrigger(TriggerKey triggerKey, CronTrigger cronTrigger) throws SchedulerException {
		// 先暂停
		scheduler.pauseTrigger(triggerKey);
		// 重新部署
		scheduler.rescheduleJob(triggerKey, cronTrigger);
		// 先暂停
		scheduler.resumeTrigger(triggerKey);
	}

	/**
	 * 添加或修改jobDetail，但并不运行
	 * 
	 * @param jobDetail
	 * @return
	 */
	public boolean updateJobDetail(JobDetail jobDetail) {
		boolean flag = false;
		try {
			scheduler.addJob(jobDetail, true);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean unscheduleJob(TriggerKey triggerKey) throws SchedulerException {
		scheduler.pauseTrigger(triggerKey);// 停止触发器
		return scheduler.unscheduleJob(triggerKey);// 移除触发器
	}

	/**
	 * 删除Job,直接从数据库中删除，关联表数据也删除
	 * 
	 * @param triggerKey
	 * @param jobKey
	 * @throws SchedulerException
	 */
	public void deleteJob(TriggerKey triggerKey, JobKey jobKey) throws SchedulerException {
		scheduler.pauseTrigger(triggerKey);// 停止触发器
		scheduler.unscheduleJob(triggerKey);// 移除触发器
		scheduler.deleteJob(jobKey);// 删除任务
	}

	public boolean deleteJob(JobKey jobKey) throws SchedulerException {
		scheduler.pauseJob(jobKey);
		return scheduler.deleteJob(jobKey);
	}

	/**
	 * 暂停某个触发器
	 * 
	 * @param triggerKey
	 * @return
	 * @throws SchedulerException
	 */
	public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
		scheduler.pauseTrigger(triggerKey);
	}

	/**
	 * 恢复已暂停的触发器
	 * 
	 * @param triggerKey
	 * @return
	 * @throws SchedulerException
	 */
	public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
		// 暂停触发器
		scheduler.resumeTrigger(triggerKey);
	}

	/**
	 * 暂停所有触发器
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public void standby() throws SchedulerException {
		scheduler.standby();
	}

	/**
	 * 启动所有触发器
	 * 
	 * @return
	 */
	public boolean startAllTrigger() {
		boolean flag = false;
		try {
			if (scheduler.isInStandbyMode()) {
				scheduler.start();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 停止所有触发器,停止后无法启动，无法调用start方法
	 * 
	 * @return
	 */
	public boolean shutdown() {
		boolean flag = false;
		try {
			scheduler.shutdown();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public List<JobDetail> getJobs() throws SchedulerException {
		List<JobDetail> jobDetails = new ArrayList<JobDetail>();
		Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
		for (JobKey jobKey : jobKeys) {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			jobDetails.add(jobDetail);
		}
		return jobDetails;
	}

	public List<? extends Trigger> getTriggersOfJob(JobKey jobKey) throws SchedulerException {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		return triggers;
	}

	public TriggerState getTriggerState(TriggerKey triggerKey) throws SchedulerException {
		return scheduler.getTriggerState(triggerKey);
	}

	public static String parseTriggerState(TriggerState triggerState) {
		String triggerDesc = null;
		switch (triggerState) {
		case NORMAL:
			triggerDesc = "正常";
			break;
		case PAUSED:
			triggerDesc = "暂停";
			break;
		case COMPLETE:
			triggerDesc = "已完成";
			break;
		case ERROR:
			triggerDesc = "异常";
			break;
		case BLOCKED:
			triggerDesc = "阻塞";
			break;
		case NONE:
			triggerDesc = "已删除";
			break;
		default:
			break;
		}
		return triggerDesc;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

}
