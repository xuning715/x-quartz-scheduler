package com.x.quartz.job;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractJob implements Job {

	private final static Logger logger = LogManager.getLogger(AbstractJob.class);

	public abstract void execute();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug(context);
		logger.info(context.getTrigger().getKey() + " is running.");
		this.execute();
	}

}
