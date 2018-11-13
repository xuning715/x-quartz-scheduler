package com.x.scheduler.model;

import java.util.Date;
import java.util.Map;

import com.x.framework.annotation.MappingColumn;
import com.x.framework.annotation.MappingTable;
import com.x.framework.model.BaseModel;

@MappingTable(tableName = "MONITOR_RESPONSE")
public class Response extends BaseModel {
	@MappingColumn(columnName = "RESPONSE_ID", columnPk = true)
	private String responseId;
	
	@MappingColumn(columnName = "RESPONSE_FLAG")
	private boolean responseFlag;
	
	@MappingColumn(columnName = "RESPONSE_REMARK")
	private String responseRemark;
	
	@MappingColumn(columnName = "RESPONSE_CREATE_TIME")
	private Date responseCreateTime;
	
	@MappingColumn(columnName = "REQUEST_ID")
	private String requestId;
	
	@MappingTable(tableName = "MONITOR_REQUEST")
	private Request request;
	
	private Map<String, Object> responseMap;

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public boolean getResponseFlag() {
		return responseFlag;
	}

	public void setResponseFlag(boolean responseFlag) {
		this.responseFlag = responseFlag;
	}

	public String getResponseRemark() {
		return responseRemark;
	}

	public void setResponseRemark(String responseRemark) {
		this.responseRemark = responseRemark;
	}

	public Date getResponseCreateTime() {
		return responseCreateTime;
	}

	public void setResponseCreateTime(Date responseCreateTime) {
		this.responseCreateTime = responseCreateTime;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Map<String, Object> getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(Map<String, Object> responseMap) {
		this.responseMap = responseMap;
	}

}
