package com.x.scheduler.job;

import com.x.quartz.job.AbstractJob;
import com.x.quartz.job.Job;
import com.x.framework.rpc.BaseRpcService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

@Job
public class RpcJob extends AbstractJob {
	private final Logger logger = LogManager.getLogger(RpcJob.class);

	private BaseRpcService rpcService;
	private String  methodName;
    private Class[] paramTypeArray;
    private Object[] paramValueArray;

    public RpcJob() {
        logger.info("============================RpcJob is inited==============================");
    }

	public void setRpcService(BaseRpcService rpcService) {
		this.rpcService = rpcService;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setParamTypeArray(String[] paramTypeArray) throws ClassNotFoundException {
        this.paramTypeArray = new Class[paramTypeArray.length];
        int i = 0;
        for (String paramType : paramTypeArray) {
            this.paramTypeArray[i++] = Class.forName(paramType);
        }
	}

    public void setParamValueArray(Object[] paramValueArray) {
        this.paramValueArray = paramValueArray;
    }

    private void response() {
//		ListOperations<String, String> list = redisTemplate.opsForList();
//		list.leftPush(Constant.MONITOR_QUEUE_ + this.request.getRequestLevel(), JSON.toJSONString(response));
	}

	@Override
	public void execute() {
		try {
			Method method = rpcService.getClass().getMethod(methodName, paramTypeArray);
			Object returnValue = method.invoke(rpcService, paramValueArray);
		} catch (Exception e) {
			logger.error(e);
		}
		this.response();
	}
	
}
