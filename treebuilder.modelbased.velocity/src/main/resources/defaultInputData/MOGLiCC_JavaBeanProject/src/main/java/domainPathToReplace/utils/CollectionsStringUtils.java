package <domainPathToReplace>.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CollectionsStringUtils
{
	public static List<String> commaSeparatedStringToStringList(final String s) {
		final List<String> toReturn = new ArrayList<String>();
		
		if (StringUtils.isEmpty(s))
		{
			return toReturn;
		}
		
		final String[] result = commaSeparatedStringToStringArray(s);
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}

	public static String[] commaSeparatedStringToStringArray(final String s) {
		if (StringUtils.isEmpty(s))
		{
			final String[] toReturn = {};
			return toReturn;
		}

		final String[] result = s.split(",");
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim(); 
		}
		return result;
	}

	public static HashSet<String> commaSeparatedStringToHashSet(final String s) {
		final HashSet<String> toReturn = new HashSet<String>();
		
		if (StringUtils.isEmpty(s))
		{
			return toReturn;
		}
		
		final String[] result = commaSeparatedStringToStringArray(s);
		for (int i = 0; i < result.length; i++) {
			toReturn.add(result[i]); 
		}
		return toReturn;
	}
		
	public static String stringListToCommaSeparatedString(final List<String> list) {
		if (list == null || list.size() == 0)
		{
			return "";
		}
		
		final StringBuffer sb = new StringBuffer();
		for (final String s : list) {
			sb.append(s).append(", ");
		}
		
		final String toReturn = sb.toString(); 
		return toReturn.substring(0, toReturn.length() - 2);
	}

	public static String stringArrayToCommaSeparatedString(final String[] array) {
		if (array == null || array.length == 0)
		{
			return "";
		}
		
		final StringBuffer sb = new StringBuffer();
		for (final String s : array) {
			sb.append(s).append(", ");
		}
		final String toReturn = sb.toString();
		
		return toReturn.substring(0, toReturn.length() - 2);
	}

	public static String stringHashSetToCommaSeparatedString(final HashSet<String> hashSet) {
		if (hashSet == null || hashSet.size() == 0)
		{
			return "";
		}
		
		final StringBuffer sb = new StringBuffer();
		for (final String s : hashSet) {
			sb.append(s).append(", ");
		}
		final String toReturn = sb.toString();
		
		return toReturn.substring(0, toReturn.length() - 2);
	}

	public static List<Long> commaSeparatedStringToLongList(final String s)
	{
		final List<Long> toReturn = new ArrayList<Long>();
		
		if (StringUtils.isEmpty(s))
		{
			return toReturn;
		}
		
		final String[] result = commaSeparatedStringToStringArray(s);
		for (int i = 0; i < result.length; i++) {
			toReturn.add(new Long( result[i] )); 
		}
		return toReturn;
	}
	
	public static String listOfLongsToCommaSeparatedString(final List<Long> listOfLongs) {
		if (listOfLongs == null || listOfLongs.size() == 0)
		{
			return "";
		}
		
		final StringBuffer sb = new StringBuffer();
		for (final Long l : listOfLongs) {
			sb.append(l).append(", ");
		}
		final String toReturn = sb.toString(); 
		return toReturn.substring(0, toReturn.length() - 2);
	}
	
}
