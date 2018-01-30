package com.x.scheduler.client;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.framework.remote.HttpClient;
import com.x.scheduler.model.Request;
import com.x.framework.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

public class RestClient implements Client {
	@Autowired
	private HttpClient httpClient;

	@Override
	public Map<String, Object> request(Request request) throws Exception {
		String url = request.getRequestUrl();
		String params = request.getRequestParamJson();
		// 调用restful接口
		String responseJson = httpClient.doPostJson(url, params);

		System.out.println("=======" + url);
		System.out.println("==params=====" + params);
		System.out.println("==responseJson=====" + responseJson);
		return this.jsonToMap(responseJson);
	}

	private Map<String, Object> jsonToMap(String json) throws Exception {
		if (json != null && json.length() > 0) {
			Map<String, Object> apiResultMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
			};
			apiResultMap = mapper.readValue(json, typeReference);
			return apiResultMap;
		} else {
			throw new BusinessException("json is null or empty");
		}
	}

	public static void main(String[] arg) throws Exception {
		try {
			//如果ip通 而端口不通 则返回空串 坑人啊
			Request request = new Request();
			request.setRequestUrl("http://localhost:8080/fckeditor/rest.jsp");
			request.setRequestParamJson("{\"name\" : \"xx1x\"}");
			RestClient RestClient = new RestClient();

			Map<String, Object> apiResultMap = RestClient.request(request);
			System.out.println(apiResultMap.get("name"));
			System.out.println(apiResultMap.get("age"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
