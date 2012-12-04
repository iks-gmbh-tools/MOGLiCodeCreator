package com.iksgmbh.moglicc.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiCodeCreator2;
import com.iksgmbh.moglicc.MOGLiSystemConstants2;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.utils.FileUtil;

public class MOGLiLogUtilUnitTest2 extends CoreTestParent {
	
	public static final String LOGFILE = MOGLiSystemConstants2.DIR_LOGS_FILES + "/" + MOGLiSystemConstants2.FILENAME_LOG_FILE;
	
	@Before
	public void setup() {
		super.setup();
		MOGLiCodeCreator2.setApplicationRootDir(applicationTestDirAsString);
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void createsNewLogfile() {
		// prepare test
		applicationLogfile.delete();
		assertFileDoesNotExist(applicationLogfile);
		
		// call functionality under test
		MOGLiLogUtil2.createNewLogfile(applicationLogfile);
		
		// verify test result
		assertFileExists(applicationLogfile);
	}

	@Test
	public void testLog() {
		MOGLiLogUtil2.createNewLogfile(applicationLogfile);
		MOGLiLogUtil2.logInfo("Test");
		assertLogfileIsEntry("Test" + FileUtil.getSystemLineSeparator());
	}
	
	@Test
	public void testLogError() {
		MOGLiLogUtil2.createNewLogfile(applicationLogfile);
		MOGLiLogUtil2.logError("Test");
		assertLogfileIsEntry("ERROR: Test" + FileUtil.getSystemLineSeparator());
	}

	
	private void assertLogfileIsEntry(String expectedLogEntry) {
		File logfile = MOGLiFileUtil2.getNewFileInstance(LOGFILE);
		final String actualFileContent = MOGLiFileUtil2.getFileContent(logfile);
		assertEquals("Expected Log Entry not found in logfile", expectedLogEntry, actualFileContent);
	}

}
