package com.x.scheduler.client;

import java.util.Map;

import com.x.scheduler.model.Request;

public interface Client {
	public Map<String, Object> request(Request request) throws Exception;
}
