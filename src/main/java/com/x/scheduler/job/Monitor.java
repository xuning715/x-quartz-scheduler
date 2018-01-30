package com.x.scheduler.job;

import java.util.Map;

import com.x.scheduler.model.Request;
import com.x.scheduler.model.Response;
import com.x.scheduler.client.Client;

import com.x.quartz.job.AbstractJob;
import com.x.quartz.job.Job;
import org.springframework.data.redis.core.RedisTemplate;

@Job
public class Monitor extends AbstractJob {
	private Request request;
	private RuleEngine ruleEngine;
	private Client client;
	private RedisTemplate redisTemplate;

	public void setRequest(Request request) {
		this.request = request;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private Response request() {
		Response response = new Response();
		try {
			response.setRequestId(request.getRequestId());
			System.out.println("=========getRequestId=========" + request.getRequestId());
			Map<String, Object> responseMap = this.client.request(this.request);
			response.setResponseMap(responseMap);
			response = ruleEngine.executeRule(response);
		} catch (Exception e) {
			response.setResponseFlag(false);
			response.setResponseRemark(e.getMessage());
			e.printStackTrace();
		} finally {
			response.setRequest(request);
			return response;
		}
	}

	private void response() {
		Response response = this.request();
//		ListOperations<String, String> list = redisTemplate.opsForList();
//		list.leftPush(Constant.MONITOR_QUEUE_ + this.request.getRequestLevel(), JSON.toJSONString(response));
	}

	@Override
	public void execute() {
		this.response();
	}
	
}
