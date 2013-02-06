package com.iksgmbh.helper;

import static com.iksgmbh.test.FolderContentTestUtility.*;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

public class FolderContentBasedTextFileLineInserterUnitTests {

	@Before
	public void setup() {
		FileUtil.deleteDirWithContent(mainTestFolder);
	}

	@Test
	public void insertsLineInJavaFiles() throws Exception {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContentBasedTextFileLineInserter inserter = new FolderContentBasedTextFileLineInserter(mainTestFolder, null);
		inserter.setFileExtension("java");
		inserter.setLineMarkerToInsertAfter("A");
		inserter.insert(" B");

		// verify test result
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> javaFiles = folderContent.getFilesWithExtensions("java");
		assertFileContent("A" + LINE_BREAK + " B" + LINE_BREAK + "  C", javaFiles);

		final List<File> txtFiles = folderContent.getFilesWithExtensions("txt");
		assertFileContent("A" + LINE_BREAK + "  C", txtFiles);
	}

	@Test
	public void throwsExceptionWhenAFileWasNotFound() throws Exception {
		// prepare test
		final File testFolder = createTestFolder();
		final File missingFile1 = new File(testFolder, "MissingFile1.java");
		missingFile1.createNewFile();
		final File missingFile2 = new File(testFolder, "MissingFile2.java");
		missingFile2.createNewFile();

		// call functionality under test
		final FolderContentBasedTextFileLineInserter inserter = new FolderContentBasedTextFileLineInserter(mainTestFolder, null);
		inserter.setFileExtension("java");
		inserter.setLineMarkerToInsertAfter("A");
		missingFile1.delete();
		missingFile2.delete();
		
		try {			
			inserter.insert(" B");
			fail("Unexpect exception not thrown!");
		} catch (final RuntimeException re) {
			// verify test result
			final String expected = "Error reading D:\\Reik\\dev\\git\\mogli\\global\\..\\global\\target\\sourceTestFolder\\MissingFile1.java"
			                        + FileUtil.getSystemLineSeparator() +
			                        "Error reading D:\\Reik\\dev\\git\\mogli\\global\\..\\global\\target\\sourceTestFolder\\MissingFile2.java";

			assertEquals("Error message", expected, re.getMessage());
		}
	}

	private void assertFileContent(final String expectedContent, final List<File> files) throws IOException {
		for (final File file : files) {
			final String actualContent = FileUtil.getFileContent(file);
			assertEquals("file content", expectedContent, actualContent);
		}
	}

}
