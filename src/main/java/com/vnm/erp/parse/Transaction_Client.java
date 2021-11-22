package com.vnm.erp.parse;

import java.util.Date; 
public class Transaction_Client {
 private String serial;
 private int type; 
private int status; 
 private String ip; 
private String startdate;
 private String enddate;  
private long id; 
private String transactionid; 
private String Code; 
 private Date createdtime;
 private long Prebalance;
 private long lastbalance;
 private long price;
 private String  usercode; 
private long  userid;
public String getSerial() {
	return serial;
}
public void setSerial(String serial) {
	this.serial = serial;
}
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public String getStartdate() {
	return startdate;
}
public void setStartdate(String startdate) {
	this.startdate = startdate;
}
public String getEnddate() {
	return enddate;
}
public void setEnddate(String enddate) {
	this.enddate = enddate;
}
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getTransactionid() {
	return transactionid;
}
public void setTransactionid(String transactionid) {
	this.transactionid = transactionid;
}
public String getCode() {
	return Code;
}
public void setCode(String code) {
	Code = code;
}
public Date getCreatedtime() {
	return createdtime;
}
public void setCreatedtime(Date createdtime) {
	this.createdtime = createdtime;
}
public long getPrebalance() {
	return Prebalance;
}
public void setPrebalance(long prebalance) {
	Prebalance = prebalance;
}
public long getLastbalance() {
	return lastbalance;
}
public void setLastbalance(long lastbalance) {
	this.lastbalance = lastbalance;
}
public long getPrice() {
	return price;
}
public void setPrice(long price) {
	this.price = price;
}
public String getUsercode() {
	return usercode;
}
public void setUsercode(String usercode) {
	this.usercode = usercode;
}
public long getUserid() {
	return userid;
}
public void setUserid(long userid) {
	this.userid = userid;
} 
 
}
