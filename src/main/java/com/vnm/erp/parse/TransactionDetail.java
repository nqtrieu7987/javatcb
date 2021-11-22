package com.vnm.erp.parse;

public class TransactionDetail {
private String transactionid;  
private String	pin; 
private String	serial; 
private int	resultcode;
public String getTransactionid() {
	return transactionid;
}
public void setTransactionid(String transactionid) {
	this.transactionid = transactionid;
}
public String getPin() {
	return pin;
}
public void setPin(String pin) {
	this.pin = pin;
}
public String getSerial() {
	return serial;
}
public void setSerial(String serial) {
	this.serial = serial;
}
public int getResultcode() {
	return resultcode;
}
public void setResultcode(int resultcode) {
	this.resultcode = resultcode;
} 
}
