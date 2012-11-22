package com.iksgmbh.moglicc.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.MogliSystemConstants;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.utils.FileUtil;

public class MogliLogUtilUnitTest extends CoreTestParent {
	
	public static final String LOGFILE = MogliSystemConstants.DIR_LOGS_FILES + "/" + MogliSystemConstants.FILENAME_LOG_FILE;
	
	@Before
	public void setup() {
		super.setup();
		MogliCodeCreator.setApplicationRootDir(applicationTestDirAsString);
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void createsNewLogfile() {
		// prepare test
		applicationLogfile.delete();
		assertFileDoesNotExist(applicationLogfile);
		
		// call functionality under test
		MogliLogUtil.createNewLogfile(applicationLogfile);
		
		// verify test result
		assertFileExists(applicationLogfile);
	}

	@Test
	public void testLog() {
		MogliLogUtil.createNewLogfile(applicationLogfile);
		MogliLogUtil.logInfo("Test");
		assertLogfileIsEntry("Test" + FileUtil.getSystemLineSeparator());
	}
	
	@Test
	public void testLogError() {
		MogliLogUtil.createNewLogfile(applicationLogfile);
		MogliLogUtil.logError("Test");
		assertLogfileIsEntry("ERROR: Test" + FileUtil.getSystemLineSeparator());
	}

	
	private void assertLogfileIsEntry(String expectedLogEntry) {
		File logfile = MogliFileUtil.getNewFileInstance(LOGFILE);
		final String actualFileContent = MogliFileUtil.getFileContent(logfile);
		assertEquals("Expected Log Entry not found in logfile", expectedLogEntry, actualFileContent);
	}

}
