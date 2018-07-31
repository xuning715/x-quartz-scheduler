package com.x.quartz.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class SpringJobBeanFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringJobBeanFactory.class);

	private ApplicationContext applicationContext;
	
	private static List<String> jobNameList = new ArrayList<String>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		Map<String, AbstractJob> map = applicationContext.getBeansOfType(AbstractJob.class);
		System.out.println(map.size());
		for (Map.Entry<String, AbstractJob> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			jobNameList.add(entry.getKey());
		}
	}

	public static List<String> getJobNameList() {
		return jobNameList;
	}

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		String name = bundle.getJobDetail().getKey().getName();
		Object jobInstance = applicationContext.getBean(name);
		// Object jobInstance = super.createJobInstance(bundle);
		// 把Job交给Spring来管理，这样Job就能使用由Spring产生的Bean了
		// try {
		// applicationContext.getAutowireCapableBeanFactory().autowireBean(montior);
		// } catch (Exception e) {
		// logger.error("createJobInstance exception is : ", e);
		// }
		return jobInstance;
	}

}
