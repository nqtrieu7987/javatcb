package com.vnm.pcrf.soap;

public class PcrfQueryresponse
	{
		private  String  serviceName;     
		private  String  startDate;   
		private  String  quota;  
		public String getQuota() {
			return quota;
		}
		public void setQuota(String quota) {
			this.quota = quota;
		}
		private  String  endDate="";
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
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
		 
	}
		 