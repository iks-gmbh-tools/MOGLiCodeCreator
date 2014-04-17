package com.iksgmbh.helper;

import static com.iksgmbh.test.FolderContentTestUtility.LINE_BREAK;
import static com.iksgmbh.test.FolderContentTestUtility.assertFileContent;
import static com.iksgmbh.test.FolderContentTestUtility.initTestFolder;
import static com.iksgmbh.test.FolderContentTestUtility.mainTestFolder;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

public class FolderContentBasedTextFileLineInserterUnitTest {

	@Before
	public void setup() {
		FileUtil.deleteDirWithContent(mainTestFolder);
	}

	@Test
	public void insertsLineInJavaFiles() throws Exception {
		// prepare test
		initTestFolder();

		// call functionality under test
		final FolderContentBasedTextFileLineInserter inserter = new FolderContentBasedTextFileLineInserter(mainTestFolder, null);
		inserter.setFileExtension("java");
		inserter.setLineMarkerToInsertAfter("A");
		inserter.insert(" B");

		// verify test result
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> javaFiles = folderContent.getFilesWithEndingPattern("java");
		assertFileContent("A" + LINE_BREAK + " B" + LINE_BREAK + "  C", javaFiles);

		final List<File> txtFiles = folderContent.getFilesWithEndingPattern("txt");
		assertFileContent("A" + LINE_BREAK + "  C", txtFiles);
	}

	@Test
	public void throwsExceptionWhenAFileWasNotFound() throws Exception {
		// prepare test
		final File testFolder = initTestFolder();
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
			assertTrue(re.getMessage().contains("Error reading"));
			assertTrue(re.getMessage().contains("MissingFile1.java"));
			assertTrue(re.getMessage().contains("MissingFile2.java"));
		}
	}

}
