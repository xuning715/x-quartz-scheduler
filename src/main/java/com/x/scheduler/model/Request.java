package com.x.scheduler.model;

import java.util.Date;

import com.x.framework.annotation.MappingColumn;
import com.x.framework.annotation.MappingTable;
import com.x.framework.model.BaseObject;

@MappingTable(tableName = "MONITOR_REQUEST")
public class Request extends BaseObject {
	@MappingColumn(columnName = "REQUEST_ID", columnPk = true)
	private String requestId;
	
	@MappingColumn(columnName = "REQUEST_NAME")
	private String requestName;
	
	@MappingColumn(columnName = "REQUEST_LEVEL")
	private Integer requestLevel;
	
	@MappingColumn(columnName = "REQUEST_URL")
	private String requestUrl;
	
	@MappingColumn(columnName = "REQUEST_PARAM_JSON")
	private String requestParamJson;
	
	@MappingColumn(columnName = "REQUEST_TYPE")
	private Integer requestType;
	
	@MappingColumn(columnName = "REQUEST_CREATE_TIME")
	private Date requestCreateTime;
	
	@MappingColumn(columnName = "REQUEST_MODIFY_TIME")
	private Date requestModifyTime;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public Integer getRequestLevel() {
		return requestLevel;
	}

	public void setRequestLevel(Integer requestLevel) {
		this.requestLevel = requestLevel;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestParamJson() {
		return requestParamJson;
	}

	public void setRequestParamJson(String requestParamJson) {
		this.requestParamJson = requestParamJson;
	}

	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}

	public Date getRequestCreateTime() {
		return requestCreateTime;
	}

	public void setRequestCreateTime(Date requestCreateTime) {
		this.requestCreateTime = requestCreateTime;
	}

	public Date getRequestModifyTime() {
		return requestModifyTime;
	}

	public void setRequestModifyTime(Date requestModifyTime) {
		this.requestModifyTime = requestModifyTime;
	}

}
