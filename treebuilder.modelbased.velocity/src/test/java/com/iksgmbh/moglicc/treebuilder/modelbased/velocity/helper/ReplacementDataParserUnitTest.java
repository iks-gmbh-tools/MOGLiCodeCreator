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

import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.VelocityModelBasedTreeBuilderTestParent;

public class ReplacementDataParserUnitTest extends VelocityModelBasedTreeBuilderTestParent {

	@Test
	public void parsesReplacementLine() throws MOGLiPluginException {
		// prepare test
		final String line = "* mavenGroupId com.iksgmbh.moglicc"; 

		// call functionality under test
		final ReplacementData replacement = ReplacementDataParser.doYourJobFor(line);

		// verify test result
		assertEquals("search string", "mavenGroupId", replacement.getOldString());
		assertEquals("replacement string", "com.iksgmbh.moglicc", replacement.getNewString());
		assertEquals("file ending pattern", null, replacement.getFileEndingPattern());
	}
	
	@Test
	public void parsesReplacementLineUsingSpaces() throws MOGLiPluginException {
		// prepare test
		final String line = "\".xml\" \"Maven Description\" \"My Description\""; 

		// call functionality under test
		final ReplacementData replacement = ReplacementDataParser.doYourJobFor(line);

		// verify test result
		assertEquals("search string", "Maven Description", replacement.getOldString());
		assertEquals("replacement string", "My Description", replacement.getNewString());
		assertEquals("file ending pattern", ".xml", replacement.getFileEndingPattern());
	}
	
	@Test
	public void throwsExceptionForMissingReplacement() throws MOGLiPluginException {
		// prepare test
		final String line = "filename searchstring"; 

		try {
			// call functionality under test
			ReplacementDataParser.doYourJobFor(line);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", "Missing value in line: " + SYSTEM_LINE_SEPARATOR 
					                      + "@ReplaceIn filename searchstring", e.getMessage());
		}
	}

	@Test
	public void throwsExceptionForMissingValues() throws MOGLiPluginException {
		// prepare test
		final String line = "filename"; 

		try {
			// call functionality under test
			ReplacementDataParser.doYourJobFor(line);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", "Values are missing for line: " + SYSTEM_LINE_SEPARATOR 
					                      + "@ReplaceIn filename", e.getMessage());
		}
	}	
}