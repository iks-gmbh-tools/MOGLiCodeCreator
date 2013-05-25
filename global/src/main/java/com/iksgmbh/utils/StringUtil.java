package com.iksgmbh.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static String removeBlankAndCommentLines(final String text, final char COMMENT_INDICATOR) {
		String[] lines = text.split(FileUtil.getSystemLineSeparator());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() > 0 && line.charAt(0) != COMMENT_INDICATOR) {
				sb.append(line + ", ");
			}
		}
		String toReturn = sb.toString();
		if (toReturn.length() > 2) {
			toReturn = toReturn.substring(0, toReturn.length()-2);
		}
		return toReturn;
	}

	public static String[] getListFromLineWithCommaSeparatedElements(final String line) {
		if (line.trim().length() == 0) {
			return new String[0];
		}

		final String trimmedLine = line.replaceAll(" ", "");
		return trimmedLine.split(",");
	}

	public static boolean startsWithUpperCase(final String s) {
		return Character.isUpperCase(s.charAt(0));
	}

	public static boolean startsWithLowerCase(final String s) {
		return Character.isLowerCase(s.charAt(0));
	}
	
	public static String firstToUpperCase(final String string) {
		if (string == null) {
			return null;
		}
		if ("".equals(string)) {
			return "";
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String firstToLowerCase(final String string) {
		if (string == null) {
			return null;
		}
		if ("".equals(string)) {
			return "";
		}
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	public static String concat(final List<String> list) {
		final StringBuffer sb = new StringBuffer();
		int counter = 0;
		for (String line : list) {
			counter++;
			sb.append(line);
			if (counter < list.size()) {
				sb.append(FileUtil.getSystemLineSeparator());
			}
		}
		return sb.toString();
	}

	public static String removePrefixIfExisting(final String s, final String prefix) {
		if (s.startsWith(prefix)) {
			return s.substring(prefix.length());
		}
		return s;
	}
	
	public static String removeSuffixIfExisting(final String s, final String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.indexOf(suffix));
		}
		return s;
	}

	public static String removeSuffixByLength(final String s, final int numberOfChars) {
		return s.substring(0, s.length() - numberOfChars);
	}

	public static List<String> getLinesFromText(final String text) {
		final String[] result = StringUtils.splitByWholeSeparator(text, FileUtil.getSystemLineSeparator());
		return Arrays.asList(result);
	}

	public static String buildTextFromLines(final List<String> lines) {
		final StringBuffer sb = new StringBuffer();
		for (final String line : lines) {
			sb.append(line);
			sb.append(FileUtil.getSystemLineSeparator());
		}
		return sb.toString().trim();
	}

	public static String replaceBetween(final String s, final String replaceStart, final String replaceEnd, final String replacement) {
		final String toReplace = substringBetween(s, replaceStart, replaceEnd);
		if (toReplace == null) {
			return s;
		}
		return s.replace(toReplace, replacement);
	}

	public static String substringBetween(final String s, final String replaceStart, final String replaceEnd) {
		final int pos1 = s.indexOf(replaceStart) + replaceStart.length();
		final int pos2 = s.indexOf(replaceEnd);
		if (pos1 == 0 || pos2 == -1 || pos1 >= pos2) {
			return null;
		}
		final String toReplace = s.substring(pos1, pos2);
		return toReplace;
	}

}
