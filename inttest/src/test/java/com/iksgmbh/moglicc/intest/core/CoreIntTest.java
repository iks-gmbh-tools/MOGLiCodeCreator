package com.iksgmbh.moglicc.intest.core;

import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_LOG_FILE;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.intest.IntTestParent;

public class CoreIntTest extends IntTestParent {

	@Test
	public void createsEmergencyLogFileIfDefinedWorkspaceDirCannotBeCreated() {
		// prepare test
		initPropertiesWith("workspace=C:/temp/Mogli/workspace");
		final File emergencyLogFile = new File(applicationTestDir, FILENAME_LOG_FILE);
		assertFileDoesNotExist(emergencyLogFile);
		String[] args = new String[0];
		
		// call functionality under test
		MogliCodeCreator.main(args);
		
		// cleanup critical stuff before possible test failures
		applicationPropertiesFile.delete();
		assertFileDoesNotExist(applicationPropertiesFile);
		
		// verify test result
		assertFileExists(emergencyLogFile);
		assertFileContainsEntry(emergencyLogFile, "C:\\temp\\Mogli\\workspace\\log");
		assertFileContainsEntry(emergencyLogFile, "Could not create ");
	}

}
