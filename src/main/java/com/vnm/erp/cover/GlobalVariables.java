package com.vnm.erp.cover;

import io.jsonwebtoken.Jwts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.albertschmitt.crypto.common.RSAPrivateKey;
import org.albertschmitt.crypto.common.RSAPublicKey;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.vnm.getimport.utils.Utils;

 
public final class GlobalVariables {
	public static int listener_port = 8010;
	public static String current_directory;
	public static Properties CONFIG_FILE;
	public static final Logger logger = Logger.getLogger("com.vnm.erp");
	public static String CRLF = "\r\n";
	public static DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	public static int OTP_TYPE = 0;
	public static int OTP_LENGTH = 0;
	public static int OTP_LIVE_TIME = 0;
	public static String config_file = "otp.properties"; 
	public static Hashtable<String, String> HASH_ERROR_MESSAGE; 
	private static String DEFAULT_LOG4J_PATH = "stress/client-log4j.properties";
	// private static Connection ds;  
  	static RSAPrivateKey privateKey ;
	static RSAPublicKey publicKey ;
	static byte[] keyjwt = new byte[64];
	public static ArrayList<String> fileAlreadyRead = new ArrayList<String>(); 
 	  public static String Username="RestAPI";
	  public  static  String Password="Vnm!23Upcc"; 
	private static String  dburl;
	private static String  dbuser;
	private static String  dbpass;
	private static String  dburlossdb;
	private static String  dbuserossdb;
	private static String  dbpassossdb;
	public static String aesKey;
	public static String keyredis;
	public static AtomicInteger at = new AtomicInteger(0);
	public static AtomicBoolean boolean1 = new AtomicBoolean(true);
	public static  BlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);
	public static ResourcePoolClient pool;
	public static Connection getConnection()
	{
		try {
			System.out.println(dburl);
			return DriverManager.getConnection(dburl,
					dbuser, dbpass);
		} catch ( Exception e) { 
			e.printStackTrace();
		}
		return null;
	}
	public static Connection getConnectionOSSDB()
	{
		try { 
			return DriverManager.getConnection(dburlossdb,dbuserossdb,dbpassossdb);//"jdbc:oracle:thin:@10.8.9.70:1522:TEST","IT_TRIEUNQ", "abcd1234");
		} catch ( Exception e) { 
			System.out.println("loioioioioioioioip");
			e.printStackTrace();
		}
		return null;
	}
	 
	public static void LoadAllParameter() throws IOException {
		LoadConfigFile();
	}
	public static String genTransaction(String UserID)
	{ 	 String UseridLast=UserID;
		if(UserID.length()==1)
		{
			UseridLast="00"+UserID;
		}
		else
		{
			if(UserID.length()==2)
			{
				UseridLast="0"+UserID;
			}
		}
		return ("E"+UseridLast+System.nanoTime());
	}
	
	  public static String createKeyhash(String password) {
	        try {
	            SecretKey secretKey = new SecretKeySpec(  java.util.Base64.getDecoder().decode(aesKey), KEYHASH_ENCRYPTION_ALGORITHM);
	            Cipher cipher = Cipher.getInstance(KEYHASH_ENCRYPTION_ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
	            return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return "";
	    }
	    public static final String KEYHASH_ENCRYPTION_ALGORITHM = "AES";
	    //CIMB
	    public  static String extractPassword(String keyHash) {
	        try {
	            SecretKey secretKey = new SecretKeySpec(java.util.Base64.getDecoder().decode(aesKey), KEYHASH_ENCRYPTION_ALGORITHM);
	            Cipher cipher = Cipher.getInstance(KEYHASH_ENCRYPTION_ALGORITHM);
	            cipher.init(Cipher.DECRYPT_MODE, secretKey);
	            byte[] decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(keyHash));
	            return new String(decryptedBytes);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return "";
	    }
	    
 
	
	 public static String gennewkey(String UserID)
	  {
		
		 String UseridLast=UserID;
			if(UserID.length()==1)
			{
				UseridLast="00"+UserID;
			}
			else
			{
				if(UserID.length()==2)
				{
					UseridLast="0"+UserID;
				}
			}
			return "AT-"+UseridLast+((System.currentTimeMillis()+"").substring(3));
		
	  }
	 public static String genmd5(String password)
	  {
		  
		  MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		
	      md.update(password.getBytes());

	      byte byteData[] = md.digest();

	      //convert the byte to hex format method 1
	      StringBuffer sb = new StringBuffer();
	      for (int i = 0; i < byteData.length; i++) {
	       sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	      }
 
 
	      StringBuffer hexString = new StringBuffer();
	  	for (int i=0;i<byteData.length;i++) {
	  		String hex=Integer.toHexString(0xff & byteData[i]);
	 	     	if(hex.length()==1) hexString.append('0');
	 	     	hexString.append(hex);
	  	}
	  	return hexString.toString();
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace();
		}
		return "";
	  }
	  
	 public static String ParseSession(String token) {
			try {

				String unpack = Jwts.parser().setSigningKey(keyjwt)
						.parseClaimsJws(token).getBody().getSubject();

				 return unpack;
				 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return "";
		} 
	  
    public static String encryptrsa(String msg)
    {  
    	return msg; 
    
    }
   
    public static String deencryptrsa(String msg)
    {  
    	return msg;
//		try {
//		    if (privateKey==null)
//		    {  
//		    	String	publicKeyfile	= "./private_key.pem";
//		    	privateKey = instance.readPrivateKey(publicKeyfile);
//		    } 
//	    	return new String( instance.decode(Base64.decode(msg), privateKey));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	   return "";
    
    }
	
    public static void LoadConfigFile() {  
		 
		PropertyConfigurator.configure(DEFAULT_LOG4J_PATH);
		current_directory = System.getProperty("user.dir");
		String configFile = current_directory + File.separator + config_file;
		Utils util = new Utils();
		InputStream input = null; 
		new SecureRandom().nextBytes(keyjwt); 
		 
		System.out.print("start");
		try {
			File f = new File(configFile);
			if (!f.exists()) {
				logger.error("File config " + configFile + " does not exist !");
				return;
			}
			input = new FileInputStream(configFile);
			CONFIG_FILE = new Properties();
			CONFIG_FILE.load(input);
 			util.setConfigProperties(config_file, "jwtkey", keyjwt.toString());
			dburl =util.GetConfig("DBURL");
			dbpass =util.GetConfig("DBPASS");
			dbuser =util.GetConfig("DBUSER");  
			dburlossdb =util.GetConfig("DBURLOSSDB");
			dbpassossdb =util.GetConfig("DBPASSOSSDB");
			dbuserossdb =util.GetConfig("DBUSEROSSDB");  
			aesKey= util.GetConfig("keyaes"); 
			try {

				listener_port = Integer.parseInt(util
						.GetConfig("otp_listener_port"));
			} catch (Exception ex) {
			}
			logger.info("listener_port = " + listener_port);
			try {
				OTP_TYPE = Integer.parseInt(util.GetConfig("otp_type"));
			} catch (Exception ex) {
			}
			if (OTP_TYPE > 9) {
				OTP_TYPE = 0;
			}
			logger.info("OTP_TYPE = " + OTP_TYPE);
			try {
				OTP_LENGTH = Integer.parseInt(util.GetConfig("otp_length"));
			} catch (Exception ex) {
			}
			if (OTP_LENGTH > 16) {
				OTP_LENGTH = 16;
			}
			logger.info("OTP_LENGTH = " + OTP_LENGTH);
			try {
				OTP_LIVE_TIME = Integer.parseInt(util
						.GetConfig("otp_live_time"));
			} catch (Exception ex) {
			}
			logger.info("OTP_LIVE_TIME = " + OTP_LIVE_TIME);
			System.out.print("end");

		} catch (Exception ex) {
		
			logger.error(ex);
		}    
	}
	 
	
	

}
