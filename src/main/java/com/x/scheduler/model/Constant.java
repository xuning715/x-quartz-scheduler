package com.x.scheduler.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Constant {
	public final static String blank = "";
	public final static String js = "js";
	public final static String comma = ", ";
	public final static String quote = "'";
	public final static String MONITOR_QUEUE_ = "MONITOR_QUEUE_";
	public final static String MAIL_TYPE = "m1";


	public static void main(String[] args) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine se = manager.getEngineByName("js");
		String str = "1+2*(3+6)-5/2";
		Double result = (Double) se.eval(str);
		System.out.println(result);

		str = "2 > 1";
		boolean b = (Boolean) se.eval(str);
		System.out.println(b);
	}
}
