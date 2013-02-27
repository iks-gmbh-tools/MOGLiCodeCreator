package com.iksgmbh.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilUnitTest {

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
	
}
