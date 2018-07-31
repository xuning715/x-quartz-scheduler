package com.x.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

	public abstract void execute();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("", context);
		logger.info(context.getTrigger().getKey() + " is running.");
		this.execute();
	}

}
