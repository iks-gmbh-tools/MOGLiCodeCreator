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

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.test.CoreTestParent;

public class MOGLiLogUtilUnitTest extends CoreTestParent {
		
	@Before
	public void setup() {
		super.setup();
		MOGLiCodeCreator.setApplicationRootDir(applicationTestDirAsString);
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void createsNewLogfile() {
		// prepare test
		applicationLogfile.delete();
		assertFileDoesNotExist(applicationLogfile);
		
		// call functionality under test
		MOGLiLogUtil.createNewLogfile(applicationLogfile);
		
		// verify test result
		assertFileExists(applicationLogfile);
	}

	@Test
	public void testLog() {
		MOGLiLogUtil.createNewLogfile(applicationLogfile);
		MOGLiLogUtil.logInfo("Test");
		assertLogfileIsEntry("Test");
	}
	
	@Test
	public void testLogError() {
		MOGLiLogUtil.createNewLogfile(applicationLogfile);
		MOGLiLogUtil.logError("Test");
		assertLogfileIsEntry("ERROR: Test");
	}

	
	private void assertLogfileIsEntry(String expectedLogEntry) {
		final File logfile = MOGLiFileUtil.getNewFileInstance(LOGFILE);
		final String actualFileContent = MOGLiFileUtil.getFileContent(logfile);
		assertEquals("Expected Log Entry not found in logfile", expectedLogEntry, actualFileContent);
	}

}