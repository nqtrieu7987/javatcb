package com.vnm.erp.cover;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
 
import com.vnm.erp.parse.MyResult;

public class Tibco { 					 
	private static String Endpoint1 = "http://10.6.22.35:40010/Business_Process/EPOS_Inbound";
	private static String Endpoint2 = "http://10.8.8.52:45010/Business_Process/Selfcare";
	
 
	  public static MyResult getresutlCOS(String imsi)
			{
				 String tibcoxml=  Acbalance( imsi );
						 
				    MyResult m = new MyResult();
					DocumentBuilder db;
					try {
						db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						InputSource is = new InputSource();
						is.setCharacterStream(new StringReader(tibcoxml)); 
						Document doc = db.parse(is);
						Node respon = doc.getElementsByTagName("CALL_PLAN").item(0);
						System.out.println("+++++++++++++++++++++"+respon.getTextContent());
						m.setValue(respon.getTextContent()); 
						m.setCode(respon.getTextContent()); 
					} catch (Exception e) {
						GlobalVariables.logger.error("error get data balance :"+imsi);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return m; 
			}
	private static String Acbalance( String msisdns) {
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000); 
		HttpPost post = new HttpPost("http://10.6.22.97:8022/services/Inbound");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction","GetSubscription"); 
		try {
			String m="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:prep=\"http://www.intecbilling.com/HCPT/prepaid_soap\"> "+
 "  <soapenv:Header/> "+
   "<soapenv:Body> "+
     " <prep:GetSubscriptionRequest> "+
     "    <prep:MSISDN>"+msisdns+"</prep:MSISDN> "+
      "   <prep:EFFECTIVE_DATE>1/1/2020</prep:EFFECTIVE_DATE> "+
       "  <prep:ACCOUNT_NUMBER></prep:ACCOUNT_NUMBER> "+
     " </prep:GetSubscriptionRequest> "+
   "</soapenv:Body> "+
"</soapenv:Envelope>";
			xmlEntity = new StringEntity(m					);
			GlobalVariables.logger.info(m					);
			post.setEntity(xmlEntity);			
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity()); 
			GlobalVariables.logger.info(result);
			post.releaseConnection();
				
			
		} catch (Exception ex) {
			GlobalVariables.logger.error("error parse data balance :"+msisdns);
			ex.printStackTrace();
		}
		return result;

	}	
	
public static String PushTopup(String phonenum,String Topuppin) {
		 
		StringEntity xmlEntity;
		HttpResponse response;
		String result = ""; 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost(Endpoint1);

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "/Business_Process/EPOS_Inbound/Etopup");
 
		String data="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://www.tibco.com/schemas/VNM_PROJECT/Shared_Resources/Schema/XML/xsd/EPOS/Schema.xsd12\">"+
		  " <soapenv:Header/> "+
		  " <soapenv:Body>"+
		   "   <sch:EtopUp_Request>"+ 
		     "    <sch:DeliveryChannelCtrlID>ETOPUP</sch:DeliveryChannelCtrlID>"+
		    "     <sch:STAN>"+Topuppin+"</sch:STAN>"+
		     "     <sch:MobNum>"+phonenum+"</sch:MobNum>"+ 
		    "     <sch:Currency>VND</sch:Currency>"+        
		     "    <sch:TopUpAmount>2</sch:TopUpAmount>"+        
		   "  </sch:EtopUp_Request>"+
		  " </soapenv:Body>"+
		"</soapenv:Envelope>";
		try {
			xmlEntity = new StringEntity(data		);
			post.setEntity(xmlEntity); 
			GlobalVariables.logger.info("request to TIBCO recharge:"
					+ data);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			GlobalVariables.logger.info("response from TIBCO:" + result);
			post.releaseConnection();

			return result;
		} catch (Exception ex) {
			return "";
		}

	} 
 
}
