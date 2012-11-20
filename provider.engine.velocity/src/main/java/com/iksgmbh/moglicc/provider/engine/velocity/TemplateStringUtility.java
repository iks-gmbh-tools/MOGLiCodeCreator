package com.iksgmbh.moglicc.provider.engine.velocity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TemplateStringUtility {

	public static boolean contains(String text, String searchString) {
		return text.contains(searchString);
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
		return list.isEmpty();
	}

	public static String toCommaSeparatedString(final List<String> list) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size()-1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	public static String getNowAsTimeStamp() {
		return "" + new Date().getTime();
	}

}
