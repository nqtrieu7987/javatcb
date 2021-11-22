package com.vnm.getimport.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ExcelUtils {

	protected static Logger logger = Logger.getLogger(ExcelUtils.class
			.getName());

	/**
	 * @param String
	 *            [] headerRow Các cột header của CSV file
	 * 
	 * @param String
	 *            [] headerRule header qui định
	 * 
	 */
	public static boolean verifyCSVFormat(String[] headerRow,
			String[] headerRule) {
		boolean isValid = true;
		List<String> headerCols = Arrays.asList(headerRow);
		for (String header : headerRule) {
			if (!headerCols.contains(header.toUpperCase())) {
				logger.error("Header field [" + header + "] is required.");
				isValid = false;
				break;
			}
		}

		return isValid;
	}
	
	
	

}
