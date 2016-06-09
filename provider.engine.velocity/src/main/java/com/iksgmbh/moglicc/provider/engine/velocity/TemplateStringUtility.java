/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.utils.FileUtil;

public class TemplateStringUtility {

	/**
	 * Adds a new String element to a list of strings.
	 * @param list
	 * @param newElement
	 */
	public static void addToList(final List<String> list, 
			                     final String newElement) 
	{
		list.add(newElement);
	}

	/**
	 * Checks a String to contain a substring.
	 * @param text
	 * @param searchString
	 * @return true if searchString is contained at least once in text 
	 */
	public static boolean contains(String text, String searchString) 
	{
		if (searchString == null) return false;
		
		return text.contains(searchString);
	}
	
	/**
	 * Checks all Strings in a list to contain a substring.
	 * @param text
	 * @param searchString
	 * @return true if searchString is contained at least once in at least one String element 
	 */
	public static boolean contains(List<String> texts, String searchString) 
	{
		if (texts == null) return false;
		
		for (String text : texts) 
		{
			if (contains(text, searchString))
			{
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Checks a String to be not null.
	 * @param string
	 * @return true if not null
	 */
	public boolean isStringValueSet(String string) {
		return string != null;
	}

	/**
	 * Checks a String to have the value "null" or not.
	 * @param s
	 * @return true if equals to "null"
	 */
	public boolean isNullString(String s) {
		return s.equals("null");
	}

	/**
	 * Converts first letter of String to upper case.
	 * @param string
	 * @return e.g. "Upper" for "upper"
	 */
	public static String firstToUpperCase(String string) {
		if (string == null) {
			return null;
		}
		if ("".equals(string)) {
			return "";
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * Converts first letter of String to lower case.
	 * @param string
	 * @return e.g. "Lower" for "lower"
	 */
	public static String firstToLowerCase(String string) {
		if (string == null) {
			return null;
		}
		if ("".equals(string)) {
			return "";
		}
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	/**
	 * Cuts a trailing substring from a String.
	 * @param string
	 * @param suffix
	 * @return string without suffix
	 */
	public static String cutSuffix(String string, String suffix) {
		return StringUtils.removeEnd(string, suffix);
	}

	/**
	 * Cuts a leading substring from a String.
	 * @param string
	 * @param num number of chars to cut
	 * @return string cut in the beginning
	 */
	public static String cutLeadingChars(String string, int num) {
		return string.substring(num);
	}

	/**
	 * Concatenates two Strings.
	 * @param s1
	 * @param s2
	 * @return s1 + s2
	 */
	public static String addStrings(String s1, String s2) {
		return s1 + s2;
	}

	/**
	 * Checks that a list of Strings has no element.
	 * @param list
	 * @return true if without entry
	 */
	public static boolean isListEmpty(final List<String> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return list.isEmpty();
	}

	/**
	 * Concatenates all Strings of a list.
	 * @param list of Strings
	 * @return contcatenation of all string elements
	 */
	public static String toCommaSeparatedString(final List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size()-1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Creates an array of Strings from an single String using commas.
	 * @param string containing commas
	 * @return String array
	 */
	public static String[] commaSeparatedStringToStringArray(final String s) {
		final String[] result = s.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim(); 
		}
		return result;
	}
	
	/**
	 * Creates a list of Strings from an single String using commas.
	 * @param string containing commas
	 * @return list of Strings
	 */
	public static List<String> commaSeparatedStringToStringList(final String s) {
		final String[] result = commaSeparatedStringToStringArray(s);
		final List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}

	/**
	 * Creates a hashset of Strings from an single String using commas.
	 * @param string containing commas
	 * @return hashset of Strings
	 */
	public static HashSet<String> commaSeparatedStringToStringHashSet(final String s) {
		final String[] result = commaSeparatedStringToStringArray(s);
		final HashSet<String> toReturn = new HashSet<String>();
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}
	
	/**
	 * @return String representation of Timestamp for 'now'. 
	 */
	public static String getNowAsTimeStamp() {
		return "" + new Date().getTime();
	}

	/**
	 * Converts the current point of time as String. 
	 * @param dateFormat
	 * @return String representation of Timestamp for 'now' formatted by dateFormat. 
	 */
	public static String getNowAsFormattedString(final String dateFormat) {
		return getDateAsFormattedString(new Date(), dateFormat);
	}

	/**
	 * Converts a date into a String representation using a given dateformt
	 * @param date to format
	 * @param dateFormat as String to instantiate a SimpleDateFormat object, e.g. "yyyy.MM.dd HHmm"
	 * @return formatted date string e.g. "2013.06.06 2012"
	 */
	public static String getDateAsFormattedString(final Date date, final String dateFormat) {
		final DateFormat fmt = new SimpleDateFormat( dateFormat );
		return fmt.format(date);
	}

	/**
	 * Replaces in an containerString als occurrences of toReplace by replacement.
	 * @param containerString
	 * @param toReplace
	 * @param replacement
	 * @return replaced String
	 */
	public static String replaceAllIn(final String containerString, final String toReplace, final String replacement) {
		return StringUtils.replace(containerString, toReplace, replacement);
	}

	/**
	 * Reads text file content using UTF-8 encoding.
	 * @param filename
	 * @return List of strings or empty list (if file does not exist)
	 * @throws IOException
	 */
	public static List<String> getTextFileContent(final String filename) throws IOException {
		
		final File file = new File(filename);
		
		if (file.exists())
		{
			return FileUtil.getFileContentAsList(file);
		}
		
		final List<String> toReturn = new ArrayList<String>();
		toReturn.add("ERROR: File " + file.getAbsolutePath() + " not found!");
		return toReturn;
	}

	/**
	 * Converts e.g. orderCustomerAddress into ORDER_CUSTOMER_ADDRESS.
	 * Spaces are removed.
	 * @param camelCaseString
	 * @return String that represents a typical tablename in a database
	 */
	public static String toDBTableName(final String camelCaseString) 
	{
		String toReturn = "";
		char[] charArray = camelCaseString.replace(" ", "").toCharArray();
		
		for (char c : charArray) {
			if (c >= 97) {
				toReturn += ("" + c).toUpperCase();
			} else {
				toReturn += "_" + c;
			}
		}
		
		return toReturn;
	}

	/**
	 * Converts e.g. orderCustomerAddress into 'Order Customer Address'.
	 * Spaces are removed.
	 * @param camelCaseString
	 * @return String that represents a typical tablename in a database
	 */
	public static String toDisplayName(final String camelCaseString) 
	{
		String toReturn = "";
		char[] charArray = camelCaseString.replace(" ", "").toCharArray();
		
		boolean firstChar = true;
		for (char c : charArray) {
			if (firstChar) {
				toReturn += ("" + c).toUpperCase();
				firstChar = false;
			} 
			else
			{
				if (c >= 97) {
					toReturn += ("" + c);
				} else {
					toReturn += " " + c;
				}
			}
			
		}
		
		return toReturn;
	}
	
}