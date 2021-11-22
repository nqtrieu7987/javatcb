package com.vnm.pcrf.soap;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vnm.erp.cover.ClientOB;
import com.vnm.erp.cover.GlobalVariables;
import com.vnm.erp.parse.MyResult;

public class PCRF {

	static class pcrfresponseparent {
		private Pcrfresponse message;
		public Pcrfresponse getMessage() {
			return message;
		}
		public void setMessage(Pcrfresponse message) {
			this.message = message;
		}
	}

	class pcrfsetquataanddate {
		private String usrIdentifier;
		private String srvName;
		private String qtaValue;
		private String srvEndDateTime;

		public String getUsrIdentifier() {
			return usrIdentifier;
		}

		public void setUsrIdentifier(String usrIdentifier) {
			this.usrIdentifier = usrIdentifier;
		}

		public String getSrvName() {
			return srvName;
		}

		public void setSrvName(String srvName) {
			this.srvName = srvName;
		}

		public String getQtaValue() {
			return qtaValue;
		}

		public void setQtaValue(String qtaValue) {
			this.qtaValue = qtaValue;
		}

		public String getSrvEndDateTime() {
			return srvEndDateTime;
		}

		public void setSrvEndDateTime(String srvEndDateTime) {
			this.srvEndDateTime = srvEndDateTime;
		}
	}

	 
	
	public Pcrfresponse custompcrfparse(String data, String Package,
			String msisdn, String endDate,ClientOB cliento) {
		if(cliento.UrlPrcf==null) 
			{ Loginresponse   response= loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
			cliento.UrlPrcf=response.getLocation();
			}
		String xml = custompcrfcall(data, Package, msisdn , endDate,cliento); 
		Pcrfresponse m = new Pcrfresponse();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("resultCode").item(0);
			if (respon.getTextContent().equals("5004")) 
			{
				Loginresponse   response=loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
				if(response.getResultCode().equals("0"))
				{
					cliento.UrlPrcf=response.getLocation();
				  custompcrfparse(data,  Package,
						 msisdn,  endDate,cliento);
				}
			}
			else
			{

				 m.setResultCode(respon.getTextContent());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;

	}
 
	public PcrfQueryresponse querypcrfparse(String Package,
			String msisdn,ClientOB cliento) {
		if(cliento.UrlPrcf==null) 
			{ Loginresponse   response= loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
			cliento.UrlPrcf=response.getLocation();
			}
		
		String xml = querypcrfcall(  msisdn ,cliento );
		PcrfQueryresponse m = new PcrfQueryresponse();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("resultCode").item(0);
			if (respon.getTextContent().equals("5004")) 
			{
				Loginresponse   response=loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
				if(response.getResultCode().equals("0"))
				{
					cliento.UrlPrcf=response.getLocation();
				  return querypcrfparse(Package,msisdn,cliento);
				}
			}
			else
			{
				NodeList serviceservices = doc.getElementsByTagName("subscribedService");
				for(int i=0;i<serviceservices.getLength();i++)
				{
					String d=serviceservices.item(i).getTextContent();
					if(d.contains(Package))
					{
						NodeList nodes=serviceservices.item(i).getChildNodes(); // attribletagsssss
						for (int j=0;j<nodes.getLength();j++ ) {
							  //System.out.println(nodes.item(j).getTextContent());
							 	if(nodes.item(j).getTextContent().contains("SRVSTARTDATETIME"))	
							 	{
							 		m.setStartDate(nodes.item(j).getLastChild().getTextContent());
							 	}
							 	if(nodes.item(j).getTextContent().contains("SRVENDDATETIME"))	
							 	{ 
							 		m.setEndDate(nodes.item(j).getLastChild().getTextContent());
							 	}
//							 	if(nodes.item(j).getTextContent().contains("SRVSTARTDATETIME"))	
//							 	{
//							 		m.setQuota(nodes.item(j).getLastChild().getNodeValue());
//							 	}
						} 
					}
					
				
				}
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;

	}
	
	public String custompcrfcallfree( String qtaValue, String Package,
		String msisdn, String endDate, String start_date) throws Exception  {
		ClientOB client=GlobalVariables.pool.acquire();
 		String res = "";
		try { 
			res=  custompcrfcallfree11(  qtaValue,   Package,
					  msisdn,   endDate,   start_date,client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{ 
				GlobalVariables.pool.returnPoll(client);  
		}
		return res;
	
		
		
	}
	
	public String custompcrfcallfree11(String qtaValue, String Package,
			String msisdn, String endDate, String start_date,ClientOB cliento) {
		if(cliento.UrlPrcf==null) 
			{ Loginresponse   response= loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
			cliento.UrlPrcf=response.getLocation();
			}
		
		String xml = custompcrfcallfree1(    qtaValue,   Package,
				  msisdn,   endDate,   start_date,  cliento );
 		DocumentBuilder db;
 		try {
 			 
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("resultCode").item(0);
			if (respon.getTextContent().equals("5004")) 
			{
				Loginresponse   response=loginpcrf(GlobalVariables.Username, GlobalVariables.Password,cliento);
				if(response.getResultCode().equals("0"))
				{
					cliento.UrlPrcf=response.getLocation();
				  return custompcrfcallfree11(  qtaValue,   Package,
							  msisdn,   endDate,   start_date,  cliento);
				}
			}
			else
			{

			  return xml;
 				 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}
	
	private String custompcrfcallfree1(String qtaValue, String Package,
			String msisdn, String endDate, String start_date,ClientOB cliento) {
		StringEntity xmlEntity;
		org.apache.http.HttpResponse response;
		String result = ""; 
		 
		try {
			cliento.post.setURI(new URI(cliento.UrlPrcf));
		} catch (URISyntaxException e) {
			 
			e.printStackTrace();
		} 
			
		 
		try {
			 
			 
			 
			String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rm=\"rm:soap\">"
					+ "<soapenv:Header/>"
					+ "<soapenv:Body>"
					+ " <rm:subscribeService>"
					+ "    <inPara>"
					+ "   <subscriber>"
					+ "   <attribute>"
					+ "        <key>usrIdentifier</key>" + "     <value>"
					+ msisdn
					+ "</value>"
					+ "    </attribute>"
					+ "</subscriber>"
					+ " <subscribedService>"
					+ "    <attribute>"
					+ "       <key>srvName</key>"
					+ "     <value>"
					+ Package
					+ "</value>"
					+ " </attribute>"
					+ "  <attribute>"
					+ "      <key>srvStartDateTime</key>"
					+ "   <value>"+start_date+"</value>"
					+ "  </attribute>"
					+ "  <attribute>"
					+ "     <key>srvEndDateTime</key>"
					+ "   <value>"
					+ endDate
					+ "</value>"
					+ "  </attribute>"
					+ " <attribute>"
					+ "    <key>qtaValue</key>"
					+ "       <value>"
					+ qtaValue
					+ "</value>"
					+ "    </attribute>"
					+ " </subscribedService>"
					+ "  </inPara>"
					+ "  </rm:subscribeService>"
					+ " </soapenv:Body> " + " </soapenv:Envelope>";
			 
 			xmlEntity = new StringEntity(postdata);

			cliento.post.setEntity(xmlEntity);

			response = cliento.client.execute(cliento.post);
			result = EntityUtils.toString(response.getEntity()); 
			 

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return result;

	}
	public String querypcrfAR( String msisdn) throws Exception  {
		ClientOB client=GlobalVariables.pool.acquire();
		String res="";
		try {
		 
			res=  querypcrfAR1(msisdn,client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{ 
				GlobalVariables.pool.returnPoll(client);  
		}
		return res;
	
		
		
	}
	private String querypcrfAR1( 	String msisdn,ClientOB client) {
		 
 		try {
 			if(client.UrlPrcf==null) 
			{ 
				Loginresponse   response= loginpcrf(GlobalVariables.Username, GlobalVariables.Password,client);
				client.UrlPrcf=response.getLocation();
			}
		
		String xml = querypcrfcall(  msisdn  ,client);
 		DocumentBuilder db;
		 
				db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
	
				Document doc = db.parse(is);
				Node respon = doc.getElementsByTagName("resultCode").item(0);
				if (respon.getTextContent().equals("5004")) 
				{
					 
					Loginresponse   response=loginpcrf(GlobalVariables.Username, GlobalVariables.Password,client);
					if(response.getResultCode().equals("0"))
					{
						client.UrlPrcf=response.getLocation();
					  return querypcrfAR1(msisdn,client);
					}
				}
				else
				{
					 
					return xml;
					
				}
			 
			return xml;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 		return "";

	}
 
 

	public Loginresponse loginpcrf(String Username, String Password,ClientOB cliento) {

		StringEntity xmlEntity;
		org.apache.http.HttpResponse response;
		Loginresponse result = new Loginresponse();
		
		try
		{
		cliento.post.releaseConnection();
 
		}catch(Exception ex){}
		try
		{
 		cliento.client.close();

		}catch(Exception ex){}
		
		cliento.client = HttpClientBuilder.create().build(); 

		cliento.post=new HttpPost("http://10.20.193.68:8080/axis/services/ScfPccSoapServiceEndpointPort");
		cliento.post.setHeader("Content-Type", "text/xml;charset=UTF-8");  
		try {
			String postdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
					+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rm=\"rm:soap\">"
					+ " <soapenv:Header/> " + "  <soapenv:Body> "
					+ "    <rm:LGI> " + "     <inPara>          "
					+ "           <Login>      " + "             <attribute> "
					+ "            <key>OPNAME</key> " + "       <value>"
					+ Username + "</value> " + "        </attribute> "
					+ "       <attribute> " + "            <key>PWD</key> "
					+ "           <value>" + Password + "</value> "
					+ "         </attribute> " + "        </Login> "
					+ "     </inPara> " + "   </rm:LGI> " + "</soapenv:Body> "
					+ "</soapenv:Envelope>";
			xmlEntity = new StringEntity(postdata);
			cliento.post.setEntity(xmlEntity);
			response = cliento.client.execute(cliento.post);
		String 	res = EntityUtils.toString(response.getEntity()); 
		DocumentBuilder db  = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(res));
 
		Document doc = db.parse(is); 
		String m = doc.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getFirstChild().getTextContent();
		if(m.equals("0"))
		{
			Header[] headers = response.getAllHeaders();
			String location="";
			for (Header header : headers) {
     				System.out.println("Key : " + header.getName() 
				      + " ,Value : " + header.getValue());
     				if(header.getName().equals("Location"))
     				{
     					location=header.getValue();
     					break;
     				}
			}
			result.setLocation(location);
			result.setResultCode("0");
		} 
		

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return result;
	}

	private String querypcrfcall(	String msisdn,ClientOB cliento) {
		StringEntity xmlEntity;
		org.apache.http.HttpResponse response;
		String result = ""; 
		 
		try {
			cliento.post.setURI(new URI(cliento.UrlPrcf));
		} catch (URISyntaxException e) {
			 
			e.printStackTrace();
		} 
		try {
			String postdata ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rm=\"rm:soap\"> "+
					  " <soapenv:Header/>"+
					  " <soapenv:Body>"+
					  "    <rm:getSubscriberAllInf>"+
					    "     <inPara>"+
					 "        <subscriber>"+
					   "            <attribute>"+
					    "              <key>usrIdentifier</key> "+
					   "               <value>"+ msisdn +"</value> "+
					 "              </attribute> "+
					    "        </subscriber> "+
					  "       </inPara> "+
					   "   </rm:getSubscriberAllInf> "+
					  " </soapenv:Body> "+
					"</soapenv:Envelope> ";
			xmlEntity = new StringEntity(postdata);

			cliento.post.setEntity(xmlEntity);

			response = cliento.client.execute(cliento.post);
			result = EntityUtils.toString(response.getEntity()); 
			 

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return result;

	}
	
	private String custompcrfcall(String qtaValue, String Package,
			String msisdn, String endDate,ClientOB cliento) {
		StringEntity xmlEntity;
		org.apache.http.HttpResponse response;
		String result = ""; 
		 
		try { 
			cliento.post.setURI(new URI(cliento.UrlPrcf));
		} catch (URISyntaxException e) {
			 
			e.printStackTrace();
		} 
		try {
			String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rm=\"rm:soap\">"
					+ "<soapenv:Header/>"
					+ "<soapenv:Body>"
					+ " <rm:subscribeService>"
					+ "    <inPara>"
					+ "   <subscriber>"
					+ "   <attribute>"
					+ "        <key>usrIdentifier</key>" + "     <value>"
					+ msisdn
					+ "</value>"
					+ "    </attribute>"
					+ "</subscriber>"
					+ " <subscribedService>"
					+ "    <attribute>"
					+ "       <key>srvName</key>"
					+ "     <value>"
					+ Package
					+ "</value>"
					+ " </attribute>"
					+ "  <attribute>"
					+ "      <key>srvStartDateTime</key>"
					+ "   <value></value>"
					+ "  </attribute>"
					+ "  <attribute>"
					+ "     <key>srvEndDateTime</key>"
					+ "   <value>"
					+ endDate
					+ "</value>"
					+ "  </attribute>"
//					+ " <attribute>"
//					+ "    <key>qtaValue</key>"
//					+ "       <value>"
//					+ qtaValue
//					+ "</value>"
//					+ "    </attribute>"
					+ " </subscribedService>"
					+ "  </inPara>"
					+ "  </rm:subscribeService>"
					+ " </soapenv:Body> " + " </soapenv:Envelope>";
			 
			xmlEntity = new StringEntity(postdata);

			cliento.post.setEntity(xmlEntity);

			response = cliento.client.execute(cliento.post);
			result = EntityUtils.toString(response.getEntity());

			 

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return result;

	}

}
