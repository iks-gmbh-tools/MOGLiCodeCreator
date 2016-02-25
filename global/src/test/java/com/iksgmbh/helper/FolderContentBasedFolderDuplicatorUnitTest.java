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
package com.iksgmbh.helper;

import static com.iksgmbh.test.FolderContentTestUtility.FILE11_JAVA;
import static com.iksgmbh.test.FolderContentTestUtility.FILE12_XML;
import static com.iksgmbh.test.FolderContentTestUtility.FILE1_TXT;
import static com.iksgmbh.test.FolderContentTestUtility.FILE21_TXT;
import static com.iksgmbh.test.FolderContentTestUtility.FILE22_TXT;
import static com.iksgmbh.test.FolderContentTestUtility.FILE2_XML;
import static com.iksgmbh.test.FolderContentTestUtility.FILE_JAVA;
import static com.iksgmbh.test.FolderContentTestUtility.FILE_ORIG_CONTENT;
import static com.iksgmbh.test.FolderContentTestUtility.FILE_PROPERTIES;
import static com.iksgmbh.test.FolderContentTestUtility.FILE_TXT;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER1;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER2;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_SUB_FOLDER;
import static com.iksgmbh.test.FolderContentTestUtility.initTestFolder;
import static com.iksgmbh.test.FolderContentTestUtility.mainTestFolder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.FileUtil.FileCreationStatus;
import com.iksgmbh.utils.ImmutableUtil;

public class FolderContentBasedFolderDuplicatorUnitTest {
	
	public static File targetTestFolder = new File("../global/target/targetTestFolder");
	
	private enum Scenario {CREATE_NEW, OVERWRITE, PRESERVE};

	@Before
	public void setup() {
		FileUtil.deleteDirWithContent(mainTestFolder);
		FileUtil.deleteDirWithContent(targetTestFolder);
	}
	
	@Test
	public void duplicatesSourceFolder() throws Exception {
		// prepare test
		initTestFolder();
		assertFalse("Folder does already exist:\n" + targetTestFolder.getAbsolutePath(), targetTestFolder.exists());
		
		// call functionality under test
		final HashMap<String, FileCreationStatus> result = createTestDuplicator().duplicateTo(targetTestFolder);

		// verify test result
		assertTargetDirContent();
		assertFileStatusSamples(result, Scenario.CREATE_NEW);		
	}
	
	private FolderContentBasedFolderDuplicator createTestDuplicator() {
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(".svn");
		return new FolderContentBasedFolderDuplicator(mainTestFolder, ignoreList);
	}

	private void assertTargetDirContent() throws IOException {
		assertTrue("Folder does not exist:\n" + targetTestFolder.getAbsolutePath(), targetTestFolder.exists());
		File file = new File(targetTestFolder, FILE1_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(targetTestFolder, FILE2_XML);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		File folder = new File(targetTestFolder, SUB_FOLDER1);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE11_JAVA);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE12_XML);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());

		
		folder = new File(targetTestFolder, SUB_FOLDER2);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE21_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE22_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		folder = new File(folder, SUB_SUB_FOLDER);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE_JAVA);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE_PROPERTIES);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		final String fileContent = FileUtil.getFileContent(file);
		assertEquals("file content", FILE_ORIG_CONTENT, fileContent);
	}
	
	@Test
	public void duplicatesSourceFolderWithExistingTargetDirAndWithOverrideInstruction() throws Exception {
		// prepare test
		initTestFolder();
		createExistingFilesInTargetDir();
		
		// call functionality under test
		final HashMap<String, FileCreationStatus> result = createTestDuplicator().duplicateTo(targetTestFolder);

		// verify test result
		assertTargetDirContent();
		assertFileStatusSamples(result, Scenario.OVERWRITE);		
	}

	@Test
	public void duplicatesSourceFolderWithExistingTargetDirAndWithPreserveInstruction() throws Exception {
		// prepare test
		initTestFolder();
		createExistingFilesInTargetDir();
		
		// call functionality under test
		final HashMap<String, FileCreationStatus> result = createTestDuplicator().duplicateTo(targetTestFolder, true);

		// verify test result
		assertTargetDirContent();
		assertFileStatusSamples(result, Scenario.PRESERVE);		
	}

	private void createExistingFilesInTargetDir() throws IOException {
		targetTestFolder.mkdirs();
		final File txtFile1 = new File(targetTestFolder, FILE1_TXT);
		txtFile1.createNewFile();
		final File folder = new File(targetTestFolder, SUB_FOLDER1);
		folder.mkdirs();
		final File javaFile1 = new File(folder, FILE11_JAVA);
		javaFile1.createNewFile();
	}

	private void assertFileStatusSamples(final HashMap<String, FileCreationStatus> result, final Scenario scenario) 
	{
		final File txtFile1 = new File(targetTestFolder, FILE1_TXT);
		final File folder1 = new File(targetTestFolder, SUB_FOLDER1);
		final File folder2 = new File(targetTestFolder, SUB_FOLDER2);
		final File javaFile1 = new File(folder1, FILE11_JAVA);
		final File xmlfile = new File(folder1, FILE12_XML);
		final File txtFile2 = new File(folder2, FILE22_TXT);
		
		assertEquals("FileStatus", FileCreationStatus.NOT_EXISTING_FILE_CREATED, result.get(txtFile2.getAbsolutePath()));
		assertEquals("FileStatus", FileCreationStatus.NOT_EXISTING_FILE_CREATED, result.get(xmlfile.getAbsolutePath()));
		
		if (scenario == Scenario.OVERWRITE) {
			assertEquals("FileStatus", FileCreationStatus.EXISTING_FILE_OVERWRITTEN, result.get(javaFile1.getAbsolutePath()));
			assertEquals("FileStatus", FileCreationStatus.EXISTING_FILE_OVERWRITTEN, result.get(txtFile1.getAbsolutePath()));
		}
		else if (scenario == Scenario.PRESERVE) {
			assertEquals("FileStatus", FileCreationStatus.EXISTING_FILE_PRESERVED, result.get(javaFile1.getAbsolutePath()));
			assertEquals("FileStatus", FileCreationStatus.EXISTING_FILE_PRESERVED, result.get(txtFile1.getAbsolutePath()));
			
		} else {
			assertEquals("FileStatus", FileCreationStatus.NOT_EXISTING_FILE_CREATED, result.get(javaFile1.getAbsolutePath()));
			assertEquals("FileStatus", FileCreationStatus.NOT_EXISTING_FILE_CREATED, result.get(txtFile1.getAbsolutePath()));
		}
	}
}