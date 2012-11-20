package com.iksgmbh.moglicc.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.utils.FileUtil;

public class MogliFileUtilUnitTest extends CoreTestParent {

	private static final String TEST_FILE_CONTENT = "TEST FILE CONTENT";
	
	@Before
	public void setup() {
		super.setup();
		try {
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
		} catch (IOException e) {
			throw new MogliCoreException("Error writing file " + applicationLogfile.getAbsolutePath());
		}
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void testGetFileContent() {
		try {
			String actualFileContent = MogliFileUtil.getFileContent(applicationLogfile);
			assertNotNull(actualFileContent);
			String expectedFileContent = TEST_FILE_CONTENT + FileUtil.getSystemLineSeparator();
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAppendToFile() {
		try {
			String actualFileContent = MogliFileUtil.getFileContent(applicationLogfile);
			assertNotNull(actualFileContent);
			String expectedFileContent = TEST_FILE_CONTENT + FileUtil.getSystemLineSeparator();
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));
			
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
			actualFileContent = MogliFileUtil.getFileContent(applicationLogfile);
			expectedFileContent += expectedFileContent;
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
