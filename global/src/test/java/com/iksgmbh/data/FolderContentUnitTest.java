/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.data;

import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER1;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER2;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_SUB_FOLDER;
import static com.iksgmbh.test.FolderContentTestUtility.initTestFolder;
import static com.iksgmbh.test.FolderContentTestUtility.mainTestFolder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		initTestFolder();

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
		initTestFolder();

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
		initTestFolder();

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
		initTestFolder();

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
		initTestFolder();

		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(SUB_FOLDER1, SUB_FOLDER2);
		final FolderContent folderContent = new FolderContent(mainTestFolder, ignoreList);
		final List<File> result = folderContent.getFilesWithEndingPattern("java");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 0, result.size());
	}

	@Test
	public void returnsNumberFoldersWithoutSubFolder1And2() throws Exception {
		// prepare test
		initTestFolder();

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
		initTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithEndingPattern("java");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 2, result.size());
	}

	@Test
	public void returnsNumberOfTxtFiles() throws Exception {
		// prepare test
		initTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithEndingPattern("txt");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 4, result.size());
	}
	
	@Test
	public void returnsNumberOfIniFiles() throws Exception {
		// prepare test
		initTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithEndingPattern("ini");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 0, result.size());
	}

	@Test
	public void returnsNumberOfXmlFiles() throws Exception {
		// prepare test
		initTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithEndingPattern("xml");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 2, result.size());
	}

	@Test
	public void returnsNumberOfPropertiesFiles() throws Exception {
		// prepare test
		initTestFolder();

		// call functionality under test
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> result = folderContent.getFilesWithEndingPattern("properties");
		
		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("size of result list", 1, result.size());
	}

	@Test
	public void returnsFolderForFileName() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);

		// call functionality under test
		final File result = folderContent.getFolder(SUB_FOLDER1);
		
		// verify test result
		assertNotNull("Not null expected", result);
	}

	@Test
	public void returnsFolderForPathEnding() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);

		// call functionality under test
		final File result = folderContent.getFolder(SUB_FOLDER2 + "/" + SUB_SUB_FOLDER);
		
		// verify test result
		assertNotNull("Not null expected", result);
	}
	
	@Test
	public void returnsNullForNotExistingFolder() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);

		// call functionality under test
		final File result = folderContent.getFolder("NOT_EXISTONG");
		
		// verify test result
		assertNull(result);
	}

	@Test
	public void throwsExceptionForAmbiguousFolderName() throws Exception {
		// prepare test
		initTestFolder();
		final File newSubFolder = new File(mainTestFolder, SUB_FOLDER1 + "/" + SUB_FOLDER2);
		newSubFolder.mkdirs();
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);

		// call functionality under test
		try {
			folderContent.getFolder(SUB_FOLDER2);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			System.err.println(e.getMessage());
			assertTrue(e.getMessage().contains("Ambiguous path ending: 2 matches for 'subFolder2'"));			
		}
	}
}