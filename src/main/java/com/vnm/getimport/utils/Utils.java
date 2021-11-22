package com.vnm.getimport.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.vnm.erp.cover.GlobalVariables;
 
public class Utils
{ 
  
  public String GetConfig(String key)
  {
    String result = "";
    if (GlobalVariables.CONFIG_FILE.containsKey(key)) {
      result = GlobalVariables.CONFIG_FILE.getProperty(key);
    } else {
      GlobalVariables.logger.error("Get Config file fail with key : " + key);
    }
    return result;
  }
  
  public String getTextFile(String filename)
    throws IOException
  {
    BufferedReader br = null;
    
    StringBuilder sb = new StringBuilder();
    try
    {
      br = new BufferedReader(new FileReader(filename));
      String line = br.readLine();
      while (line != null)
      {
        sb.append(line);
       // sb.append(System.lineSeparator());
        line = br.readLine();
      }
      br.close();
    }
    catch (Exception ex)
    {
      if (br != null) {
        br.close();
      }
    }
    return sb.toString();
  }
  
  public boolean isInteger(String str)
  {
    try
    {
      Integer.parseInt(str);
      return true;
    }
    catch (NumberFormatException nfe) {}
    return false;
  }
  
  public boolean isNumeric(String str)
  {
    try
    {
      Double.parseDouble(str);
      return true;
    }
    catch (NumberFormatException nfe) {}
    return false;
  }
  
  
  public String getConfigProperties(String filename, String key)
  {
    String configFile = GlobalVariables.current_directory + File.separator + filename;
    InputStream input = null;
    
    String result = "";
    try
    {
      File f = new File(configFile);
      if (!f.exists())
      {
        GlobalVariables.logger.error("File config " + configFile + " does not exist !");
        return "";
      }
      input = new FileInputStream(configFile);
      Properties propFile = new Properties();
      propFile.load(input);
      if (propFile.containsKey(key))
      {
        result = propFile.getProperty(key);
        
        propFile = null;
        input.close();
        return result;
      }
      propFile = null;
      input.close();
      
      return result;
    }
    catch (Exception ex)
    {
      GlobalVariables.logger.error(ex);
    }
    return result;
  }
  
  public void setConfigProperties(String filename, String key, String value)
    throws IOException
  {
    String configFile = GlobalVariables.current_directory + File.separator + filename;
    
    Properties propFile = new Properties();
    
    File f = new File(configFile);
    if (f.exists())
    {
      InputStream input = new FileInputStream(configFile);
      propFile.load(input);
    }
    FileOutputStream out = new FileOutputStream(configFile);
    propFile.setProperty(key, value);
    propFile.store(out, "");
    
    out.close();
  }
  
  public void removeConfigProperties(String filename, String key)
    throws IOException
  {
    String configFile = GlobalVariables.current_directory + File.separator + filename;
    
    File f = new File(configFile);
    if (f.exists())
    {
      Properties propFile = new Properties();
      InputStream input = new FileInputStream(configFile);
      propFile.load(input);
      FileOutputStream out = new FileOutputStream(configFile);
      propFile.remove(key);
      propFile.store(out, "");
      out.close();
    }
  }
  
  public String getErrorMessage(int error_code)
  {
    String message = "Ma loi chua duoc dinh nghia";
    if (GlobalVariables.HASH_ERROR_MESSAGE.containsKey("" + error_code)) {
      message = (String)GlobalVariables.HASH_ERROR_MESSAGE.get("" + error_code);
    }
    return message;
  }
  
  public boolean isMobileNumber(String msisdn)
  {
    if (msisdn == null) {
      return false;
    }
    if (msisdn.length() < 9) {
      return false;
    }
    if (msisdn.length() > 16) {
      return false;
    }
    return true;
  }
  
 
}
