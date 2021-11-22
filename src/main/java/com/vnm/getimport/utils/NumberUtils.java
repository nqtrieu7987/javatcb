package com.vnm.getimport.utils;

import org.apache.log4j.Logger;

public class NumberUtils {

	protected static Logger logger = Logger.getLogger(NumberUtils.class
			.getName());

	public static Long StringToLong(String str) {
		Long number = null;
		try {
			if (str != null)
				number = Long.parseLong(str);
		} catch (Exception e) {
		}
		return number;
	}

	public static Integer StringToInteger(String str) {
		Integer number = null;
		try {
			if (str != null)
				number = Integer.parseInt(str);
		} catch (Exception e) {
		}
		return number;
	}

	public static Double StringToDouble(String str) throws Exception {
		Double number = null;
		try {
			if (str != null)
				number = Double.parseDouble(str);
		} catch (Exception e) {
		}
		return number;
	}

	public static Long isNumber(String str) {
		Long number = null;
		try {
			str = str.replace(".", "");
			str = str.replace(",", "");
			number = Long.parseLong(str);
		} catch (Exception e) {
		}
		return number;
	}

	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

}
