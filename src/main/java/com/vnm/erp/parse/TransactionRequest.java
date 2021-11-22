package com.vnm.erp.parse;

public  class TransactionRequest
{
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	} 
	private String page="0";
	private String startDate;
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private String transactionid;
	public String getTransactionid() {
		return transactionid;
	}
	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}
	private String endDate;
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	private String type;
 
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	} 
	
}
