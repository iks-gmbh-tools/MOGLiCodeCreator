package com.iksgmbh.data;

import static com.iksgmbh.test.FolderContentTestUtility.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class FolderContentUnitTest {
	
	@Before
	public void setup() {
		FileUtil.deleteDirWithContent(mainTestFolder);
	}
	
	@Test
	public void returnsNumberOfFolders() throws Exception {
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
	public void returnsNumberOfAllFiles() throws Exception {
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
	public void returnsNumberOfAllFilesWithoutSubSubFolder() throws Exception {
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
	public void returnsNumberOfAllFilesWithoutSubFolder2() throws Exception {
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
	public void returnsNumberOfJavaFilesWithoutSubFolder1And2() throws Exception {
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
	public void returnsNumberFoldersWithoutSubFolder1And2() throws Exception {
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
	public void returnsNumberOfJavaFiles() throws Exception {
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
	public void returnsNumberOfTxtFiles() throws Exception {
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
	public void returnsNumberOfIniFiles() throws Exception {
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
	public void returnsNumberOfXmlFiles() throws Exception {
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
	public void returnsNumberOfPropertiesFiles() throws Exception {
		// prepare test
		createTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithExtensions("properties");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 1, result.size());
	}

}
