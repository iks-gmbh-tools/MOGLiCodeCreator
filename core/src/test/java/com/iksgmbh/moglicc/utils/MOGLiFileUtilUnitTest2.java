package com.iksgmbh.moglicc.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiCoreException2;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.utils.FileUtil;

public class MOGLiFileUtilUnitTest2 extends CoreTestParent {

	private static final String TEST_FILE_CONTENT = "TEST FILE CONTENT";
	
	@Before
	public void setup() {
		super.setup();
		try {
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
		} catch (IOException e) {
			throw new MOGLiCoreException2("Error writing file " + applicationLogfile.getAbsolutePath());
		}
	}
	
	// **************************  Test Methods  *********************************
	
	@Test
	public void testGetFileContent() {
		try {
			String actualFileContent = MOGLiFileUtil2.getFileContent(applicationLogfile);
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
			String actualFileContent = MOGLiFileUtil2.getFileContent(applicationLogfile);
			assertNotNull(actualFileContent);
			String expectedFileContent = TEST_FILE_CONTENT + FileUtil.getSystemLineSeparator();
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));
			
			FileUtil.appendToFile(applicationLogfile, TEST_FILE_CONTENT);
			actualFileContent = MOGLiFileUtil2.getFileContent(applicationLogfile);
			expectedFileContent += expectedFileContent;
			assertTrue("Unerwarteter filecontent: <" + actualFileContent + ">", expectedFileContent.equals(actualFileContent));			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
