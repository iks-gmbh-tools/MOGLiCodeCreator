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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

/**
 * Replaces lines or part of them in each file that match the file pattern within the defined folder.
 *
 * @author Reik Oberrath
 */
public class FolderContentBasedTextFileLineReplacer {

	private FolderContent folderContent;
	private List<String> errorList = new ArrayList<String>();

	private IOEncodingHelper encodingHelper = IOEncodingHelper.STANDARD;

	public FolderContentBasedTextFileLineReplacer(final File rootDir, final List<String> toIgnore) {
		folderContent = new FolderContent(rootDir, toIgnore);
	}

	public FolderContentBasedTextFileLineReplacer(final FolderContent folderContent) {
		this.folderContent = folderContent;
	}

	public void setEncodingHelper(IOEncodingHelper encodingHelper) {
		this.encodingHelper = encodingHelper;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public FolderContent getFolderContent() {
		return folderContent;
	}

	public void doYourJob(final List<ReplacementData> replacements) {
		for (final ReplacementData replacementData : replacements) {
			doYourJob(replacementData);
		}
	}

	public void doYourJob(final ReplacementData replacementData)
	{
		if (replacementData.wasReplacementPerformed()) {
			return;
		}

		final List<File> filesWithExtensions = folderContent.getFilesWithEndingPattern(replacementData.getFileEndingPattern());
		for (final File file : filesWithExtensions) {
			try {
				final List<String> fileContent = FileUtil.getFileContentAsList(file);
				final List<String> result = replace(fileContent, replacementData.getOldString(), replacementData.getNewString());
				FileUtil.createNewFileWithContent(encodingHelper, file, result);
				replacementData.addMatchingFile(file);
				replacementData.setReplacementPerformed(true);
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}
	}

	private List<String> replace(final List<String> lines, final String oldString, final String newString) {
		final List<String> toReturn = new ArrayList<String>();
		for (final String line : lines) {
			toReturn.add(StringUtils.replace(line, oldString, newString));
		}
		return toReturn;
	}

	public static class ReplacementData {

		private List<File> matchingFiles = new ArrayList<File>();
		private String oldString;
		private String newString;
		private String fileEndingPattern;
		private boolean replacementPerformed = false;

		public ReplacementData(final String oldString, final String newString) {
			this.oldString = oldString;
			this.newString = newString;
		}

		public ReplacementData(final String oldString, final String newString, final String fileEndingPattern) {
			this(oldString, newString);
			this.fileEndingPattern = fileEndingPattern;
		}

		public String getFileEndingPattern() {
			return fileEndingPattern;
		}

		public boolean wasReplacementPerformed() {
			return replacementPerformed;
		}

		public void setReplacementPerformed(final boolean replacementPerformed) {
			this.replacementPerformed = replacementPerformed ;
		}

		public String getNewString() {
			return newString;
		}

		public String getOldString() {
			return oldString;
		}

		public void addMatchingFile(final File f) {
			matchingFiles.add(f);
		}

		public List<File> getMatchingFiles() {
			Collections.sort(matchingFiles); 
			return matchingFiles;
		}
	}

}