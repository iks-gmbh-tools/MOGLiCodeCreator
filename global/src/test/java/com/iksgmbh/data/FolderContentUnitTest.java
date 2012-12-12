package com.iksgmbh.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class FolderContentUnitTest {

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
	public void returnsNumberOfFolders() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFolders();
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 4, result.size());
	}

	@Test
	public void returnsNumberOfAllFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFiles();
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 9, result.size());
	}

	@Test
	public void returnsNumberOfAllFilesWithoutSubSubFolder() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(SUB_SUB_FOLDER);
		final FolderContent folderContent = new FolderContent(mainTestFolder, ignoreList);
		final List<File> result = folderContent.getFiles();
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 6, result.size());
	}

	@Test
	public void returnsNumberOfAllFilesWithoutSubFolder2() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(SUB_FOLDER2);
		final FolderContent folderContent = new FolderContent(mainTestFolder, ignoreList);
		final List<File> result = folderContent.getFiles();
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 4, result.size());
	}

	@Test
	public void returnsNumberOfJavaFilesWithoutSubFolder1And2() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(SUB_FOLDER1, SUB_FOLDER2);
		final FolderContent folderContent = new FolderContent(mainTestFolder, ignoreList);
		final List<File> result = folderContent.getFilesWithExtensions("java");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 0, result.size());
	}

	@Test
	public void returnsNumberFoldersWithoutSubFolder1And2() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(SUB_FOLDER1, SUB_FOLDER2);
		final FolderContent folderContent = new FolderContent(mainTestFolder, ignoreList);
		final List<File> result = folderContent.getFolders();
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 1, result.size());
	}

	@Test
	public void returnsNumberOfJavaFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("java");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 2, result.size());
	}

	@Test
	public void returnsNumberOfTxtFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("txt");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 4, result.size());
	}
	
	@Test
	public void returnsNumberOfIniFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("ini");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 0, result.size());
	}

	@Test
	public void returnsNumberOfXmlFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("xml");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 2, result.size());
	}

	@Test
	public void returnsNumberOfPropertiesFiles() throws IOException {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("properties");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 1, result.size());
	}

	private void createTestFolder() throws IOException {
		mainTestFolder.mkdirs();
		
		File file = new File(mainTestFolder, "file1.txt");
		file.createNewFile();
		file = new File(mainTestFolder, "file2.xml");
		file.createNewFile();
		
		File subFolder = new File(mainTestFolder, SUB_FOLDER1);
		subFolder.mkdirs();
		file = new File(subFolder, "file11.java");
		file.createNewFile();
		file = new File(subFolder, "file12.xml");
		file.createNewFile();
		
		subFolder = new File(mainTestFolder, SUB_FOLDER2);
		subFolder.mkdirs();
		file = new File(subFolder, "file21.txt");
		file.createNewFile();
		file = new File(subFolder, "file22.txt");
		file.createNewFile();
		
		subFolder = new File(subFolder, SUB_SUB_FOLDER);
		subFolder.mkdirs();
		file = new File(subFolder, "file.txt");
		file.createNewFile();
		file = new File(subFolder, "file.java");
		file.createNewFile();
		file = new File(subFolder, "file.properties");
		file.createNewFile();
	}
}
