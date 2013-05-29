package com.iksgmbh.moglicc.generator.modelbased.filestructure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

public class ReplacementDataParserUnitTest {

	@Test
	public void parsesReplacementLine() throws MOGLiPluginException {
		// prepare test
		final String line = "@replaceIn * <mavenGroupId> com.iksgmbh.moglicc"; 

		// call functionality under test
		final ReplacementData replacement = new ReplacementDataParser().parse(line);

		// verify test result
		assertEquals("search string", "<mavenGroupId>", replacement.getOldString());
		assertEquals("replacement string", "com.iksgmbh.moglicc", replacement.getNewString());
		assertEquals("file ending pattern", null, replacement.getFileEndingPattern());
	}
	
	@Test
	public void parsesReplacementLineUsingSpaces() throws MOGLiPluginException {
		// prepare test
		final String line = "@replaceIn \".xml\" \"Maven Description\" \"My Description\""; 

		// call functionality under test
		final ReplacementData replacement = new ReplacementDataParser().parse(line);

		// verify test result
		assertEquals("search string", "Maven Description", replacement.getOldString());
		assertEquals("replacement string", "My Description", replacement.getNewString());
		assertEquals("file ending pattern", ".xml", replacement.getFileEndingPattern());
	}
	
}
