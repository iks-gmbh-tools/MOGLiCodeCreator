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
package com.iksgmbh.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

		return concat(list, FileUtil.getSystemLineSeparator());
	}
	
	public static String concat(final List<String> list, final String lineSeparator) {
		final StringBuffer sb = new StringBuffer();
		int counter = 0;
		for (String line : list) {
			counter++;
			sb.append(line);
			if (counter < list.size() && lineSeparator != null) {
				sb.append(lineSeparator);
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
	
	/**
	 * Removes all lines from the list that contains <toRemove>.
	 * @param lines
	 * @param toRemove
	 * @return List<String>
	 */
	public static List<String> removeLineFromList(final List<String> lines, final String toRemove) {
		final List<String> toReturn = new ArrayList<String>();
		for (final String line : lines) {
			if (! line.contains(toRemove))
			{
				toReturn.add(line);
			}
		}
		return toReturn;
	}
	
	public static List<String> replaceLineInList(final List<String> lines, final String lineToReplace, final String replacementLine) {
		final List<String> toReturn = new ArrayList<String>();
		for (final String line : lines) {
			if (line.equals(lineToReplace))
			{
				toReturn.add(replacementLine);
			}
			else
			{				
				toReturn.add(line);
			}
		}
		return toReturn;
	}	

	public static String replaceBetween(final String s, final String replaceStart, final String replaceEnd, final String replacement) {
		final String toReplace = substringBetween(s, replaceStart, replaceEnd);
		if (toReplace == null) {
			return s;
		}
		return s.replace(toReplace, replacement);
	}

	public static String substringBetween(final String s, final String replaceStart, final String replaceEnd) {
		final int pos1 = s.indexOf(replaceStart);
		final int pos2 = s.indexOf(replaceEnd);
		if (pos1 == -1 || pos2 == -1 || pos1 >= pos2) {
			return null;
		}
		final String toReplace = s.substring(pos1 + replaceStart.length(), pos2);
		return toReplace;
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  final List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

	public static String cutUnwantedLeadingControlChars(final String line) {
		if (line.trim().length() == 0) {
			return line;
		}

		String toReturn = line;
		while ((int)toReturn.charAt(0) == 65279) {
			toReturn = toReturn.substring(1);
			if (toReturn.length() == 0) {
				return "";
			}
		}
		return toReturn;
	}

	public static String getStringFromStringListThatContainsSubstring(final List<String> list, final String substring) {
		for (final String s : list) {
			if (s.contains(substring)) {
				return s;
			}
		}
		return null;
	}

	public static int getIndexForElementThatContainsSubstring(final List<String> list, final String substring) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(substring)) {
				return i;
			}
		}
		return -1;
	}

	public static List<String> commaSeparatedStringToStringList(final String s) {
		final String[] result = commaSeparatedStringToStringArray(s);
		final List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}

	public static String[] commaSeparatedStringToStringArray(final String s) {
		final String[] result = s.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim(); 
		}
		return result;
	}
	
}