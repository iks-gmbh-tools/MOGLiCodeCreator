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

import static com.iksgmbh.test.FolderContentTestUtility.FILE_ORIG_CONTENT;
import static com.iksgmbh.test.FolderContentTestUtility.LINE_BREAK;
import static com.iksgmbh.test.FolderContentTestUtility.assertFileContent;
import static com.iksgmbh.test.FolderContentTestUtility.initTestFolder;
import static com.iksgmbh.test.FolderContentTestUtility.mainTestFolder;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;

public class FolderContentBasedTextFileLineReplacerUnitTest {

	@Test
	public void replacesSubstringInLinesButOnlyOfJavaFiles() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContentBasedTextFileLineReplacer replacer = new FolderContentBasedTextFileLineReplacer(mainTestFolder, null);
		final ReplacementData replacementData = new ReplacementData("A", "AA", ".java");

		// call functionality under test
		replacer.doYourJob(replacementData);

		// verify test result
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> javaFiles = folderContent.getFilesWithEndingPattern(".java");
		assertEquals("number java files", 2, javaFiles.size());
		assertFileContent("AA" + LINE_BREAK + "  C", javaFiles);

		final List<File> txtFiles = folderContent.getFilesWithEndingPattern(".txt");
		assertEquals("number text files", 4, txtFiles.size());
		assertFileContent(FILE_ORIG_CONTENT, txtFiles);
	}	
	
	@Test
	public void replacesTwoSubstringsInOneLines() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContentBasedTextFileLineReplacer replacer = new FolderContentBasedTextFileLineReplacer(mainTestFolder, null);
		final List<ReplacementData> replacements = new ArrayList<FolderContentBasedTextFileLineReplacer.ReplacementData>();
		final ReplacementData replacementData1 = new ReplacementData("A", "AA", ".java");
		replacements.add(replacementData1);
		final ReplacementData replacementData2 = new ReplacementData("C", "CC", ".java");
		replacements.add(replacementData2);

		// call functionality under test
		replacer.doYourJob(replacements);

		// verify test result
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		final List<File> javaFiles = folderContent.getFilesWithEndingPattern(".java");
		assertEquals("number java files", 2, javaFiles.size());
		assertFileContent("AA" + LINE_BREAK + "  CC", javaFiles);
	}	
	


}