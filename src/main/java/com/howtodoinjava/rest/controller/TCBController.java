package com.howtodoinjava.rest.controller;

import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;
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
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.vnm.erp.cover.GlobalVariables;
import com.vnm.erp.cover.JavaSecutityEncrypt_V2;
import com.vnm.erp.cover.JavaSignSHA256;
import com.vnm.erp.cover.JavaSignSHA256_V2;

@RestController
@RequestMapping(path = "/tcb")
public class TCBController {

    @GetMapping(path = "/pushstatus", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String pushstatus(@RequestBody String Xml) {
        GlobalVariables.logger.info(Xml);

        // return "<soapenv:Envelope"+
        // "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
        // "<soapenv:Header"+
        // "xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\"/>"+
        // "<soapenv:Body"+
        // "xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"+
        // "<PushStatusRs"+
        // "xmlns=\"http://app.linkcard.tcb.com/\">"+
        // "<RspnInf>"+
        // "<Id>stg11213128755dcb6ba88d0847</Id>"+
        // "<TxId>1234567890</TxId>"+
        // "<CreDtTm>2020-12-04T15:06:45.469+07:00</CreDtTm>"+
        // "<Desc>Trans sale"+
        // "response</Desc>"+
        // "<Sgntr>NO"+
        // "SIGNATURE</Sgntr>"+
        // "</RspnInf>"+
        // "<Envt>"+
        // "<TrgtPty>"+
        // "<Nm>CARD</Nm>"+
        // "</TrgtPty>"+
        // "<SrcPty>"+
        // "<Nm>TCB</Nm>"+
        // "</SrcPty>"+
        // "<Rqstr>"+
        // "<Nm>Response from TCB"+
        // "system</Nm>"+
        // "</Rqstr>"+
        // "</Envt>"+
        // "<RspnSts>"+
        // "<Status>0</Status>"+
        // "<ErrCd>APP-001</ErrCd>"+
        // "<ErrMsg>Da ghi nhan</ErrMsg>"+
        // "</RspnSts>"+
        // "</PushStatusRs>"+
        // "</soapenv:Body>";
        Gson gson = new Gson();
        return gson.toJson(GetPUSHSTATUS(Xml));
    }

    @GetMapping(path = "/updatestatus", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String updatestatus(@RequestBody String xml) {
        GlobalVariables.logger.info(xml);

        UPDATESTATUS update_status = GetUPDATESTATUS(xml);
        String sSource = "UpdateStatus" + update_status.PartID + update_status.Channel + update_status.TxnStsRemark + update_status.TxnSts;
        String sSource_hashed = JavaSignSHA256_V2.signData_SHA256(sSource);

        byte[] signature_encrypted = Base64.getDecoder().decode(update_status.Sgntr1);
        PublicKey pk = JavaSecutityEncrypt_V2.getPulicKey("tcb_mahoa.cer");
        String signature_decrypted = new String(JavaSecutityEncrypt_V2.decrypt2(signature_encrypted, pk));
        String status = "RCCF";
        String code = "000";
        if (signature_decrypted == sSource_hashed) {
            status = "RCCF";
            code = "000";
        } else {
            status = "RCER";
            code = "001";
        }
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<v1:UpdateStatusRspn"
                + "xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "<v1:RspnInf>"
                + "<v1:Id/><v1:TxId/>"
                + "<v1:CreDtTm>" + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + "</v1:CreDtTm>"
                + "<v1:PmtTp/>"
                + "<v1:Desc/>"
                + "<v1:HeaderHashStr/>"
                + "<v1:Sgntr>H2H Payment ACH Intergration v1.0"
                + "53/60\n"
                + "<v1:Sgntr1/>\n"
                + "<v1:Sgntr2/>\n"
                + "</v1:Sgntr>\n"
                + "</v1:RspnInf>\n"
                + "<v1:RspnSts>\n"
                + "<v1:Sts>" + status + "</v1:Sts>\n"
                + "<v1:AddtlStsRsnInf>" + code + "</v1:AddtlStsRsnInf>\n"
                + "</v1:RspnSts>\n"
                + "</v1:UpdateStatusRspn>\n"
                + "</soapenv:Body>\n"
                + "</soapenv:Envelope>";

    }

    UPDATESTATUS GetUPDATESTATUS(String Xml) {

        UPDATESTATUS m = new UPDATESTATUS();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(Xml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Result") != null) {
                m.setResult(doc.getElementsByTagName("Result").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("PartID") != null) {
                m.setPartID(doc.getElementsByTagName("PartID").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("Channel") != null) {
                m.setChannel(doc.getElementsByTagName("Channel").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxnStsRemark") != null) {
                m.setTxnStsRemark(doc.getElementsByTagName("TxnStsRemark").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxnSts") != null) {
                m.setTxnSts(doc.getElementsByTagName("TxnSts").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxnDes") != null) {
                m.setTxnDes(doc.getElementsByTagName("TxnDes").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxnDate") != null) {
                m.setTxnDate(doc.getElementsByTagName("TxnDate").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxnBnkID") != null) {
                m.setTxnBnkID(doc.getElementsByTagName("TxnBnkID").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ChannelTxnId") != null) {
                m.setChannelTxnId(doc.getElementsByTagName("ChannelTxnId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("Sgntr1") != null) {
                m.setChannelTxnId(doc.getElementsByTagName("Sgntr1").item(0).getTextContent());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    class UPDATESTATUS {

        private String Result = "";
        private String PartID = "";
        private String Channel = "";
        private String TxnStsRemark = "";
        private String TxnSts = "";
        private String TxnDes = "";
        private String TxnDate = "";
        private String TxnBnkID = "";
        private String ChannelTxnId = "";
        private String Sgntr1 = "";

        public String getSgntr1() {
            return Sgntr1;
        }

        public void setSgntr1(String result) {
            Sgntr1 = result;
        }

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
            if (doc.getElementsByTagName("TxTp") != null) {
                m.setTxTp(doc.getElementsByTagName("TxTp").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TxId") != null) {
                m.setTxId(doc.getElementsByTagName("TxId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("CusID") != null) {
                m.setCusID(doc.getElementsByTagName("CusID").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("CusPhone") != null) {
                m.setCusPhone(doc.getElementsByTagName("CusPhone").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("Reason") != null) {
                m.setReason(doc.getElementsByTagName("Reason").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefNumber") != null) {
                m.setRefNumber(doc.getElementsByTagName("RefNumber").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    class PUSHSTATUS {

        private String TxTp = "";
        private String TxId = "";
        private String CusID = "";
        private String CusPhone = "";
        private String Status = "";
        private String Reason = "";
        private String RefNumber = "";
        private String TokenInfo = "";

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

    @GetMapping(path = "/createactiveurl", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String CreateActiveUrlRq(@RequestParam("CustomerName") String CustomerName,
            @RequestParam("MobileNumber") String MobileNumber) {
        Gson gson = new Gson();
        return gson.toJson(GetCreateActiveUrl(CustomerName, MobileNumber));
    }

    public String PostCreateActiveUrl(String CustomerName, String MobileNumber) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "   <soapenv:Header/>" + "   <soapenv:Body>"
                + "      <v1:ActiveUrlReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1Partner2TCB.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "         <v1:ReqGnlInf>" + "            <v1:Id>" + uuid.toString() + "</v1:Id>"
                + "            <v1:TxId>" + TxId + "</v1:TxId>" + "            <v1:CreDtTm>"
                + CreDtTm.format(new Date()) + "</v1:CreDtTm>"
                + "            <v1:RoutingRule>CreateActiveUrl</v1:RoutingRule>" + "            <v1:Sgntr>"
                + "               <v1:Sgntr1>" + CreateActiveUrl(TxId, CustomerName, MobileNumber) + "</v1:Sgntr1>"
                + "            </v1:Sgntr>" + "         </v1:ReqGnlInf>" + "         <v1:Envt>"
                + "            <v1:TrgtPty>" + "               <v1:Nm>TCB</v1:Nm>" + "            </v1:TrgtPty>"
                + "            <v1:SrcPty>" + "               <v1:Nm>MSBC</v1:Nm>" + "            </v1:SrcPty>"
                + "            <v1:Rqstr>" + "               <v1:Nm>Call from MASAN system</v1:Nm>"
                + "            </v1:Rqstr>" + "         </v1:Envt>" + "         <v1:ReqInf>"
                + "            <v1:CustomerId>+84" + MobileNumber.substring(1) + "</v1:CustomerId>"
                + "            <v1:CustomerName>" + CustomerName + "</v1:CustomerName>" + "           <v1:MobileNumber>"
                + MobileNumber + "</v1:MobileNumber>" + "         </v1:ReqInf>" + "      </v1:ActiveUrlReq>"
                + "   </soapenv:Body>" + "</soapenv:Envelope>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        try {
        	 
        	DefaultHttpClient httpClient = new DefaultHttpClient(); 
            HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1") ;

            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("Connection", "Keep-Alive");
            post.setHeader("SOAPAction", "CreateActiveUrl");
            xmlEntity = new StringEntity(postdata);
            GlobalVariables.logger.info("CreateActiveUrl request"+postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            GlobalVariables.logger.info("CreateActiveUrl response"+response.getStatusLine().getStatusCode());
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info("CreateActiveUrl Response"+result);post.releaseConnection();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        // we can optionally disable hostname verification. 
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        
        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        
        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        return HttpClients
                .custom()
                .setSSLSocketFactory(connectionFactory)
                .build();
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
            if (doc.getElementsByTagName("ActiveUrlLink") != null) {
                m.setValue(doc.getElementsByTagName("ActiveUrlLink").item(0).getTextContent());
            }
            m.setCode(respon.getTextContent());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    @GetMapping(path = "/deletecardlink", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String DeleteCardLink(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo) {
        Gson gson = new Gson();
        return gson.toJson(GetDeleteCardLink(TransactionWalnetID, "", TokenInfo, "", "", ""));
    }

    public String PostDeleteCardLink(String TransactionWalnetID, String CardInfo, String TokenInfo, String TrxnAmount,
            String Otp, String OtpTranId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "   <soapenv:Header/>" + "   <soapenv:Body>"
                + "      <DeleteCardTokenReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1 Partner2TCB.xsd\" xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "         <ReqGnlInf>" + "            <Id>" + uuid.toString() + "</Id>" + "            <TxId>" + TxId
                + "</TxId>" + "            <CreDtTm>" + CreDtTm.format(new Date()) + "</CreDtTm>"
                + "            <RoutingRule>DeleteCardTokenRequest</RoutingRule>" + "            <Desc/>"
                + "            <HeaderHashStr/>" + "            <Sgntr>" + "               <Sgntr1>"
                + otherApi("DeleteCardTokenRequest",CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "</Sgntr1>" + "            </Sgntr>"
                + "         </ReqGnlInf>" + "         <Envt>" + "            <TrgtPty>" + "               <Nm>TCB</Nm>"
                + "            </TrgtPty>" + "            <SrcPty>" + "               <Nm>MBC</Nm>"
                + "            </SrcPty>" + "            <Rqstr>" + "               <Nm>Call from MBC system</Nm>"
                + "            </Rqstr>" + "         </Envt>" + "         <ReqInf>" + "            <TokenInfo>"
                + TokenInfo + "</TokenInfo>" + "            <TerminalId>11021957</TerminalId>"
                + "            <TraceNumber>" + TransactionWalnetID + "</TraceNumber>" + "         </ReqInf>"
                + "      </DeleteCardTokenReq>" + "   </soapenv:Body>" + "</soapenv:Envelope>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "DeleteCardToken");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    MyResult GetDeleteCardLink(String TransactionWalnetID, String CardInfo, String TokenInfo, String TrxnAmount,
            String Otp, String OtpTranId) {
        String tibcoxml = PostDeleteCardLink(TransactionWalnetID, CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId);
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

    @GetMapping(path = "/verifyotp", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String VerifyOTP(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("Otp") String Otp,
            @RequestParam("RoutingRule") String RoutingRule, @RequestParam("OtpTranId") String OtpTranId) {
        Gson gson = new Gson();
        return gson.toJson(GetVerifyOTP(TransactionWalnetID, "", JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), "",
                Otp, OtpTranId, RoutingRule));
    }

    public String PostVerifyOTP(String TransactionWalnetID, String CardInfo, String TokenInfo, String TrxnAmount,
            String Otp, String OtpTranId, String RoutingRule) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        String postdata = "<VerifyOTPReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
                + "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<ReqGnlInf>" + "		<Id>"
                + uuid.toString() + "</Id>" + "		<TxId>" + TxId + "</TxId>" + "		<CreDtTm>"
                + CreDtTm.format(new Date()) + "</CreDtTm>" + "		<RoutingRule>" + RoutingRule
                + "</RoutingRule>" + "		<Desc/>" + "		<HeaderHashStr/>" + "		<Sgntr>"
                + "			<Sgntr1>" + otherApi(RoutingRule,CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "</ Sgntr1>"
                + "		</Sgntr>" + "	</ReqGnlInf>" + "	<Envt>" + "		<TrgtPty>" + "			<Nm>CARD</Nm>"
                + "		</TrgtPty>" + "		<SrcPty>" + "			<Nm>MBC</Nm>" + "		</SrcPty>" + "		<Rqstr>"
                + "			<Nm>CARD</Nm>" + "		</Rqstr>" + "	</Envt>" + "	<ReqInf>" + "		<TokenInfo>"
                + TokenInfo + "</TokenInfo>" + "		<TerminalId>11021957</TerminalId>" + "		<TraceNumber>"
                + TransactionWalnetID + "</TraceNumber>" + "		<OtpTranId>" + OtpTranId + "</OtpTranId>"
                + "		<OtpNumber>" + Otp + "</OtpNumber>" + "	</ReqInf>" + "</VerifyOTPReq>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "VerifyOTP");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    VERIFYOTP GetVerifyOTP(String TransactionWalnetID, String CardInfo, String TokenInfo, String TrxnAmount, String Otp,
            String OtpTranId, String RoutingRule) {
        String tibcoxml = PostVerifyOTP(TransactionWalnetID, CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId,
                RoutingRule);
        VERIFYOTP m = new VERIFYOTP();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(tibcoxml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrCd") != null) {
                m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrMsg") != null) {
                m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("OtpTranId") != null) {
                m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefTranId") != null) {
                m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    class VERIFYOTP {

        String Status = "";
        String ErrCd = "";
        String ErrMsg = "";
        String OtpTranId = "";
        String RefTranId = "";
        String TokenInfo = "";

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

    @GetMapping(path = "/cardtokentrxntopup", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String CardTokenTrxnTopuP(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("TrxnAmount") String TrxnAmount,
            @RequestParam("Des") String Des) {
        Gson gson = new Gson();
        return gson.toJson(GetCardTokenTrxnTopup(Des, TransactionWalnetID, "",
                JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), TrxnAmount, "", ""));
    }

    public String PostCardTokenTrxnTopup(String Des, String TransactionWalnetID, String CardInfo, String TokenInfo,
            String TrxnAmount, String Otp, String OtpTranId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");

        String postdata = "<CardTokenTrxnTopupReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1 Partner2TCB.xsd\""
                + "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<ReqGnlInf>" + "		<Id>"
                + uuid.toString() + "</Id>" + "		<TxId>" + TxId + "</TxId>" + "		<CreDtTm>"
                + CreDtTm.format(new Date()) + "</CreDtTm>"
                + "		<RoutingRule>CardTokenTrxnTopupRequest</RoutingRule>" + "		<Desc/>"
                + "		<HeaderHashStr/>" + "		<Sgntr>" + "			<Sgntr1>"
                + otherApi("CardTokenTrxnTopupRequest",CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "			</ Sgntr1>" + "		</Sgntr>"
                + "	</ReqGnlInf>" + "	<Envt>" + "		<TrgtPty>" + "			<Nm>CARD</Nm>" + "		</TrgtPty>"
                + "		<SrcPty>" + "			<Nm>MBC</Nm>" + "		</SrcPty>" + "		<Rqstr>"
                + "			<Nm>Call from PARTNER_CODE system</Nm>" + "		</Rqstr>" + "	</Envt>" + "	<ReqInf>"
                + "		<TokenInfo>" + TokenInfo + "</TokenInfo>" + "		<TerminalId>11021957</TerminalId>"
                + "		<TraceNumber>" + TransactionWalnetID + "</TraceNumber>" + "		<TrxnAmount>" + TrxnAmount
                + "</TrxnAmount>" + "		<TrxnCurrency>vnd</TrxnCurrency>" + "		<Description>" + Des
                + "</Description>" + "		<InvoiceNumber>" + TransactionWalnetID + "</InvoiceNumber>"
                + "		<RequestDateTime>" + RequestDt.format(new Date()) + "</RequestDateTime>"
                + "	</ReqInf>" + "</CardTokenTrxnTopupReq> ";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "CardTokenTrxnTopup");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    CARDTOKENTRXN GetCardTokenTrxnTopup(String Des, String transactionWalletId, String CardInfo, String TokenInfo,
            String TrxnAmount, String Otp, String OtpTranId) {
        String tibcoxml = PostCardTokenTrxnTopup(Des, transactionWalletId, CardInfo, TokenInfo, TrxnAmount, Otp,
                OtpTranId);
        CARDTOKENTRXN m = new CARDTOKENTRXN();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(tibcoxml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrCd") != null) {
                m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrMsg") != null) {
                m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("OtpTranId") != null) {
                m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefTranId") != null) {
                m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    class CARDTOKENTRXN {

        String Status = "";
        String ErrCd = "";
        String ErrMsg = "";
        String OtpTranId = "";
        String RefTranId = "";
        String TokenInfo = "";

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

    @GetMapping(path = "/cardtokenTrxnsale", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String CardTokenTrxnSale(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("TrxnAmount") String TrxnAmount,
            @RequestParam("Des") String Des) {
        Gson gson = new Gson();
        return gson.toJson(GetCardTokenTrxnSale(Des, TransactionWalnetID, "",
                JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), TrxnAmount, "", ""));
    }

    public String PostCardTokenTrxnSale(String Description, String TransactionWalnetID, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");

        String postdata = "<CardTokenTrxnSaleReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
                + "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<ReqGnlInf>" + "		<Id>"
                + uuid.toString() + "</Id>" + "		<TxId>" + TxId + "</TxId>" + "		<CreDtTm>"
                + CreDtTm.format(new Date()) + "</CreDtTm>"
                + "		<RoutingRule>CardTokenTrxnSaleRequest</RoutingRule>" + "		<Desc/>"
                + "		<HeaderHashStr/>" + "		<Sgntr>" + "			<Sgntr1>"
                + otherApi("CardTokenTrxnSaleRequest",CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "			</ Sgntr1>" + "		</Sgntr>"
                + "	</ReqGnlInf>" + "	<Envt>" + "		<TrgtPty>" + "			<Nm>CARD</Nm>" + "		</TrgtPty>"
                + "		<SrcPty>" + "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK </Nm>" + "		</SrcPty>"
                + "		<Rqstr>" + "			<Nm>Call from PARTNER_CODE system</Nm>" + "		</Rqstr>" + "	</Envt>"
                + "	<ReqInf>" + "		<TokenInfo>" + TokenInfo + "</TokenInfo>"
                + "		<TerminalId>11021957</TerminalId>" + "		<TraceNumber>" + TransactionWalnetID
                + "</TraceNumber>" + "		<TrxnAmount>" + TrxnAmount + "</TrxnAmount>"
                + "		<TrxnCurrency>vnd</TrxnCurrency>" + "		<Description>" + Description + "</Description>"
                + "		<InvoiceNumber>" + TransactionWalnetID + "</InvoiceNumber>" + "		<RequestDateTime>"
                + RequestDt.format(new Date()) + "</RequestDateTime>" + "	</ReqInf>"
                + "</CardTokenTrxnSaleReq>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "CardTokenTrxnSale");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    CARDTOKENTRXN GetCardTokenTrxnSale(String Description, String transactionWalletId, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        String tibcoxml = PostCardTokenTrxnSale(Description, transactionWalletId, CardInfo, TokenInfo, TrxnAmount, Otp,
                OtpTranId);
        CARDTOKENTRXN m = new CARDTOKENTRXN();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(tibcoxml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrCd") != null) {
                m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrMsg") != null) {
                m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("OtpTranId") != null) {
                m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefTranId") != null) {
                m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    @GetMapping(path = "/cardtokentrxnwithdraw", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String CardTokenTrxnWithdraw(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("TrxnAmount") String TrxnAmount,
            @RequestParam("Des") String Des) {
        Gson gson = new Gson();
        return gson.toJson(GetCardTokenTrxnWithdraw(Des, TransactionWalnetID, "",
                JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), TrxnAmount, "", ""));
    }

    public String PostCardTokenTrxnWithdraw(String Description, String TransactionWalnetID, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
        String postdata = "<CardTokenTrxnWithdrawReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection /v1\""
                + "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<ReqGnlInf>" + "		<Id>"
                + uuid.toString() + "</Id>" + "		<TxId>" + TxId + "</TxId>" + "		<CreDtTm>"
                + CreDtTm.format(new Date()) + "</CreDtTm>"
                + "		<RoutingRule>CardTokenTrxnSaleRequest</RoutingRule>" + "		<Desc/>"
                + "		<HeaderHashStr/>" + "		<Sgntr>" + "			<Sgntr1>"
                + otherApi("CardTokenTrxnSaleRequest",CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "			</ Sgntr1>" + "		</Sgntr>"
                + "	</ReqGnlInf>" + "	<Envt>" + "		<TrgtPty>" + "			<Nm>TCB</Nm>" + "		</TrgtPty>"
                + "		<SrcPty>" + "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK</Nm>" + "		</SrcPty>"
                + "		<Rqstr>" + "			<Nm>Call from PARTNER_CODE system</Nm>" + "		</Rqstr>" + "	</Envt>"
                + "	<ReqInf>" + "		<TokenInfo>" + TokenInfo + "</TokenInfo>"
                + "		<TerminalId>11021957</TerminalId>" + "		<TraceNumber>" + TransactionWalnetID
                + "</TraceNumber>" + "		<TrxnAmount>" + TrxnAmount + "</TrxnAmount>"
                + "		<TrxnCurrency>vnd</TrxnCurrency>" + "		<Description>" + Description + "</Description>"
                + "		<InvoiceNumber>" + TransactionWalnetID + "</InvoiceNumber>"
                + "		<RequestDateTime>yyyy-MM-dd HH:mi:ss</RequestDateTime>" + "	</ReqInf>"
                + "</CardTokenTrxnWithdrawReq> ";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "CardTokenTrxnWithdraw");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    CARDTOKENTRXN GetCardTokenTrxnWithdraw(String Description, String transactionWalletId, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        String tibcoxml = PostCardTokenTrxnWithdraw(Description, transactionWalletId, CardInfo, TokenInfo, TrxnAmount,
                Otp, OtpTranId);
        CARDTOKENTRXN m = new CARDTOKENTRXN();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(tibcoxml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrCd") != null) {
                m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrMsg") != null) {
                m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("OtpTranId") != null) {
                m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefTranId") != null) {
                m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    @GetMapping(path = "/cardtokentrxnrefund", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String CardTokenTrxnRefund(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("TrxnAmount") String TrxnAmount,
            @RequestParam("Des") String Des) {
        Gson gson = new Gson();
        return gson.toJson(GetCardTokenTrxnRefund(Des, TransactionWalnetID, "",
                JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), TrxnAmount, "", ""));
    }

    public String PostCardTokenTrxnRefund(String Description, String TransactionWalnetID, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
        String postdata = "<CardTokenTrxnRefundReq xsi:schemaLocation=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\""
                + "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "	<ReqGnlInf>" + "		<Id>"
                + uuid.toString() + "</Id>" + "		<TxId>" + TxId + "</TxId>" + "		<CreDtTm>"
                + CreDtTm.format(new Date()) + "</CreDtTm>"
                + "		<RoutingRule>CardTokenTrxnRefundRequest</RoutingRule>" + "		<Desc/>"
                + "		<HeaderHashStr/>" + "		<Sgntr>" + "			<Sgntr1>"
                + otherApi("CardTokenTrxnRefundRequest",CardInfo, TokenInfo, TrxnAmount, Otp, OtpTranId) + "			</ Sgntr1>" + "		</Sgntr>"
                + "	</ReqGnlInf>" + "	<Envt>" + "		<TrgtPty>" + "			<Nm>CARD</Nm>" + "		</TrgtPty>"
                + "		<SrcPty>" + "			<Nm> PARTNER-ID-ISSUED-BY-TECHCOMBANK</Nm>" + "		</SrcPty>"
                + "		<Rqstr>" + "			<Nm>Call from PARTNER system</Nm>" + "		</Rqstr>" + "	</Envt>"
                + "	<ReqInf>" + "		<TokenInfo>" + TokenInfo + "</TokenInfo>"
                + "		<TerminalId>11021957</TerminalId>" + "		<RefundType>MR</RefundType>"
                + "		<TraceNumber>" + TransactionWalnetID + "</TraceNumber>" + "		<TrxnAmount>" + TrxnAmount
                + "</TrxnAmount>" + "		<TrxnCurrency>vnd</TrxnCurrency>" + "		<Description>" + Description
                + "</Description>" + "		<OrigTranId>123456</OrigTranId>" + "		<RequestDateTime>"
                + RequestDt.format(new Date()) + "</RequestDateTime>" + "	</ReqInf>"
                + "</CardTokenTrxnRefundReq>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/CardToken/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "CardTokenTrxnRefund");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    CARDTOKENTRXN GetCardTokenTrxnRefund(String Description, String transactionWalletId, String CardInfo,
            String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
        String tibcoxml = PostCardTokenTrxnRefund(Description, transactionWalletId, CardInfo, TokenInfo, TrxnAmount,
                Otp, OtpTranId);
        CARDTOKENTRXN m = new CARDTOKENTRXN();
        DocumentBuilder db;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(tibcoxml));
            Document doc = db.parse(is);
            if (doc.getElementsByTagName("Status") != null) {
                m.setStatus(doc.getElementsByTagName("Status").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrCd") != null) {
                m.setErrCd(doc.getElementsByTagName("ErrCd").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("ErrMsg") != null) {
                m.setErrMsg(doc.getElementsByTagName("ErrMsg").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("OtpTranId") != null) {
                m.setOtpTranId(doc.getElementsByTagName("OtpTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("RefTranId") != null) {
                m.setRefTranId(doc.getElementsByTagName("RefTranId").item(0).getTextContent());
            }
            if (doc.getElementsByTagName("TokenInfo") != null) {
                m.setTokenInfo(doc.getElementsByTagName("TokenInfo").item(0).getTextContent());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    /////////////////////////////
    @GetMapping(path = "/fundtransfer", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String FundTransfer(@RequestParam("Description") String Description,
            @RequestParam("FrAcct_AcctId") String frAcct_AcctId,
            @RequestParam("FrAcct_AcctTitl") String frAcct_AcctTitl,
            @RequestParam("ToAcct_AcctId") String toAcct_AcctId,
            @RequestParam("ToAcct_AcctTitl") String toAcct_AcctTitl, @RequestParam("ToAcct_Citad") String toAcct_Citad,
            @RequestParam("ToAcct_CustName") String toAcct_CustName, @RequestParam("TxAmt") String TxAmt,
            @RequestParam("PaymentType") String payment_type, @RequestParam("customerID") String customerID,
            @RequestParam("frAccId") String frAccId, @RequestParam("frAccTitl") String frAccTitl,
            @RequestParam("toAccId") String toAccId, @RequestParam("toAccTitl") String toAccTitl,
            @RequestParam("toAccName") String toAccName
    ) {
        Gson gson = new Gson();
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");

        return gson.toJson(PostFundTransfer(Description, uuid, TxId, payment_type, CreDtTm.format(new Date()), customerID, toAcct_Citad,
                TxAmt, frAccId, frAccTitl, toAccId, toAccTitl, toAccName));

    }

    public String encriptFundTransfer(String Description, String AcctId, String AcctTitl, String TxAmt) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
        String strRequestDt = RequestDt.format(new Date());
        String _pathPublic = "/mobivi.cer";
        String signature1 = "";
        String signature2 = "";
        String customer_id = "";
        String payment_type = "";
        String ReqInfEncrypted = JavaSignSHA256_V2
                .signData_SHA256("FundTransfer" + uuid.toString() + AcctId + AcctTitl + strRequestDt + TxAmt);

        return "";// PostFundTransfer(Description, uuid.toString(), TxId, payment_type,
        // CreDtTm.format(new Date()),
        // ReqInfEncrypted, signature1, signature2, customer_id);
    }

    public String PostFundTransfer(String Description, UUID id, String TxId, String payment_type, String CreDtTm,
            String customerID, String citad, String txAmt, String frAccId, String frAccTitl, String toAccId,
            String toAccTitl, String toAccName) {
        String ReqInfEncrypted, signature1, signature2;
        ReqInfEncrypted = signature1 = signature2 = "";
        signature1 = JavaSignSHA256_V2
                .signData_SHA256("FundTransfer" + id.toString() + toAccId + toAccTitl + CreDtTm + txAmt);
        signature2 = signature1;

        String TxTp = "";
        if (citad == "") {
            citad = "01310001"; // "Internal"
        }
        if (citad == "01310001") {
            TxTp = "DOMESTIC"; // ATM
        }
        String ReqInf = "<ReqInf xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">" + "<TxTp>" + TxTp
                + "</TxTp>" + "<TxDt>" + CreDtTm + "</TxDt>" + "<TxAmt>" + txAmt + "</TxAmt>" + "<Desc>" + Description
                + "</Desc>" + "<FrAcct>" + "<AcctId>" + frAccId + "</AcctId>" + "<AcctTitl>" + frAccTitl + "</AcctTitl>"
                + "</FrAcct>" + "<ToAcct>" + "<AcctId>" + toAccId + "</AcctId>" + "<AcctTitl>" + toAccTitl
                + "</AcctTitl>" + "<FIData>" + "<CITAD>" + citad + "</CITAD>" + "</FIData>" + "<PtyData>" + "<Tp/>"
                + "<Val>" + toAccTitl + "</Val>" + "<Desc/>" + "</PtyData>" + "</ToAcct>" + "</ReqInf>";

        ReqInfEncrypted = JavaSecutityEncrypt_V2.encrypt_AES256(ReqInf, "tcb_mahoa.cer");
        String postdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "   <soapenv:Header/>" + "   <soapenv:Body>"
                + "      <v1:XferReq xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "         <v1:ReqGnlInf>" + "            <v1:Id>" + id + "</v1:Id>" + "            <v1:TxId>" + TxId
                + "</v1:TxId>" + "            <v1:CreDtTm>" + CreDtTm + "</v1:CreDtTm>" + "            <v1:PmtTp>"
                + payment_type + "</v1:PmtTp>" + "            <v1:Desc>" + Description + "</v1:Desc>"
                + "            <v1:Sgntr>" + "               <v1:Sgntr1 user=\"" + customerID + "1\">" + signature1
                + "</v1:Sgntr1>" + "               <v1:Sgntr2 user=\"" + customerID + "2\">" + signature2
                + "</v1:Sgntr2>" + "            </v1:Sgntr>" + "         </v1:ReqGnlInf>" + "         <v1:Envt>"
                + "            <v1:SrcPty>" + "               <v1:Nm>" + customerID + "</v1:Nm>"
                + "            </v1:SrcPty>" + "            <v1:TrgtPty>" + "               <v1:Nm>TCB</v1:Nm>"
                + "            </v1:TrgtPty>" + "            <v1:Rqstr>" + "               <v1:Nm>" + customerID
                + "</v1:Nm>" + "            </v1:Rqstr>" + "            <v1:TrgtPty/>" + "         </v1:Envt>"
                + "         <v1:ReqInf>" + ReqInfEncrypted + "</v1:ReqInf>" + "      </v1:XferReq>"
                + "   </soapenv:Body>" + "</soapenv:Envelope>";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/PartnerToTCB/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "FundTransfer");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    MyResult GetFundTransfer(String Description, String AcctId, String AcctTitl, String TxAmt) {
        String tibcoxml = encriptFundTransfer(Description, AcctId, AcctTitl, TxAmt);
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

    @GetMapping(path = "/accountinfo", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public String AccountInfo(@RequestParam("TransactionWalnetID") String TransactionWalnetID,
            @RequestParam("TokenInfo") String TokenInfo, @RequestParam("TrxnAmount") String TrxnAmount,
            @RequestParam("Des") String Des) {
        Gson gson = new Gson();
        return gson.toJson(GetCardTokenTrxnRefund(Des, TransactionWalnetID, "",
                JavaSignSHA256.encryptWithPublicKeyTCB(TokenInfo), TrxnAmount, "", ""));
    }

    public String encriptAccountInfo(String SvcName, String ClientTerminalSeqNum, String Bindata, String Token,
            String Name, String ORG, String SubjectRole, String MsgGroupReference, String AcctId) {
        UUID uuid = UUID.randomUUID();
        String TxId = "MBC" + uuid.toString();
        DateFormat CreDtTm = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat RequestDt = new SimpleDateFormat("yyyy-MM-dd HH:mi:ss");
        String strRequestDt = RequestDt.format(new Date());
        String _pathPublic = "/mobivi.cer";
        String ReqInf = JavaSignSHA256_V2.signData_SHA256(
                "AcctInq" + uuid.toString() + SvcName + Name + SubjectRole + MsgGroupReference + AcctId);
        return PostAccountInfo(SvcName, uuid.toString(), TxId, ClientTerminalSeqNum, Bindata, Token, Name, ORG,
                SubjectRole, CreDtTm.format(new Date()), MsgGroupReference);
    }

    public String PostAccountInfo(String SvcName, String id, String TxId, String ClientTerminalSeqNum, String Bindata,
            String Token, String Name, String ORG, String SubjectRole, String CreDtTm, String MsgGroupReference) {

        String postdata = "<soapenv:Envelope" + "	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "	xmlns=\"http://www.techcombank.com.vn/account/v1\">" + "	<soapenv:Header/>" + "	<soapenv:Body>"
                + "		<AcctInqRq>" + "			<RqUID>" + id + "</RqUID>" + "			<MsgRqHdr>"
                + "				<SvcIdent>" + "					<SvcName>" + SvcName + "</SvcName>"
                + "				</SvcIdent>" + "				<CredentialsRqHdr>" + "					<SubjectRole>"
                + SubjectRole + "</SubjectRole>" + "					<OvrdExceptionCode>ACCT</OvrdExceptionCode>"
                + "				</CredentialsRqHdr>" + "				<ContextRqHdr>"
                + "					<MsgGroupReference>" + MsgGroupReference + "</MsgGroupReference>"
                + "					<ClientTerminalSeqNum>" + ClientTerminalSeqNum + "</ClientTerminalSeqNum>"
                + "					<ClientDt>" + CreDtTm + "</ClientDt>" + "					<ClientApp>"
                + "						<Org>" + ORG + "</Org>" + "						<Name>" + Name + "</Name>"
                + "						<Version>1000</Version>" + "					</ClientApp>"
                + "				</ContextRqHdr>" + "			</MsgRqHdr>" + "			<RecCtrlIn>"
                + "				<Cursor>" + "					<BinLength>1</BinLength>"
                + "					<BinData>" + Bindata + "</BinData>" + "				</Cursor>"
                + "				<Token>" + Token + "</Token>" + "			</RecCtrlIn>" + "		</AcctInqRq>"
                + "	</soapenv:Body>" + "</soapenv:Envelope> ";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/PartnerToTCB/v1");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "AccountInfo");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    MyResult GetAccountInfo(String SvcName, String ClientTerminalSeqNum, String Bindata, String Token, String Name,
            String ORG, String SubjectRole, String MsgGroupReference, String AcctId) {
        String tibcoxml = encriptAccountInfo(SvcName, ClientTerminalSeqNum, Bindata, Token, Name, ORG, SubjectRole,
                MsgGroupReference, AcctId);
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

    public static class BankAccountInfo {

        private String CITAD = "";
        private String BkCd = "";
        private String BkNm = "";
        private String BrnchNm = "";
        private String StatPrvc = "";
        private String ActvSts = "";
        private String IsCentralizedBank = "";

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
    @GetMapping(path = "/inqlistbankinfo", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
        String postdata = "<soapenv:Envelope" + "	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "	xmlns:v1=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "	< soapenv:Header />"
                + "	<soapenv:Body>"
                + "		<InqBnkInfReq"
                + "			xmlns=\"http://www.techcombank.com.vn/services/bank/collection/v1\">"
                + "			<ReqGnlInf>"
                + "				<Id>" + uuid.toString() + "</Id>"
                + "				<TxId>"
                + TxId + "</TxId>"
                + "				<CreDtTm>" + CreDtTm.format(new Date())
                + "</CreDtTm>"
                + "				<Desc>Inquiry List Bank Info</Desc>"
                + "				<Sgntr/>"
                + "			</ReqGnlInf>"
                + "			<Envt>" + "				<SrcPty>"
                + "					<Nm>"
                + CustomerID + "</Nm>" + "				</SrcPty>"
                + "				<Rqstr>"
                + "					<Nm>Call from Customer</Nm>"
                + "				</Rqstr>" + "			</Envt>"
                + "			<ReqInf>"
                + "				<BkId>ALL</BkId>"
                + "			</ReqInf>"
                + "		</InqBnkInfReq>"
                + "	</soapenv:Body>"
                + "</soapenv:Envelope> ";
        StringEntity xmlEntity;
        HttpResponse response;
        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        HttpPost post = new HttpPost("https://api.techcombank.com.vn:446/services/bank/collection/PartnerToTCB/v1");

        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("SOAPAction", "CardTokenTrxnRefund");
        try {
            xmlEntity = new StringEntity(postdata);
            System.out.println(postdata);
            post.setEntity(xmlEntity);
            response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            GlobalVariables.logger.info(result);post.releaseConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    BankAccountInfo GetInqListBankInfo(String CustomerID) {
        String tibcoxml = PostInqListBankInfo(CustomerID);
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
            m.setCITAD(CITAD.getTextContent());
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

    public static String CreateActiveUrl(String TxId, String CustomerName, String MobileNumber) {

        String RoutingRule = "CreateActiveUrl";
        String TrgtPty = "TCB";
        String SrcPty = "MBC";
        String CustomerID = "+84" + MobileNumber.substring(1);
        return JavaSignSHA256.encryptWithPublicKeyTCB(
                TxId + RoutingRule + TrgtPty + SrcPty + CustomerID + CustomerName + MobileNumber);
    }

    public static String otherApi(String RoutingRule,String CardInfo, String TokenInfo, String TrxnAmount, String Otp, String OtpTranId) {
         String TrgtPty = "TCB";
        String SrcPty = "MBC";
        String TerminalId = "11021957";
        return JavaSignSHA256.encryptWithPublicKeyTCB(
                RoutingRule + TrgtPty + SrcPty + TerminalId + CardInfo + TokenInfo + TrxnAmount + Otp + OtpTranId);
    }

}
