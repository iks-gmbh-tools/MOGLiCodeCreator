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

	public static void addToList(final List<String> list, 
			                     final String newElement) 
	{
		list.add(newElement);
	}

	public static boolean contains(String text, String searchString) 
	{
		if (searchString == null) return false;
		
		return text.contains(searchString);
	}
	
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
	

	public boolean isStringValueSet(String s) {
		return s != null;
	}

	public boolean isIntegerValueSet(Integer i) {
		return i != null;
	}

	public boolean isNullString(String s) {
		return s.equals("null");
	}

	public boolean isBooleanValueSet(Boolean b) {
		return b != null;
	}

	public boolean isCreateGUI(Boolean b) {
		return b.booleanValue();
	}

	/**
	 * First letter to upper: "upper" -> "Upper"
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
	 * First letter to lower: "Lower" -> "lower"
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

	public static String cutSuffix(String str, String suffix) {
		return StringUtils.removeEnd(str, suffix);
	}

	public static String cutLeadingChars(String str, int num) {
		return str.substring(num);
	}

	public static String addStrings(String s1, String s2) {
		return s1 + s2;
	}

	public static boolean isListEmpty(final List<String> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return list.isEmpty();
	}

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
	
	public static String[] commaSeparatedStringToStringArray(final String s) {
		final String[] result = s.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim(); 
		}
		return result;
	}
	
	public static List<String> commaSeparatedStringToStringList(final String s) {
		final String[] result = commaSeparatedStringToStringArray(s);
		final List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}

	public static HashSet<String> commaSeparatedStringToStringHashSet(final String s) {
		final String[] result = commaSeparatedStringToStringArray(s);
		final HashSet<String> toReturn = new HashSet<String>();
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}
	
	public static String getNowAsTimeStamp() {
		return "" + new Date().getTime();
	}

	public static String getNowAsFormattedString(final String dateFormat) {
		return getDateAsFormattedString(new Date(), dateFormat);
	}

	/**
	 * @param dateFormat as String to instanciate a SimpleDateFormat object, e.g. "yyyy.MM.dd HHmm"
	 * @return formatted date string e.g. "2013.06.06 1202"
	 */
	public static String getDateAsFormattedString(final Date date, final String dateFormat) {
		final DateFormat fmt = new SimpleDateFormat( dateFormat );
		return fmt.format(date);
	}

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
	
}
