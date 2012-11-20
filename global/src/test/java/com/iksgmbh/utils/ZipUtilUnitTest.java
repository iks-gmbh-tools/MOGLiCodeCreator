package com.iksgmbh.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ZipUtilUnitTest {
	
	public static final String USER_DIR = "../global/target/test-classes/";
	
	@Test
	public void testZipDir() throws Exception {
		// prepare test
		final String targetFilename = USER_DIR + "Test.zip";
		final File target = new File(targetFilename);
		target.delete();
		assertFalse("Target not deleted!", target.exists());
		
		// call functionality under test
		ZipUtil.zipDir(USER_DIR + "zipTestDir", targetFilename);
		
		// verify test result
		assertTrue("Target not created!", target.exists());
		assertEquals("Unexpected file size!", 6041, target.length());
	}
	
	@Test
	public void testUnzip() throws IOException {;
		// prepare test
		final String targetFilename = USER_DIR + "testUnzipDir";
		final File target = new File(targetFilename);
		FileUtil.deleteDirWithContent(target);
		assertFalse("Target not deleted!", target.exists());
		
		// call functionality under test
		final String filename = USER_DIR + "UnzipTest.zip";
		ZipUtil.unzip(filename, targetFilename);
		
		// verify test result
		assertTrue("Target does not exist!", target.exists());
		assertEquals("Unexpected file number!", 4, target.listFiles().length);
		final File subDir = FileUtil.getSubDir(target, "misc");
		assertEquals("Unexpected file number!", 2, subDir.listFiles().length);
	}
}
