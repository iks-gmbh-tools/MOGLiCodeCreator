package com.iksgmbh.helper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

public class FolderContentBasedTextFileLineInserterUnitTests {
	
	private static final String LINE_BREAK = FileUtil.getSystemLineSeparator();
	private static final String TEST_MAIN_FOLDER = "../global/target/sourceFolder";
	private static final String SUB_FOLDER1 = "subFolder1";
	private static final String SUB_FOLDER2 = "subFolder2";
	private static final String SUB_SUB_FOLDER = "subSubFolder";
	
	private File mainTestFolder = new File(TEST_MAIN_FOLDER);
	
	@After
	public void tearDown() {
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

	private void assertFileContent(final String expectedContent, final List<File> files) throws IOException {
		for (final File file : files) {
			final String actualContent = FileUtil.getFileContent(file);
			assertEquals("file content", expectedContent, actualContent);
		}
	}

	private void createTestFolder() throws Exception {
		mainTestFolder.mkdirs();
		
		File file = new File(mainTestFolder, "file1.txt");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		file = new File(mainTestFolder, "file2.xml");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		
		File subFolder = new File(mainTestFolder, SUB_FOLDER1);
		subFolder.mkdirs();
		file = new File(subFolder, "file11.java");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		file = new File(subFolder, "file12.xml");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		
		subFolder = new File(mainTestFolder, SUB_FOLDER2);
		subFolder.mkdirs();
		file = new File(subFolder, "file21.txt");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		file = new File(subFolder, "file22.txt");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		
		subFolder = new File(subFolder, SUB_SUB_FOLDER);
		subFolder.mkdirs();
		file = new File(subFolder, "file.txt");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		file = new File(subFolder, "file.java");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
		file = new File(subFolder, "file.properties");
		FileUtil.createNewFileWithContent(file, "A" + LINE_BREAK + "  C");
	}

}
