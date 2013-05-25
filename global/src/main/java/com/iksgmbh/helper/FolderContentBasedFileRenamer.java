package com.iksgmbh.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.data.FolderContent;

/**
 * Renames files that match a given filename within the defined folder.
 * 
 * @author Reik Oberrath
 */
public class FolderContentBasedFileRenamer {
	
	private FolderContent folderContent;
	private List<String> errorList = new ArrayList<String>();

	public FolderContentBasedFileRenamer(final File rootDir, final List<String> toIgnore) {
		folderContent = new FolderContent(rootDir, toIgnore);
	}

	public FolderContentBasedFileRenamer(final FolderContent folderContent) {
		this.folderContent = folderContent;
	}

	public void doYourJob(final List<RenamingData> renamingData) {
		for (final RenamingData renaming : renamingData) {
			doYourJob(renaming);
		}
	}

	public FolderContent getFolderContent() {
		return folderContent;
	}


	public List<String> getErrorList() {
		return errorList;
	}

	public void doYourJob(final RenamingData renaming) 
	{
		if (renaming.oldName.equals(renaming.newName))  {
			errorList.add("File '" + renaming.getOldName() + "' is not renamed to a new name!");
		} else {
			final List<File> filesWithExtensions = folderContent.getFilesWithEndingPattern(renaming.oldName);
			for (final File oldFile : filesWithExtensions) {
				try {
					final File newFile = new File(oldFile.getParentFile(), renaming.newName);
					final boolean renamed = oldFile.renameTo(newFile);
					if (renamed) {
						renaming.renamingResults.add("'" + oldFile.getName() + "' renamed to '" + newFile.getName() + "' in " + oldFile.getParent());
					} else {
						errorList.add("Could not rename " + oldFile.getName() + " to " + newFile.getName() + " in " + oldFile.getParent());
					}
				} catch (Exception e) {
					errorList.add(e.getMessage());
				}
			}
		}
	}

	public static class RenamingData {
		private List<String> renamingResults = new ArrayList<String>();
		private String oldName;
		private String newName;

		public RenamingData(final String oldName, final String newName) {
			this.oldName = oldName;
			this.newName = newName;
		}

		public String getNewName() {
			return newName;
		}

		public String getOldName() {
			return oldName;
		}

		public List<String> getRenamingResults() {
			return renamingResults;
		}
	}

}
