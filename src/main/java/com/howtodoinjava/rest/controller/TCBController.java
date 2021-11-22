package com.howtodoinjava.rest.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.vnm.erp.cover.GlobalVariables;
import com.vnm.erp.cover.JavaSignSHA256;
import com.vnm.erp.cover.JavaSignSHA256_V2;

@RestController
@RequestMapping(path = "/tcb")
public class TCBController {
	@GetMapping(path = "/pushstatus",  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String pushstatus(@RequestBody String Xml)
	{
		GlobalVariables.logger.info(Xml);
		
//			return "<soapenv:Envelope"+
//"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
//"<soapenv:Header"+
//"xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\"/>"+
//"<soapenv:Body"+
//"xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"+
//"<PushStatusRs"+
//"xmlns=\"http://app.linkcard.tcb.com/\">"+
//"<RspnInf>"+
//"<Id>stg11213128755dcb6ba88d0847</Id>"+
//"<TxId>1234567890</TxId>"+
//"<CreDtTm>2020-12-04T15:06:45.469+07:00</CreDtTm>"+
//"<Desc>Trans sale"+
//"response</Desc>"+
//"<Sgntr>NO"+
//"SIGNATURE</Sgntr>"+
//"</RspnInf>"+
//"<Envt>"+
//"<TrgtPty>"+
//"<Nm>CARD</Nm>"+
//"</TrgtPty>"+
//"<SrcPty>"+
//"<Nm>TCB</Nm>"+
//"</SrcPty>"+
//"<Rqstr>"+
//"<Nm>Response from TCB"+
//"system</Nm>"+
//"</Rqstr>"+
//"</Envt>"+
//"<RspnSts>"+
//"<Status>0</Status>"+
//"<ErrCd>APP-001</ErrCd>"+
//"<ErrMsg>Da ghi nhan</ErrMsg>"+
//"</RspnSts>"+
//"</PushStatusRs>"+
//"</soapenv:Body>";
		Gson gson = new Gson();
		return gson.toJson(GetPUSHSTATUS(Xml));
	}
	@GetMapping(path = "/updatestatus",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String updatestatus(@RequestBody String xml) {
		GlobalVariables.logger.info( xml);		
//			return "<soapenv:Envelope"+
//"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
//"<soapenv:Header"+
//"xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\"/>"+
//"<soapenv:Body"+
//"xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"+
//"<PushStatusRs"+
//"xmlns=\"http://app.linkcard.tcb.com/\">"+
//"<RspnInf>"+
//"<Id>stg11213128755dcb6ba88d0847</Id>"+
//"<TxId>1234567890</TxId>"+
//"<CreDtTm>2020-12-04T15:06:45.469+07:00</CreDtTm>"+
//"<Desc>Trans sale"+
//"response</Desc>"+
//"<Sgntr>NO"+
//"SIGNATURE</Sgntr>"+
//"</RspnInf>"+
//"<Envt>"+
//"<TrgtPty>"+
//"<Nm>CARD</Nm>"+
//"</TrgtPty>"+
//"<SrcPty>"+
//"<Nm>TCB</Nm>"+
//"</SrcPty>"+
//"<Rqstr>"+
//"<Nm>Response from TCB"+
//"system</Nm>"+
//"</Rqstr>"+
//"</Envt>"+
//"<RspnSts>"+
//"<Status>0</Status>"+
//"<ErrCd>APP-001</ErrCd>"+
//"<ErrMsg>Da ghi nhan</ErrMsg>"+
//"</RspnSts>"+
//"</PushStatusRs>"+
//"</soapenv:Body>";
		Gson gson = new Gson();
		return gson.toJson(GetUPDATESTATUS(xml));
	}
	 
	UPDATESTATUS GetUPDATESTATUS(String Xml)
	{
		
		UPDATESTATUS m = new UPDATESTATUS();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(Xml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Result")!=null)
			m.setResult(doc.getElementsByTagName("Result").item(0).getTextContent());
			if(doc.getElementsByTagName("PartID")!=null)
				m.setPartID(doc.getElementsByTagName("PartID").item(0).getTextContent());
			if(doc.getElementsByTagName("Channel")!=null)
				m.setChannel(doc.getElementsByTagName("Channel").item(0).getTextContent());
			if(doc.getElementsByTagName("TxnStsRemark")!=null)
				m.setTxnStsRemark(doc.getElementsByTagName("TxnStsRemark").item(0).getTextContent());
			if(doc.getElementsByTagName("TxnSts")!=null)
				m.setTxnSts(doc.getElementsByTagName("TxnSts").item(0).getTextContent());
			if(doc.getElementsByTagName("TxnDes")!=null)
				m.setTxnDes(doc.getElementsByTagName("TxnDes").item(0).getTextContent());
			if(doc.getElementsByTagName("TxnDate")!=null)
				m.setTxnDate(doc.getElementsByTagName("TxnDate").item(0).getTextContent());
			if(doc.getElementsByTagName("TxnBnkID")!=null)
				m.setTxnBnkID(doc.getElementsByTagName("TxnBnkID").item(0).getTextContent());
			if(doc.getElementsByTagName("ChannelTxnId")!=null)
				m.setChannelTxnId(doc.getElementsByTagName("ChannelTxnId").item(0).getTextContent());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	class UPDATESTATUS{
		private String Result =""; 
		private String PartID  ="";
		private String Channel ="";
		private String TxnStsRemark  ="";
		private String TxnSts  ="";
		private String TxnDes  ="";
		private String TxnDate   ="";
		private String TxnBnkID   ="";
		private String ChannelTxnId   ="";
		public String getResult() {
			return Result;
		}
		public void setResult(String result) {
			Result = result;
		}
		public String getPartID() {
			return PartID;
		}
		public void setPartID(String partID) {
			PartID = partID;
		}
		public String getChannel() {
			return Channel;
		}
		public void setChannel(String channel) {
			Channel = channel;
		}
		public String getTxnStsRemark() {
			return TxnStsRemark;
		}
		public void setTxnStsRemark(String txnStsRemark) {
			TxnStsRemark = txnStsRemark;
		}
		public String getTxnSts() {
			return TxnSts;
		}
		public void setTxnSts(String txnSts) {
			TxnSts = txnSts;
		}
		public String getTxnDes() {
			return TxnDes;
		}
		public void setTxnDes(String txnDes) {
			TxnDes = txnDes;
		}
		public String getTxnDate() {
			return TxnDate;
		}
		public void setTxnDate(String txnDate) {
			TxnDate = txnDate;
		}
		public String getTxnBnkID() {
			return TxnBnkID;
		}
		public void setTxnBnkID(String txnBnkID) {
			TxnBnkID = txnBnkID;
		}
		public String getChannelTxnId() {
			return ChannelTxnId;
		}
		public void setChannelTxnId(String channelTxnId) {
			ChannelTxnId = channelTxnId;
		}
		
		
	}
	PUSHSTATUS GetPUSHSTATUS(String Xml) {
		
		PUSHSTATUS m = new PUSHSTATUS();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(Xml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("TxTp")!=null)
			m.setTxTp(doc.getElementsByTagName("TxTp").item(0).getTextContent());
			if(doc.getElementsByTagName("TxId")!=null)
				m.setTxId(doc.getElementsByTagName("TxId").item(0).getTextContent());
			if(doc.getElementsByTagName("CusID")!=null)
				m.setCusID(doc.getElementsByTagName("CusID").item(0).getTextContent());
			if(doc.getElementsByTagName("CusPhone")!=null)
				m.setCusPhone(doc.getElementsByTagName("CusPhone").item(0).getTextContent());
			if(doc.getElementsByTagName("Status")!=null)
				m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
			if(doc.getElementsByTagName("Reason")!=null)
				m.setReason(doc.getElementsByTagName("Reason").item(0).getTextContent());
			if(doc.getElementsByTagName("RefNumber")!=null)
				m.setRefNumber(doc.getElementsByTagName("RefNumber").item(0).getTextContent());
			if(doc.getElementsByTagName("TokenInfo")!=null)
				m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	class PUSHSTATUS{
		private String TxTp=""; 
		private String TxId ="";
		private String CusID="";
		private String CusPhone ="";
		private String Status ="";
		private String Reason ="";
		private String RefNumber ="";
		private String TokenInfo ="";
		public String getTxTp() {
			return TxTp;
		}
		public void setTxTp(String txTp) {
			TxTp = txTp;
		}
		public String getTxId() {
			return TxId;
		}
		public void setTxId(String txId) {
			TxId = txId;
		}
		public String getCusID() {
			return CusID;
		}
		public void setCusID(String cusID) {
			CusID = cusID;
		}
		public String getCusPhone() {
			return CusPhone;
		}
		public void setCusPhone(String cusPhone) {
			CusPhone = cusPhone;
		}
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		public String getReason() {
			return Reason;
		}
		public void setReason(String reason) {
			Reason = reason;
		}
		public String getRefNumber() {
			return RefNumber;
		}
		public void setRefNumber(String refNumber) {
			RefNumber = refNumber;
		}
		public String getTokenInfo() {
			return TokenInfo;
		}
		public void setTokenInfo(String tokenInfo) {
			TokenInfo = tokenInfo;
		}		
	}
	
	@GetMapping(path = "/createactiveurl",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String CreateActiveUrlRq(@RequestParam("CustomerName") String CustomerName,@RequestParam("MobileNumber") String MobileNumber) {
		Gson gson = new Gson();
		return gson.toJson(GetCreateActiveUrl(CustomerName, MobileNumber));
	}
	public String PostCreateActiveUrl(String CustomerName, String MobileNumber) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
				+ "   <soapenv:Header/>"
				+ "   <soapenv:Body>"
				+ "      <v1:ActiveUrlReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1 Partner2TCB.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "         <v1:ReqGnlInf>" + "            <v1:Id>"
				+ uuid.toString()
				+ "</v1:Id>"
				+ "            <v1:TxId>"
				+ TxId
				+ "</v1:TxId>"
				+ "            <v1:CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</v1:CreDtTm>"
				+ "            <v1:RoutingRule>CreateActiveUrl</v1:RoutingRule>"
				+ "            <v1:Sgntr>"
				+ "               <v1:Sgntr1>"
				+ CreateActiveUrl(TxId, CustomerName, MobileNumber)
				+ "</v1:Sgntr1>"
				+ "            </v1:Sgntr>"
				+ "         </v1:ReqGnlInf>"
				+ "         <v1:Envt>"
				+ "            <v1:TrgtPty>"
				+ "               <v1:Nm>TCB</v1:Nm>"
				+ "            </v1:TrgtPty>"
				+ "            <v1:SrcPty>"
				+ "               <v1:Nm>MSBC</v1:Nm>"
				+ "            </v1:SrcPty>"
				+ "            <v1:Rqstr>"
				+ "               <v1:Nm>Call from MASAN system</v1:Nm>"
				+ "            </v1:Rqstr>"
				+ "         </v1:Envt>"
				+ "         <v1:ReqInf>"
				+ "            <v1:CustomerId>+84"
				+ MobileNumber.substring(1)
				+ "</v1:CustomerId>"
				+ "            <v1:CustomerName>"
				+ CustomerName
				+ "</v1:CustomerName>"
				+ "           <v1:MobileNumber>"
				+ MobileNumber
				+ "</v1:MobileNumber>"
				+ "         </v1:ReqInf>"
				+ "      </v1:ActiveUrlReq>"
				+ "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CreateActiveUrl");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

	MyResult GetCreateActiveUrl(String CustomerName, String MobileNumber) {
		String tibcoxml = PostCreateActiveUrl(CustomerName, MobileNumber);

		MyResult m = new MyResult();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("Status").item(0);
			if(doc.getElementsByTagName("ActiveUrlLink")!=null) 
			m.setValue(doc.getElementsByTagName("ActiveUrlLink")
					.item(0).getTextContent());
			m.setCode(respon.getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	
	@GetMapping(path = "/deletecardlink",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String DeleteCardLink(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
			 @RequestParam("TokenInfo") String TokenInfo) {
		Gson gson = new Gson();
		return gson.toJson(GetDeleteCardLink(TransactionWalnetID, "",TokenInfo, "", "", ""));
	}
	public String PostDeleteCardLink(String TransactionWalnetID,
			String CardInfo, String TokenInfo, String TrxnAmount, String Otp,
			String OtpTranId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
				+ "   <soapenv:Header/>"
				+ "   <soapenv:Body>"
				+ "      <DeleteCardTokenReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1 Partner2TCB.xsd\" xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "         <ReqGnlInf>" + "            <Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "            <TxId>"
				+ TxId
				+ "</TxId>"
				+ "            <CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "            <RoutingRule>DeleteCardTokenRequest</RoutingRule>"
				+ "            <Desc/>"
				+ "            <HeaderHashStr/>"
				+ "            <Sgntr>"
				+ "               <Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "</Sgntr1>"
				+ "            </Sgntr>"
				+ "         </ReqGnlInf>"
				+ "         <Envt>"
				+ "            <TrgtPty>"
				+ "               <Nm>TCB</Nm>"
				+ "            </TrgtPty>"
				+ "            <SrcPty>"
				+ "               <Nm>MBC</Nm>"
				+ "            </SrcPty>"
				+ "            <Rqstr>"
				+ "               <Nm>Call from MBC system</Nm>"
				+ "            </Rqstr>"
				+ "         </Envt>"
				+ "         <ReqInf>"
				+ "            <TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "            <TerminalId>11021957</TerminalId>"
				+ "            <TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "         </ReqInf>"
				+ "      </DeleteCardTokenReq>"
				+ "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "DeleteCardToken");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	MyResult GetDeleteCardLink(String TransactionWalnetID, String CardInfo,
			String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
		String tibcoxml = PostDeleteCardLink(TransactionWalnetID, CardInfo,
				TokenInfo, TrxnAmount, Otp, OtpTranId);
		MyResult m = new MyResult();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("Status").item(0);
			m.setValue(respon.getTextContent());
			m.setCode(respon.getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	
	@GetMapping(path = "/verifyotp",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String VerifyOTP(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo, @RequestParam("Otp") String Otp,@RequestParam("RoutingRule") String RoutingRule,
			 @RequestParam("OtpTranId") String OtpTranId) {
		Gson gson = new Gson();
		return gson.toJson(GetVerifyOTP(TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), "", Otp, OtpTranId,RoutingRule));
	}
	public String PostVerifyOTP(String TransactionWalnetID, String CardInfo,
			String TokenInfo, String TrxnAmount, String Otp, String OtpTranId, String RoutingRule) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		String postdata = "<VerifyOTPReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
				+ "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "	<ReqGnlInf>" + "		<Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "		<TxId>"
				+ TxId
				+ "</TxId>"
				+ "		<CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "		<RoutingRule>"+RoutingRule+"</RoutingRule>"
				+ "		<Desc/>"
				+ "		<HeaderHashStr/>"
				+ "		<Sgntr>"
				+ "			<Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "</ Sgntr1>"
				+ "		</Sgntr>"
				+ "	</ReqGnlInf>"
				+ "	<Envt>"
				+ "		<TrgtPty>"
				+ "			<Nm>CARD</Nm>"
				+ "		</TrgtPty>"
				+ "		<SrcPty>"
				+ "			<Nm>MBC</Nm>"
				+ "		</SrcPty>"
				+ "		<Rqstr>"
				+ "			<Nm>CARD</Nm>"
				+ "		</Rqstr>"
				+ "	</Envt>"
				+ "	<ReqInf>"
				+ "		<TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "		<TerminalId>11021957</TerminalId>"
				+ "		<TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "		<OtpTranId>"+OtpTranId+"</OtpTranId>"
				+ "		<OtpNumber>"
				+ Otp
				+ "</OtpNumber>" + "	</ReqInf>" + "</VerifyOTPReq>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "VerifyOTP");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	VERIFYOTP GetVerifyOTP(String TransactionWalnetID, String CardInfo,
			String TokenInfo, String TrxnAmount, String Otp, String OtpTranId,String RoutingRule) {
		String tibcoxml = PostVerifyOTP(TransactionWalnetID, CardInfo,
				TokenInfo, TrxnAmount, Otp, OtpTranId,RoutingRule);
		VERIFYOTP m = new VERIFYOTP();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Status")!=null) 
			m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
			if(doc.getElementsByTagName("ErrCd")!=null) 
			m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
			if(doc.getElementsByTagName("ErrMsg")!=null) 
			m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
			if(doc.getElementsByTagName("OtpTranId")!=null) 
			m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
			if(doc.getElementsByTagName("RefTranId")!=null) 
			m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent()); 
			if(doc.getElementsByTagName("TokenInfo")!=null) 
			m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent()); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	class VERIFYOTP
	{
		String Status="";
		String ErrCd="";
		String ErrMsg="";
		String OtpTranId="";
		String RefTranId="";
		String TokenInfo="";
		public String getTokenInfo() {
			return TokenInfo;
		}
		public void setTokenInfo(String tokenInfo) {
			TokenInfo = tokenInfo;
		}
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		public String getErrCd() {
			return ErrCd;
		}
		public void setErrCd(String errCd) {
			ErrCd = errCd;
		}
		public String getErrMsg() {
			return ErrMsg;
		}
		public void setErrMsg(String errMsg) {
			ErrMsg = errMsg;
		}
		public String getOtpTranId() {
			return OtpTranId;
		}
		public void setOtpTranId(String otpTranId) {
			OtpTranId = otpTranId;
		}
		public String getRefTranId() {
			return RefTranId;
		}
		public void setRefTranId(String refTranId) {
			RefTranId = refTranId;
		}
		
	}

	@GetMapping(path = "/cardtokentrxntopup",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String CardTokenTrxnTopuP(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo,@RequestParam("TrxnAmount") String TrxnAmount,  
			 @RequestParam("Des") String Des) {
		Gson gson = new Gson();
		return gson.toJson(GetCardTokenTrxnTopup(Des,TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo),TrxnAmount, "", ""));
	}
	public String PostCardTokenTrxnTopup(String Des,String TransactionWalnetID,
			String CardInfo, String TokenInfo, String TrxnAmount, String Otp,
			String OtpTranId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");

		String postdata = "<CardTokenTrxnTopupReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1 Partner2TCB.xsd\""
				+ "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "	<ReqGnlInf>" + "		<Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "		<TxId>"
				+ TxId
				+ "</TxId>"
				+ "		<CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "		<RoutingRule>CardTokenTrxnTopupRequest</RoutingRule>"
				+ "		<Desc/>"
				+ "		<HeaderHashStr/>"
				+ "		<Sgntr>"
				+ "			<Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "			</ Sgntr1>"
				+ "		</Sgntr>"
				+ "	</ReqGnlInf>"
				+ "	<Envt>"
				+ "		<TrgtPty>"
				+ "			<Nm>CARD</Nm>"
				+ "		</TrgtPty>"
				+ "		<SrcPty>"
				+ "			<Nm>MBC</Nm>"
				+ "		</SrcPty>"
				+ "		<Rqstr>"
				+ "			<Nm>Call from PARTNER_CODE system</Nm>"
				+ "		</Rqstr>"
				+ "	</Envt>"
				+ "	<ReqInf>"
				+ "		<TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "		<TerminalId>11021957</TerminalId>"
				+ "		<TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "		<TrxnAmount>"
				+ TrxnAmount
				+ "</TrxnAmount>"
				+ "		<TrxnCurrency>vnd</TrxnCurrency>"
				+ "		<Description>"+Des+"</Description>"
				+ "		<InvoiceNumber>"
				+ TransactionWalnetID
				+ "</InvoiceNumber>"
				+ "		<RequestDateTime>"
				+ RequestDt.format(LocalDateTime.now())
				+ "</RequestDateTime>"
				+ "	</ReqInf>"
				+ "</CardTokenTrxnTopupReq> ";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnTopup");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	CARDTOKENTRXN GetCardTokenTrxnTopup(String Des,String transactionWalletId, String CardInfo,
			String TokenInfo, String TrxnAmount, String Otp, String OtpTranId ) {
		String tibcoxml = PostCardTokenTrxnTopup(Des,transactionWalletId, CardInfo,
				TokenInfo, TrxnAmount, Otp, OtpTranId);
		CARDTOKENTRXN m = new CARDTOKENTRXN();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Status")!=null) 
			m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
			if(doc.getElementsByTagName("ErrCd")!=null) 
			m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
			if(doc.getElementsByTagName("ErrMsg")!=null) 
			m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
			if(doc.getElementsByTagName("OtpTranId")!=null) 
			m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
			if(doc.getElementsByTagName("RefTranId")!=null) 
			m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
			if(doc.getElementsByTagName("TokenInfo")!=null) 
			m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	class CARDTOKENTRXN
	{
		String Status="";
		String ErrCd="";
		String ErrMsg="";
		String OtpTranId="";
		String RefTranId="";
		String TokenInfo="";
		public String getTokenInfo() {
			return TokenInfo;
		}
		public void setTokenInfo(String tokenInfo) {
			TokenInfo = tokenInfo;
		}
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		public String getErrCd() {
			return ErrCd;
		}
		public void setErrCd(String errCd) {
			ErrCd = errCd;
		}
		public String getErrMsg() {
			return ErrMsg;
		}
		public void setErrMsg(String errMsg) {
			ErrMsg = errMsg;
		}
		public String getOtpTranId() {
			return OtpTranId;
		}
		public void setOtpTranId(String otpTranId) {
			OtpTranId = otpTranId;
		}
		public String getRefTranId() {
			return RefTranId;
		}
		public void setRefTranId(String refTranId) {
			RefTranId = refTranId;
		}
		
	}
	
	@GetMapping(path = "/cardtokenTrxnsale",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String CardTokenTrxnSale(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo,@RequestParam("TrxnAmount") String TrxnAmount ,
			 @RequestParam("Des") String Des) {
		Gson gson = new Gson();
		return gson.toJson(GetCardTokenTrxnSale(Des,TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo),TrxnAmount, "", ""));
	}

	public String PostCardTokenTrxnSale(String Description,
			String TransactionWalnetID, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");

		String postdata = "<CardTokenTrxnSaleReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
				+ "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "	<ReqGnlInf>" + "		<Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "		<TxId>"
				+ TxId
				+ "</TxId>"
				+ "		<CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "		<RoutingRule>CardTokenTrxnSaleRequest</RoutingRule>"
				+ "		<Desc/>"
				+ "		<HeaderHashStr/>"
				+ "		<Sgntr>"
				+ "			<Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "			</ Sgntr1>"
				+ "		</Sgntr>"
				+ "	</ReqGnlInf>"
				+ "	<Envt>"
				+ "		<TrgtPty>"
				+ "			<Nm>CARD</Nm>"
				+ "		</TrgtPty>"
				+ "		<SrcPty>"
				+ "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK </Nm>"
				+ "		</SrcPty>"
				+ "		<Rqstr>"
				+ "			<Nm>Call from PARTNER_CODE system</Nm>"
				+ "		</Rqstr>"
				+ "	</Envt>"
				+ "	<ReqInf>"
				+ "		<TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "		<TerminalId>11021957</TerminalId>"
				+ "		<TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "		<TrxnAmount>"
				+ TrxnAmount
				+ "</TrxnAmount>"
				+ "		<TrxnCurrency>vnd</TrxnCurrency>"
				+ "		<Description>"
				+ Description
				+ "</Description>"
				+ "		<InvoiceNumber>"
				+ TransactionWalnetID
				+ "</InvoiceNumber>"
				+ "		<RequestDateTime>"
				+ RequestDt.format(LocalDateTime.now())
				+ "</RequestDateTime>"
				+ "	</ReqInf>"
				+ "</CardTokenTrxnSaleReq>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnSale");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	CARDTOKENTRXN GetCardTokenTrxnSale(String Description,
			String transactionWalletId, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		String tibcoxml = PostCardTokenTrxnSale(Description,
				transactionWalletId, CardInfo, TokenInfo, TrxnAmount, Otp,
				OtpTranId);
		CARDTOKENTRXN m = new CARDTOKENTRXN();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Status")!=null) 
				m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrCd")!=null) 
				m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrMsg")!=null) 
				m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
				if(doc.getElementsByTagName("OtpTranId")!=null) 
				m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("RefTranId")!=null) 
				m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("TokenInfo")!=null) 
				m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	
	@GetMapping(path = "/cardtokentrxnwithdraw",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String CardTokenTrxnWithdraw(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo,@RequestParam("TrxnAmount") String TrxnAmount  ,@RequestParam("Des") String Des) {
		Gson gson = new Gson();
		return gson.toJson(GetCardTokenTrxnWithdraw(Des,TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo),TrxnAmount, "", ""));
	}
	
	public String PostCardTokenTrxnWithdraw(String Description,
			String TransactionWalnetID, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
		String postdata = "<CardTokenTrxnWithdrawReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
				+ "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "	<ReqGnlInf>" + "		<Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "		<TxId>"
				+ TxId
				+ "</TxId>"
				+ "		<CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "		<RoutingRule>CardTokenTrxnSaleRequest</RoutingRule>"
				+ "		<Desc/>"
				+ "		<HeaderHashStr/>"
				+ "		<Sgntr>"
				+ "			<Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "			</ Sgntr1>"
				+ "		</Sgntr>"
				+ "	</ReqGnlInf>"
				+ "	<Envt>"
				+ "		<TrgtPty>"
				+ "			<Nm>TCB</Nm>"
				+ "		</TrgtPty>"
				+ "		<SrcPty>"
				+ "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK</Nm>"
				+ "		</SrcPty>"
				+ "		<Rqstr>"
				+ "			<Nm>Call from PARTNER_CODE system</Nm>"
				+ "		</Rqstr>"
				+ "	</Envt>"
				+ "	<ReqInf>"
				+ "		<TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "		<TerminalId>11021957</TerminalId>"
				+ "		<TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "		<TrxnAmount>"
				+ TrxnAmount
				+ "</TrxnAmount>"
				+ "		<TrxnCurrency>vnd</TrxnCurrency>"
				+ "		<Description>"
				+ Description
				+ "</Description>"
				+ "		<InvoiceNumber>"
				+ TransactionWalnetID
				+ "</InvoiceNumber>"
				+ "		<RequestDateTime>yyyy-MM-dd HH:mi:ss</RequestDateTime>"
				+ "	</ReqInf>" + "</CardTokenTrxnWithdrawReq> ";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnWithdraw");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	CARDTOKENTRXN GetCardTokenTrxnWithdraw(String Description,
			String transactionWalletId, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		String tibcoxml = PostCardTokenTrxnWithdraw(Description,
				transactionWalletId, CardInfo, TokenInfo, TrxnAmount, Otp,
				OtpTranId);
		CARDTOKENTRXN m = new CARDTOKENTRXN();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Status")!=null) 
				m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrCd")!=null) 
				m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrMsg")!=null) 
				m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
				if(doc.getElementsByTagName("OtpTranId")!=null) 
				m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("RefTranId")!=null) 
				m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("TokenInfo")!=null) 
				m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	
	@GetMapping(path = "/cardtokentrxnrefund",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String CardTokenTrxnRefund(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo,@RequestParam("TrxnAmount") String TrxnAmount,  @RequestParam("Des") String Des) {
		Gson gson = new Gson();
		return gson.toJson(GetCardTokenTrxnRefund(Des,TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo),TrxnAmount, "", ""));
	}
	
	public String PostCardTokenTrxnRefund(String Description,
			String TransactionWalnetID, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
		String postdata = "<CardTokenTrxnRefundReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
				+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "	<ReqGnlInf>" + "		<Id>"
				+ uuid.toString()
				+ "</Id>"
				+ "		<TxId>"
				+ TxId
				+ "</TxId>"
				+ "		<CreDtTm>"
				+ CreDtTm.format(LocalDateTime.now())
				+ "</CreDtTm>"
				+ "		<RoutingRule>CardTokenTrxnRefundRequest</RoutingRule>"
				+ "		<Desc/>"
				+ "		<HeaderHashStr/>"
				+ "		<Sgntr>"
				+ "			<Sgntr1>"
				+ otherApi(CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId)
				+ "			</ Sgntr1>"
				+ "		</Sgntr>"
				+ "	</ReqGnlInf>"
				+ "	<Envt>"
				+ "		<TrgtPty>"
				+ "			<Nm>CARD</Nm>"
				+ "		</TrgtPty>"
				+ "		<SrcPty>"
				+ "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK</Nm>"
				+ "		</SrcPty>"
				+ "		<Rqstr>"
				+ "			<Nm>Call from PARTNER system</Nm>"
				+ "		</Rqstr>"
				+ "	</Envt>"
				+ "	<ReqInf>"
				+ "		<TokenInfo>"
				+ TokenInfo
				+ "</TokenInfo>"
				+ "		<TerminalId>11021957</TerminalId>"
				+ "		<RefundType>MR</RefundType>"
				+ "		<TraceNumber>"
				+ TransactionWalnetID
				+ "</TraceNumber>"
				+ "		<TrxnAmount>"
				+ TrxnAmount
				+ "</TrxnAmount>"
				+ "		<TrxnCurrency>vnd</TrxnCurrency>"
				+ "		<Description>"+Description+"</Description>"
				+ "		<OrigTranId>123456</OrigTranId>"
				+ "		<RequestDateTime>"+RequestDt.format(LocalDateTime.now())+"</RequestDateTime>"
				+ "	</ReqInf>"
				+ "</CardTokenTrxnRefundReq>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnRefund");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	
	CARDTOKENTRXN GetCardTokenTrxnRefund(String Description,
			String transactionWalletId, String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		String tibcoxml = PostCardTokenTrxnRefund(Description,
				transactionWalletId, CardInfo, TokenInfo, TrxnAmount, Otp,
				OtpTranId);
		CARDTOKENTRXN m = new CARDTOKENTRXN();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			if(doc.getElementsByTagName("Status")!=null) 
				m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrCd")!=null) 
				m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
				if(doc.getElementsByTagName("ErrMsg")!=null) 
				m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
				if(doc.getElementsByTagName("OtpTranId")!=null) 
				m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("RefTranId")!=null) 
				m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
				if(doc.getElementsByTagName("TokenInfo")!=null) 
					m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

	
	/////////////////////////////
	
	@GetMapping(path = "/fundtransfer",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String FundTransfer(@RequestParam("Description") String Description, 
			 @RequestParam("AcctId") String AcctId,@RequestParam("AcctTitl") String AcctTitl,  @RequestParam("TxAmt") String TxAmt) {
		Gson gson = new Gson();
		return gson.toJson(GetFundTransfer(Description,Description,AcctTitl,TxAmt));
	}
	
	
	
	public String encriptFundTransfer(String Description, String AcctId,
			String AcctTitl, String TxAmt) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
		String strRequestDt = RequestDt.format(LocalDateTime.now());
		String _pathPublic = "/mobivi.cer";
		
		String ReqInf = JavaSignSHA256_V2.signData_SHA256("FundTransfer" + uuid.toString()+ AcctId + AcctTitl + strRequestDt + TxAmt);
		return PostFundTransfer(Description, uuid.toString(), TxId, CreDtTm.format(LocalDateTime.now()),ReqInf);
	}

	public String PostFundTransfer(String Description, String id, String TxId,
			String CreDtTm,  String ReqInf) {

		String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
				+ "   <soapenv:Header/>"
				+ "   <soapenv:Body>"
				+ "      <v1:XferReq xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
				+ "         <v1:ReqGnlInf>" + "            <v1:Id>"
				+ id
				+ "</v1:Id>"
				+ "            <v1:TxId>"
				+ TxId
				+ "</v1:TxId>"
				+ "            <v1:CreDtTm>"
				+ CreDtTm
				+ "</v1:CreDtTm>"
				+ "            <v1:PmtTp>CASHOUT</v1:PmtTp>"
				+ "            <v1:Desc>"
				+ Description
				+ "</v1:Desc>"
				+ "            <v1:Sgntr>"
				+ "               <v1:Sgntr1 user=\"public_key_sha256_1\">ZAQF3QvOwSX3tJM9JjzSKnqnwCUPut7cr7EPCBJuOib8XA+920MnQ6jIxvTNu1Po2MYAtoEJodJaCrNIqjstqRjNBH+anBjKXluSiR5DGkyFuonyx01lCVG+McmSVu/nWNheALf022NDnBJDXIMFRutVjGJ+k2sEEejpPNmPnfmZM+0jYo9bZ3a6T4yrUO7zh/eKitObiOE0yOm+U3eeEsGl/rnAzcTV7sMVmSua97KU+kx9poAwOyj32GgLOmlews/ivqVjo4EsRlgYc2RDitKNH+eR5z2WB4Z1aPPbrwau8qvf7n91ewetke5qm+ZTV+rEetFRJpe5jiJlMfLijw==</v1:Sgntr1>"
				+ "               <v1:Sgntr2 user=\"public_key_sha256_2\">kGeSghIuozvRq3cO7lAg0NPE07gjmK/owXSo6QcH/odMO3HYAqNS4l7PdNLeWSfhnwXL3MhowaGcX1TPn7UKMhFD/9U3IGGFrIaWTRkMeJR+JTeXm4mkcUIv2Bd6V1yqXnBv8Z8Klta6NAqO4Gt+gkamoQ/2SuNev5y2NfUHk9OSwH8hAJuKM/iYnu2eduiNAQoR9ROsriBYAwRe6JvI88+0YI9D7TPAhCzHYhZJCzhI5IWbUT6uvUU+MfTQs87lWz9ClB43R9EwIh+IfnNtBdWMHPHpXn0KdcERtItqgu4KE8ABlGoDE0J2LYWFqIPzGQjb42hkc6GVS2yL6GIjOw==</v1:Sgntr2>"
				+ "            </v1:Sgntr>"
				+ "         </v1:ReqGnlInf>"
				+ "         <v1:Envt>"
				+ "            <v1:SrcPty>"
				+ "               <v1:Nm>FWK</v1:Nm>"
				+ "            </v1:SrcPty>"
				+ "            <v1:TrgtPty>"
				+ "               <v1:Nm>H2H</v1:Nm>"
				+ "            </v1:TrgtPty>"
				+ "            <v1:Rqstr>"
				+ "               <v1:Nm>TCB</v1:Nm>"
				+ "            </v1:Rqstr>"
				+ "            <v1:TrgtPty/>"
				+ "         </v1:Envt>"
				+ "         <v1:ReqInf>"
				+ ReqInf
				+ "</v1:ReqInf>"
				+ "      </v1:XferReq>"
				+ "   </soapenv:Body>"
				+ "</soapenv:Envelope>";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnRefund");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	MyResult GetFundTransfer(String Description, String AcctId,
			String AcctTitl, String TxAmt) {
		String tibcoxml = encriptFundTransfer(Description, AcctId, AcctTitl,
				TxAmt);
		MyResult m = new MyResult();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			Node respon = doc.getElementsByTagName("Status").item(0);
			Node RefTranId = doc.getElementsByTagName("RefTranId").item(0);
			m.setValue(RefTranId.getTextContent());
			m.setCode(respon.getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	/////////////////////////////
	
	@GetMapping(path = "/accountinfo",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String AccountInfo(@RequestParam("TransactionWalnetID") String TransactionWalnetID, 
			 @RequestParam("TokenInfo") String TokenInfo,@RequestParam("TrxnAmount") String TrxnAmount,  @RequestParam("Des") String Des) {
		Gson gson = new Gson();
		return gson.toJson(GetCardTokenTrxnRefund(Des,TransactionWalnetID, "",JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo),TrxnAmount, "", ""));
	}
	
	public String encriptAccountInfo( String SvcName,  
			String ClientTerminalSeqNum,String Bindata,String Token,String Name,String ORG,String SubjectRole,  String MsgGroupReference,String AcctId) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
		String strRequestDt = RequestDt.format(LocalDateTime.now());
		String _pathPublic = "/mobivi.cer";
		String ReqInf = JavaSignSHA256_V2.signData_SHA256("AcctInq" + uuid.toString() +  SvcName +Name+SubjectRole +MsgGroupReference+AcctId);
		return PostAccountInfo(  SvcName,    uuid.toString(),  TxId, 
				 ClientTerminalSeqNum, Bindata, Token, Name, ORG, SubjectRole, CreDtTm.format(LocalDateTime.now()),MsgGroupReference);
	}

	public String PostAccountInfo(String SvcName, String id, String TxId, 
			String ClientTerminalSeqNum,String Bindata,String Token,String Name,String ORG,String SubjectRole,String CreDtTm,String MsgGroupReference) {

		String postdata = "<soapenv:Envelope"+
 "	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""+
 "	xmlns=\"http://www.techcombank.com.vn/account/v1\">"+
 "	<soapenv:Header/>"+
 "	<soapenv:Body>"+
 "		<AcctInqRq>"+
 "			<RqUID>"+id+"</RqUID>"+
 "			<MsgRqHdr>"+
 "				<SvcIdent>"+
 "					<SvcName>"+SvcName+"</SvcName>"+
 "				</SvcIdent>"+
 "				<CredentialsRqHdr>"+
 "					<SubjectRole>"+SubjectRole+"</SubjectRole>"+
 "					<OvrdExceptionCode>ACCT</OvrdExceptionCode>"+
 "				</CredentialsRqHdr>"+
 "				<ContextRqHdr>"+
 "					<MsgGroupReference>"+MsgGroupReference+"</MsgGroupReference>"+
 "					<ClientTerminalSeqNum>"+ClientTerminalSeqNum+"</ClientTerminalSeqNum>"+
 "					<ClientDt>"+CreDtTm+"</ClientDt>"+
 "					<ClientApp>"+
 "						<Org>"+ORG+"</Org>"+
 "						<Name>"+Name+"</Name>"+
 "						<Version>1000</Version>"+
 "					</ClientApp>"+
 "				</ContextRqHdr>"+
 "			</MsgRqHdr>"+
 "			<RecCtrlIn>"+
 "				<Cursor>"+
 "					<BinLength>1</BinLength>"+
 "					<BinData>"+Bindata+"</BinData>"+
 "				</Cursor>"+
 "				<Token>"+Token+"</Token>"+
 "			</RecCtrlIn>"+
 "		</AcctInqRq>"+
 "	</soapenv:Body>"+
 "</soapenv:Envelope> ";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");
		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "AccountInfo");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	MyResult GetAccountInfo( String SvcName,  
			String ClientTerminalSeqNum, String Bindata, String Token, String Name,String ORG,String SubjectRole, String  MsgGroupReference,String  AcctId) {
		String tibcoxml = encriptAccountInfo(  SvcName,  
				 ClientTerminalSeqNum, Bindata, Token, Name, ORG, SubjectRole,   MsgGroupReference,  AcctId);
		MyResult m = new MyResult();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			Node ServerStatusCode = doc.getElementsByTagName("ServerStatusCode").item(0); 
			m.setCode(ServerStatusCode.getTextContent());
			Node StatusDesc = doc.getElementsByTagName("StatusDesc").item(0); 
			m.setValue(StatusDesc.getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	
	
 
	public static class BankAccountInfo{
		private String CITAD=""; 
		private String BkCd ="";
		private String BkNm ="";
		private String BrnchNm="";
		private String  StatPrvc    ="";          
		private String ActvSts="";
		private String IsCentralizedBank="";
		public String getCITAD() {
			return CITAD;
		}
		public void setCITAD(String cITAD) {
			CITAD = cITAD;
		}
		public String getBkCd() {
			return BkCd;
		}
		public void setBkCd(String bkCd) {
			BkCd = bkCd;
		}
		public String getBkNm() {
			return BkNm;
		}
		public void setBkNm(String bkNm) {
			BkNm = bkNm;
		}
		public String getBrnchNm() {
			return BrnchNm;
		}
		public void setBrnchNm(String brnchNm) {
			BrnchNm = brnchNm;
		}
		public String getStatPrvc() {
			return StatPrvc;
		}
		public void setStatPrvc(String statPrvc) {
			StatPrvc = statPrvc;
		}
		public String getActvSts() {
			return ActvSts;
		}
		public void setActvSts(String actvSts) {
			ActvSts = actvSts;
		}
		public String getIsCentralizedBank() {
			return IsCentralizedBank;
		}
		public void setIsCentralizedBank(String isCentralizedBank) {
			IsCentralizedBank = isCentralizedBank;
		}
		
	}

	
	
	
	/////////////////////////////
	@GetMapping(path = "/inqlistbankinfo",  consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@ResponseBody
	public String InqListBankInfo(@RequestParam("CustomerID") String CustomerID) {
		Gson gson = new Gson();
		return gson.toJson(GetInqListBankInfo(CustomerID));
	}
	public String PostInqListBankInfo(String CustomerID) {
		UUID uuid = UUID.randomUUID();
		String TxId = "MBC" + uuid.toString();
		DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
		String postdata = "<soapenv:Envelope"+
 "	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""+
 "	xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"+
 "	< soapenv:Header />"+
 "	<soapenv:Body>"+
 "		<InqBnkInfReq"+
 "			xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"+
 "			<ReqGnlInf>"+
 "				<Id>"+ uuid.toString()+"</Id>"+
 "				<TxId>"+TxId+"</TxId>"+
 "				<CreDtTm>"+CreDtTm.format(LocalDateTime.now())+"</CreDtTm>"+
 "				<Desc>Inquiry List Bank Info</Desc>"+
 "				<Sgntr/>"+
 "			</ReqGnlInf>"+
 "			<Envt>"+
 "				<SrcPty>"+
 "					<Nm>"+CustomerID+"</Nm>"+
 "				</SrcPty>"+
 "				<Rqstr>"+
 "					<Nm>Call from Customer</Nm>"+
 "				</Rqstr>"+
 "			</Envt>"+
 "			<ReqInf>"+
 "				<BkId>ALL</BkId>"+
 "			</ReqInf>"+
 "		</InqBnkInfReq>"+
 "	</soapenv:Body>"+
 "</soapenv:Envelope> ";
		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost("");

		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", "CardTokenTrxnRefund");
		try {
			xmlEntity = new StringEntity(postdata);
			System.out.println(postdata);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			post.releaseConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	BankAccountInfo GetInqListBankInfo(String  CustomerID) {
		String tibcoxml = PostInqListBankInfo(  CustomerID);
		BankAccountInfo m = new BankAccountInfo();
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(tibcoxml));
			Document doc = db.parse(is);
			Node CITAD = doc.getElementsByTagName("CITAD").item(0);
			Node BkCd = doc.getElementsByTagName("BkCd").item(0);
			Node BkNm = doc.getElementsByTagName("BkCd").item(0);
			Node BrnchNm = doc.getElementsByTagName("BkCd").item(0);
			Node StatPrvc = doc.getElementsByTagName("BkCd").item(0);
			Node ActvSts = doc.getElementsByTagName("BkCd").item(0);
			Node IsCentralizedBank = doc.getElementsByTagName("BkCd").item(0); 
			m.setCITAD( CITAD.getTextContent());
			m.setBkCd(BkCd.getTextContent()); 
			m.setBkNm(BkNm.getTextContent());
			m.setBrnchNm(BrnchNm.getTextContent());
			m.setStatPrvc(StatPrvc.getTextContent());
			m.setActvSts(ActvSts.getTextContent());
			m.setIsCentralizedBank(IsCentralizedBank.getTextContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}

/////////////////
	
	
	
	public static class MyResult {
		private String code = "";
		private String other = "";

		public String getOther() {
			return other;
		}

		public void setOther(String other) {
			this.other = other;
		}

		private String value = "";

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static String CreateActiveUrl(String TxId, String CustomerName,
			String MobileNumber) {

		String RoutingRule = "CreateActiveUrl";
		String TrgtPty = "TCB";
		String SrcPty = "MBC";
		String CustomerID = "+84" + MobileNumber.substring(1);
		return JavaSignSHA256_V2.encryptWithPublicKeyTCB(TxId + RoutingRule + TrgtPty + SrcPty
				+ CustomerID + CustomerName + MobileNumber);
	}

	public static String otherApi(String CardInfo, String TokenInfo,
			String TrxnAmount, String Otp, String OtpTranId) {
		String RoutingRule = "CreateActiveUrl";
		String TrgtPty = "TCB";
		String SrcPty = "MBC";
		String TerminalId = "11021957";
		return JavaSignSHA256_V2.encryptWithPublicKeyTCB(RoutingRule + TrgtPty + SrcPty
				+ TerminalId + CardInfo + TokenInfo + TrxnAmount + Otp
				+ OtpTranId);
	}

 


}
