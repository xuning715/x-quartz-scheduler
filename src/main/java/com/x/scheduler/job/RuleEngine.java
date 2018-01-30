package com.x.scheduler.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import com.x.scheduler.model.Response;
import com.x.scheduler.model.Constant;

public class RuleEngine {
	private List<String> rules = new ArrayList<String>();

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	public Response executeRule(Response response) throws Exception {
		boolean responseFlag = true;
		String responseRemark = Constant.blank;
		Map<String, Object> responseMap = response.getResponseMap();
		for (String ruleExpression : rules) {
			for (Map.Entry<String, Object> entry : responseMap.entrySet()) {
				System.out.println(ruleExpression);
				ruleExpression = ruleExpression.replaceAll(Constant.quote + entry.getKey() + Constant.quote, Constant.quote + entry.getValue() + Constant.quote);
				System.out.println(ruleExpression);
			}
			if (!this.evalRule(ruleExpression, Boolean.class)) {
				responseFlag = false;
				responseRemark += ruleExpression + Constant.comma;
			}
			// if (!this.evalRule(ruleExpression, responseMap,
			// Boolean.class)) {
			// responseFlag = false;
			// responseRemark += ruleExpression + Constant.comma;
			// }
		}
		response.setResponseFlag(responseFlag);
		response.setResponseRemark(responseRemark);
		return response;
	}

	private <T> T evalRule(String expression, Class<T> clazz) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine se = manager.getEngineByName(Constant.js);
		Object value = null;
		value = se.eval(expression);
		if (null == value) {
			return null;
		} else {
			return (T) value;
		}
	}

	private <T> T evalRule(String expression, Map<String, String> map, Class<T> clazz) {
		JexlEngine jexl = new JexlEngine();
		Expression e = jexl.createExpression(expression);
		JexlContext jc = new MapContext();
		for (String key : map.keySet()) {
			jc.set(key, map.get(key));
		}
		Object value = e.evaluate(jc);
		if (null == value) {
			return null;
		} else {
			return (T) value;
		}
	}

	public static void main(String[] args) {
		// try {
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("money", 2100);
		// String expression = "money>=2000&&money<=4000";
		// Object code = convertToCode(expression, map);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// Map<String,Object> map=new HashMap<String,Object>();
		// map.put("testService",testService);
		// map.put("person",person);
		// String expression="testService.save(person)";
		// convertToCode(expression,map);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
