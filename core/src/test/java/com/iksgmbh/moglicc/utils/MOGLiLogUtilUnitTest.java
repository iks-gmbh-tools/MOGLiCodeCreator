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
