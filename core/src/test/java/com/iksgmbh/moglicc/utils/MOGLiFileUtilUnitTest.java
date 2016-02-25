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
package com.iksgmbh.moglicc.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.utils.FileUtil;

public class MOGLiFileUtilUnitTest extends CoreTestParent {

	private static final String TEST_FILE_CONTENT = "TEST FILE CONTENT";
	
	@Before
	public void setup() {
		super.setup();
		try {
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error writing file " + applicationLogfile.getAbsolutePath());
		}
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void testGetFileContent() {
		try {
			String actualFileContent = MOGLiFileUtil.getFileContent(applicationLogfile);
			assertNotNull(actualFileContent);
			String expectedFileContent = TEST_FILE_CONTENT;
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAppendToFile() {
		try {
			String actualFileContent = MOGLiFileUtil.getFileContent(applicationLogfile);
			assertNotNull(actualFileContent);
			String expectedFileContent = TEST_FILE_CONTENT ;
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));
			
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
			actualFileContent = MOGLiFileUtil.getFileContent(applicationLogfile);
			expectedFileContent += FileUtil.getSystemLineSeparator() + expectedFileContent;
			assertEquals("file content",  expectedFileContent, actualFileContent);			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}