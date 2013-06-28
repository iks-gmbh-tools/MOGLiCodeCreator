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

	@Test
	public void replacesLinesInTextFile() throws Exception {
		// prepare test
		final File textFile = new File("target/Test.txt");
		FileUtil.createNewFileWithContent(textFile, "ABC" + FileUtil.getSystemLineSeparator()
				                                    + "CDE" + FileUtil.getSystemLineSeparator()
				                                    + "FGH");

		// call functionality under test
		FileUtil.replaceLinesInTextFile(textFile, "D", "X");

		// verify test result
		final String fileContent = FileUtil.getFileContent(textFile);
		final String exected = "ABC" + FileUtil.getSystemLineSeparator()
						        + "CXE" + FileUtil.getSystemLineSeparator()
						        + "FGH";
		assertEquals("fileContent", exected, fileContent);
	}

	@Test
	public void returnsTipStatus() throws Exception {
		// prepare test 1
		final File folder = new File("target/TestFolder");
		FileUtil.deleteDirWithContent(folder);
		folder.mkdirs();
		final File file = new File(folder, "text.txt");
		file.createNewFile();

		// call functionality under test 1 and verify
		assertTrue(FileUtil.isTip(folder));

		// prepare test 1
		final File subfolder = new File(folder, "subFolder");
		subfolder.mkdirs();
		final File file2 = new File(subfolder, "text.txt");
		file2.createNewFile();
		
		// call functionality under test 1 and verify
		assertFalse(FileUtil.isTip(folder));
	}

}
