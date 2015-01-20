package com.iksgmbh.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilUnitTest {

	@Test
	public void removesLineFromList() {
		final String sep = FileUtil.getSystemLineSeparator();
		final String s = "# comment 1" + sep + "plugin1, plugin2" + sep + "# comment 2" + sep + "plugin 3" + sep + "#comment 3";
		final List<String> lines = StringUtil.getLinesFromText(s);
		final List<String> result = StringUtil.removeLineFromList(lines, "comment");
		final String actual = StringUtil.concat(result, ", ");
		final String expected = "plugin1, plugin2, plugin 3";
		assertEquals("Comment line removed incorrectly", expected, actual);
		assertEquals("Line Number", lines.size()-3, result.size());
	}
	
	@Test
	public void replacesLineInList() {
		final String sep = FileUtil.getSystemLineSeparator();
		final String s = "# comment 1" + sep + "plugin1, plugin2" + sep + "# comment 2" + sep + "plugin 3" + sep + "#comment 3";
		final List<String> lines = StringUtil.getLinesFromText(s);
		final List<String> result = StringUtil.replaceLineInList(lines, "plugin 3", "replaced");
		final String actual = StringUtil.concat(result, ", ");
		final String expected = "# comment 1, plugin1, plugin2, # comment 2, replaced, #comment 3";
		assertEquals("Comment line removed incorrectly", expected, actual);
		assertEquals("Line Number", lines.size(), result.size());
	}	

	@Test
	public void testExtractCommaSeparatedPluginList() {
		final String sep = FileUtil.getSystemLineSeparator();
		final String s = "# comment 1" + sep + "plugin1, plugin2" + sep + sep + "# comment 2" + sep + "plugin 3" + sep + "#comment 3";
		final String actual = StringUtil.removeBlankAndCommentLines(s, '#');
		final String expected = "plugin1, plugin2, plugin 3";
		assertEquals("Comment line removed incorrectly", expected, actual);
	}

	@Test
	public void testGetListFromLineWithCommaSeparatedElements() {
		// test 1
		String[] pluginList = StringUtil.getListFromLineWithCommaSeparatedElements(" ");
		assertEquals(pluginList.length, 0);

		// test 2
		final String sep = FileUtil.getSystemLineSeparator();
		final String s = "# comment 1" + sep + "plugin1, plugin2" + sep + sep + "# comment 2" + sep + "plugin 3" + sep + "#comment 3";
		final String actual = StringUtil.removeBlankAndCommentLines(s, '#');
		pluginList = StringUtil.getListFromLineWithCommaSeparatedElements(actual);
		assertEquals(pluginList.length, 3);
		assertEquals("Unexpected plugin list. ", "plugin1", pluginList[0]);
		assertEquals("Unexpected plugin list. ", "plugin2", pluginList[1]);
		assertEquals("Unexpected plugin list. ", "plugin3", pluginList[2]);
	}

	@Test
	public void testConcat() {
		final List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		final String result = StringUtil.concat(list);
		assertEquals("result ", "a" + FileUtil.getSystemLineSeparator() + "b", result );
	}

	@Test
	public void removesSuffixByLength() {
		// call functionality under test
		final String result = StringUtil.removeSuffixByLength("abc", 1);

		// verify test result
		assertEquals("result ", "ab", result );
		
	}

	@Test
	public void removesSuffixIfExisting() {
		// prepare test
		final String suffix = "-1.2.3";
		final String s = "abcde";

		// call functionality under test
		final String result = StringUtil.removeSuffixIfExisting(s + suffix, suffix);

		// verify test result
		assertEquals("result ", s, result );
		assertEquals("result ", s, StringUtil.removeSuffixIfExisting(s + suffix, suffix) );
	}
	
	@Test
	public void removesPrefixIfExisting() {
		// prepare test
		final String prefix = "1.2.3-";
		final String s = "abcde";

		// call functionality under test
		final String result = StringUtil.removePrefixIfExisting(prefix + s, prefix);

		// verify test result
		assertEquals("result ", s, result );
	}

	@Test
	public void replacesSubstringBetweenString1AndString2() {
		// prepare test
		final String s = "AB C DE";

		// call functionality under test
		final String result1 = StringUtil.replaceBetween(s, "AB", "DE", "3");
		final String result2 = StringUtil.replaceBetween(s, "X", "D", "3");
		final String result3 = StringUtil.replaceBetween(s, "B", "X", "3");
		final String result4 = StringUtil.replaceBetween(s, "D", "B", "3");

		// verify test result
		assertEquals("result ", "AB3DE", result1 );
		assertEquals("result ", "AB C DE", result2 );
		assertEquals("result ", "AB C DE", result3 );
		assertEquals("result ", "AB C DE", result4 );
	}

	@Test
	public void getsStringFromStringListThatContainsSubstring() {
		// prepare test
		final String expectedResult = "blurb 132 blub"; 
		final List<String> list = new ArrayList<String>();
		list.add("bubu 123 bubu");
		list.add(expectedResult);
		list.add("blarb 312 bulb");
		
		// call functionality under test
		final String result1 = StringUtil.getStringFromStringListThatContainsSubstring(list, "132");
		final String result2 = StringUtil.getStringFromStringListThatContainsSubstring(list, "xy");
		
		// verify test result
		assertEquals("result ", expectedResult, result1 );
		assertEquals("result ", null, result2 );
	}

	@Test
	public void getsIndexForElementThatContainsSubstring() {
		// prepare test
		final List<String> list = new ArrayList<String>();
		list.add("bubu 123 bubu");
		list.add("blurb 132 blub");
		list.add("blarb 312 bulb");
		
		// call functionality under test
		final int result1 = StringUtil.getIndexForElementThatContainsSubstring(list, "132");
		final int result2 = StringUtil.getIndexForElementThatContainsSubstring(list, "xy");
		
		// verify test result
		assertEquals("result ", 1, result1 );
		assertEquals("result ", -1, result2 );
	}

	@Test
	public void buildsStringListFromCommaSeparatedString() {
		// prepare test
		final String s = "Can you see, you and me, are free.";
		
		// call functionality under test
		final List<String> result = StringUtil.commaSeparatedStringToStringList(s);
		
		// verify test result
		assertEquals("result size", 3, result.size() );
		assertEquals("result ", "Can you see", result.get(0) );
		assertEquals("result ", "you and me", result.get(1) );
		assertEquals("result ", "are free.", result.get(2) );
	}
		
}
