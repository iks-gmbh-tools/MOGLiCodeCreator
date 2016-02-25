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
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.FileUtil.FileCreationStatus;

/**
 * Duplicates a file structure read by the {@link FolderContent} functionality.
 * 
 * @author Reik Oberrath
 */
public class FolderContentBasedFolderDuplicator {
	
	private FolderContent folderContent;

	final int lengthOfAbsolutePathOfSourceDir;

	public FolderContentBasedFolderDuplicator(final File sourceDir, final List<String> toIgnore) {
		this.lengthOfAbsolutePathOfSourceDir = sourceDir.getAbsolutePath().length();
		folderContent = new FolderContent(sourceDir, toIgnore);
	}

	public FolderContent getFolderContent() {
		return folderContent;
	}

	public HashMap<String, FileCreationStatus> duplicateTo(final File targetDir) {
		return duplicateTo(targetDir, false);
	}

	public HashMap<String, FileCreationStatus> duplicateTo(final File targetDir, final boolean preserveFiles) {
		final List<File> folders = folderContent.getFolders();
		createTargetDirectories(targetDir, folders);
		return createTargetFiles(targetDir, folders, preserveFiles);
	}

	private HashMap<String, FileCreationStatus> createTargetFiles(final File targetDir, 
			                                                      final List<File> folders,
			                                                      final boolean preserveFiles)
	{
		final HashMap<String, FileCreationStatus> result = new HashMap<String, FileCreationStatus>();
		for (final File folder : folders) {
			final List<File> files = folderContent.getFilesFor(folder);
			for (final File file : files) {
				final String nameWithoutOldPath = cutOldPath(file);
				final File newFile = new File(targetDir.getAbsolutePath(), nameWithoutOldPath);
				if (newFile.exists()) {
					if (preserveFiles) {
						result.put(newFile.getAbsolutePath(), FileCreationStatus.EXISTING_FILE_PRESERVED);
					} else {
						FileUtil.copyBinaryFile(file, newFile);  // is also ok for text files
						result.put(newFile.getAbsolutePath(), FileCreationStatus.EXISTING_FILE_OVERWRITTEN);
					}
				} else {
					FileUtil.copyBinaryFile(file, newFile);  // is also ok for text files
					result.put(newFile.getAbsolutePath(), FileCreationStatus.NOT_EXISTING_FILE_CREATED);
				}
			}
		}
		return result;
	}

	protected void createTargetDirectories(final File targetDir, 
			                               final List<File> folders) 
	{
		for (final File folder : folders) {
			final String nameWithoutOldPath = cutOldPath(folder);
			final File newFolder = new File(targetDir.getAbsolutePath(), nameWithoutOldPath);
			if (! newFolder.exists()) {
				boolean created = newFolder.mkdirs();
				if (! created) {				
					System.err.println("Could not create " + newFolder.getAbsolutePath());
				}
			}
		}
	}

	protected String cutOldPath(final File folder) {
		String toReturn = folder.getAbsolutePath().substring(lengthOfAbsolutePathOfSourceDir);
		while (toReturn.startsWith("/") || toReturn.startsWith("\\") ) {
			toReturn = toReturn.substring(1);
		}
		return toReturn;
	}

}