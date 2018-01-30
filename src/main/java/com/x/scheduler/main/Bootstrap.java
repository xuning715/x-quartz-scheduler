package com.x.scheduler.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
	private static final Logger logger = LogManager.getLogger(Bootstrap.class);

	public static void main(String[] args) throws Exception {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		context.registerShutdownHook();
		System.out.println(new Date().toString() + ":服务已启动");
		logger.info("服务已启动");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {
			String input = "";
			if (br.ready()) {
				input = br.readLine();
			}
			if (input.equals("stop")) {
				break;
			}
		} while (br != null);
		br.close();
	}

}
