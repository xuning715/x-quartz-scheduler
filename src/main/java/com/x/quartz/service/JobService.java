package com.x.quartz.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.x.framework.Base;
import com.x.util.LoadPackageClasses;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.x.quartz.dto.JobOutput;
import com.x.quartz.dto.TriggerOutput;
import com.x.quartz.exception.QuartzException;
import com.x.quartz.manager.QuartzManager;

@Service("jobService")
public class JobService extends Base implements InitializingBean {
	public static final String DEFAULT_DATEFORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private LoadPackageClasses lp = LoadPackageClasses.getInstance();

	private static List<String> classNameList = new ArrayList<String>();

	@Autowired
	QuartzManager quartzManager;

	/**
	 * 获取项目中被@Job注解的Job类
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public List<String> registedJobs() throws ClassNotFoundException, IOException {
		return classNameList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<Class<?>> classSet = lp.getClassSet();
		for (Class<?> clazz : classSet) {
			classNameList.add(clazz.getName());
		}
	}
	/**
	 * 返回所有jobDetail
	 * 
	 * @return
	 */
	public List<JobOutput> getAllJobDetail() {
		List<JobOutput> list = new ArrayList<JobOutput>();
		try {
			List<JobDetail> jobDetails = quartzManager.getJobs();
			for (JobDetail jobDetail : jobDetails) {

				JobOutput jobOutput = new JobOutput();
				// job组
				jobOutput.setGroup(jobDetail.getKey().getGroup());
				// job名称
				jobOutput.setName(jobDetail.getKey().getName());
				// job业务描述
				jobOutput.setDescription(jobDetail.getDescription());
				// 以下返回该任务的trigger信息
				List<? extends Trigger> triggers = quartzManager.getTriggersOfJob(jobDetail.getKey());
				for (Trigger trigger : triggers) {
					String cronExpression = ((CronTrigger) trigger).getCronExpression();
					// 触发器名称
					String triggerName = trigger.getKey().getName();
					// 触发器组名
					String triggerGroup = trigger.getKey().getGroup();

					TriggerOutput triggerOutput = new TriggerOutput();
					triggerOutput.setCronExpression(cronExpression);
					triggerOutput
							.setStartTime(this.getDate(trigger.getStartTime(), DEFAULT_DATEFORMAT_PATTERN));
					triggerOutput.setPreviousFireTime(
							this.getDate(trigger.getPreviousFireTime(), DEFAULT_DATEFORMAT_PATTERN));
					triggerOutput.setNextFireTime(
							this.getDate(trigger.getNextFireTime(), DEFAULT_DATEFORMAT_PATTERN));
					triggerOutput.setEndTime(
							this.getDate(trigger.getEndTime(), DEFAULT_DATEFORMAT_PATTERN));
					triggerOutput.setName(triggerName);
					triggerOutput.setGroup(triggerGroup);
					triggerOutput.setDescription(trigger.getDescription());
					triggerOutput
							.setState(QuartzManager.parseTriggerState(quartzManager.getTriggerState(trigger.getKey())));
					jobOutput.getTriggers().add(triggerOutput);
				}
				// DELETED -1 COMPLETE 2 PAUSED 1 PAUSED_BLOCKED 1 ERROR 3
				// BLOCKED 4 否则 返回0
				/*
				 * None：Trigger已经完成，且不会在执行，或者找不到该触发器，或者Trigger已经被删除
				 * COMPLETE：触发器完成，但是任务可能还正在执行中 PAUSED：暂停状态 BLOCKED：线程阻塞状态
				 * NORMAL:正常状态 BLOCKED：线程阻塞状态
				 */
				HashMap<String, String> targerMap = new HashMap<String, String>();
				for (Object e : jobDetail.getJobDataMap().entrySet()) {
					Map.Entry<?, ?> entry = (Entry<?, ?>) e;
					targerMap.put(entry.getKey().toString(), entry.getValue().toString());
				}
				// 任务实现类
				jobOutput.setTargetObject(jobDetail.getJobClass().getName());
				list.add(jobOutput);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> getJob(String jobGroup, String jobName, String triggerGroup, String triggerName)
			throws SchedulerException, QuartzException {
		return quartzManager.getJob(jobGroup, jobName, triggerGroup, triggerName);
	}

	public void add(String jobGroup, String jobName, String jobDescription, String jobClass, String triggerGroup,
			String triggerName, String triggerDescription, String cronExpression, String startTime)
			throws ClassNotFoundException, SchedulerException, QuartzException {
		quartzManager.add(jobGroup, jobName, jobDescription, jobClass, triggerGroup, triggerName, triggerDescription,
				cronExpression, startTime);
	}

	public void pauseTrigger(TriggerKey triggerKey) throws SchedulerException {
		quartzManager.pauseTrigger(triggerKey);
	}

	public void resumeTrigger(TriggerKey triggerKey) throws SchedulerException {
		quartzManager.resumeTrigger(triggerKey);
	}

	public void unscheduleJob(TriggerKey triggerKey) throws SchedulerException {
		quartzManager.unscheduleJob(triggerKey);
	}

	public void deleteJob(JobKey jobKey) throws SchedulerException {
		quartzManager.deleteJob(jobKey);
	}

}
