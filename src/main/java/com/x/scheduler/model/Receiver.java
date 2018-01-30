package com.x.scheduler.model;

import com.x.framework.annotation.MappingColumn;
import com.x.framework.annotation.MappingTable;
import com.x.framework.model.BaseObject;

@MappingTable(tableName = "MONITOR_RECEIVER")
public class Receiver extends BaseObject {

	@MappingColumn(columnName = "RECEIVER_ID", columnPk = true)
	private String receiverId;
	
	@MappingColumn(columnName = "RECEIVER_NAME")
	private String receiverName;
	
	@MappingColumn(columnName = "RECEIVER_MOBILE")
	private String receiverMobile;
	
	@MappingColumn(columnName = "RECEIVER_EMAIL")
	private String receiverEmail;

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

}
