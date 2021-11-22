package com.vnm.getimport.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

 
public class DateUtils {

	protected static Logger logger = Logger
			.getLogger(DateUtils.class.getName());

	

	public static Date parseLocalDate(String sDate) {
		if (null == sDate)
			return null;
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
		try {
			result = df.parse(sDate);
		} catch (ParseException e) {
			logger.error("Method parseLocalDate Error:" + e.getMessage());
		}
		return result;
	}

	public static String parseParamaterDate(Date date) {
		if (null == date)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(date);
	}

	public static Date parseParamaterDate(String sDate) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			result = df.parse(sDate);
		} catch (ParseException e) {
			logger.error("Method ParseLocalMonth Error:" + e.getMessage());
		}
		return result;
	}

	public static Date parseStartDate(String sDate) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			result = df.parse(sDate + " 00:00:00");
		} catch (ParseException e) {
			logger.error("Method parseStartDate Error:" + e.getMessage());
		}
		return result;
	}

	public static Date parseMidDate(String sDate) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			result = df.parse(sDate + " 12:00:00");
		} catch (ParseException e) {
			logger.error("Method parseMidDate Error:" + e.getMessage());
		}
		return result;
	}

	public static Date parseEndDate(String sDate) {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			result = df.parse(sDate + " 23:59:59");
		} catch (ParseException e) {
			logger.error("Method parseEndDate Error:" + e.getMessage());
		}
		return result;
	}


	public static Integer compareDate(Date date1, Date date2) {
		if (null != date2 && null != date1) {
			return date1.compareTo(date2);
		}
		return null;
	}

	public static Date parseDDEEEYYYY(String sDate) throws ParseException {
		Date result = null;
		SimpleDateFormat df = new SimpleDateFormat("dd-EEE-yyyy");
		result = df.parse(sDate);
		return result;
	}

	public static long subTractDate(Calendar date1, Calendar date2) {
		if (null != date2 && null != date1) {
			date1.set(Calendar.AM_PM, Calendar.AM);
			date1.set(Calendar.HOUR, 0);
			date1.set(Calendar.MINUTE, 0);
			date1.set(Calendar.SECOND, 0);
			date1.set(Calendar.MILLISECOND, 0);
			date2.set(Calendar.AM_PM, Calendar.AM);
			date2.set(Calendar.HOUR, 0);
			date2.set(Calendar.MINUTE, 0);
			date2.set(Calendar.SECOND, 0);
			date2.set(Calendar.MILLISECOND, 0);
			return (date1.getTimeInMillis() - date2.getTimeInMillis()) / 3600000 / 24;
		}
		return 0;
	}
	 
	public static String parseDateToString(Date date, String type)
			throws Exception {
		if (null == date)
			return "";
		SimpleDateFormat df = new SimpleDateFormat(type);
		return df.format(date);
	}
	
	public static String parseFullTime(Date date) {
		if (null == date)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:MM:ss");
		return df.format(date);
	}
}
