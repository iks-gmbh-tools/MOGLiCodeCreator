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