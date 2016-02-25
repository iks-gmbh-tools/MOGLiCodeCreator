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