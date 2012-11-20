package com.iksgmbh.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FileUtilUnitTest {

	@Test
	public void testRemoveFileExtension() {
		String filename = "a.b.c";
		assertEquals("File Extension wrongly removed", "a.b", FileUtil.removeFileExtension(filename));
		
		filename = "b.c";
		assertEquals("File Extension wrongly removed", "b", FileUtil.removeFileExtension(filename));

		filename = "c";
		assertEquals("File Extension wrongly removed", "c", FileUtil.removeFileExtension(filename));
	}
	
	@Test
	public void testAreFilePathsIdentical() {
		final File file1 = new File("Testfall");
		final File file2 = new File("Testfall");
		final File file3 = new File("Testfalll");
		
		assertTrue("Paths not identical", FileUtil.areFilePathsIdentical(file1, file2));
		assertFalse("Paths identical", FileUtil.areFilePathsIdentical(file1, file3));
	}
}
