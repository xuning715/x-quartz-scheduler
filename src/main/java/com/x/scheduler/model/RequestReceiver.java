package com.x.scheduler.model;

import com.x.framework.annotation.MappingColumn;
import com.x.framework.annotation.MappingTable;
import com.x.framework.model.BaseModel;

@MappingTable(tableName = "MONITOR_REQUEST_RECEIVER")
public class RequestReceiver extends BaseModel {

	@MappingColumn(columnName = "REQUEST_ID", columnPk = true)
	private String requestId;
	
	@MappingTable(tableName = "MONITOR_REQUEST")
	private Request request;
	
	@MappingColumn(columnName = "RECEIVER_ID", columnPk = true)
	private String receiverId;
	
	@MappingTable(tableName = "MONITOR_RECEIVER")
	private Receiver receiver;

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

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
}
