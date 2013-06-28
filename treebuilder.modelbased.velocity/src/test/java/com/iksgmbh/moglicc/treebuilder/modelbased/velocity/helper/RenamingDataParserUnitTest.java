package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.VelocityModelBasedTreeBuilderTestParent;

public class RenamingDataParserUnitTest extends VelocityModelBasedTreeBuilderTestParent {

	@Test
	public void parsesRenamingLineForPath() throws MOGLiPluginException {
		// prepare test
		final String line = "File1.txt File2.txt"; 

		// call functionality under test
		final RenamingData renaming = RenamingDataParser.doYourJobFor(line, true);

		// verify test result
		assertEquals("old name", "File1.txt", renaming.getOldName());
		assertEquals("new name", "File2.txt", renaming.getNewName());
		assertEquals("isPath", true, renaming.isPath());
	}

	@Test
	public void parsesRenamingLineForFile() throws MOGLiPluginException {
		// prepare test
		final String line = "dir1 dir2"; 

		// call functionality under test
		final RenamingData renaming = RenamingDataParser.doYourJobFor(line, false);

		// verify test result
		assertEquals("old name", "dir1", renaming.getOldName());
		assertEquals("new name", "dir2", renaming.getNewName());
		assertEquals("isPath", false, renaming.isPath());
	}

	@Test
	public void throwsExceptionForMissingName2() throws MOGLiPluginException {
		// prepare test
		final String line = "dir1"; 

		try {
			// call functionality under test
			RenamingDataParser.doYourJobFor(line, false);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", "Missing name2 in line: " + SYSTEM_LINE_SEPARATOR 
					                      + "@RenameFile dir1", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForSpaces() throws MOGLiPluginException {
		// prepare test
		final String line = "dir1 dir2 dir3"; 

		try {
			// call functionality under test
			RenamingDataParser.doYourJobFor(line, false);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", "Name2 must not contain spaces in line: " + SYSTEM_LINE_SEPARATOR 
					                      + "@RenameFile dir1 dir2 dir3", e.getMessage());
		}
	}
}
