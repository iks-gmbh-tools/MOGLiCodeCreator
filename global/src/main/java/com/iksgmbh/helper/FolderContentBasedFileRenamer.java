package com.iksgmbh.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

/**
 * Renames files that match a given filename within the defined folder.
 *
 * @author Reik Oberrath
 */
public class FolderContentBasedFileRenamer {

	private static final String SLASHDOT_SAVER = "|slashdot|";
	private FolderContent folderContent;
	private List<String> errorList = new ArrayList<String>();
	private boolean reinitFolderStructureNotwendig = false;

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
		performRenaming(renaming);
		if (reinitFolderStructureNotwendig) {
			folderContent.refresh();
		}
	}

	private void performRenaming(final RenamingData renaming) {
		if (renaming.getOldName().equals(renaming.getNewName()))  {
			errorList.add("File '" + renaming.getOldName() + "' is not renamed to a new name!");
		} else if (renaming.isPath()) {
			renameDirectory(renaming);
			reinitFolderStructureNotwendig = true;
		} else {
			renameFile(renaming);
		}
	}

	private void renameFile(final RenamingData renaming) 
	{
		final List<File> filesWithExtensions = folderContent.getFilesWithEndingPattern(renaming.getOldName());
		
		for (final File oldFile : filesWithExtensions) {
			try {
				final File newFile = new File(oldFile.getParentFile(), renaming.getNewName());
				final boolean renamed = oldFile.renameTo(newFile);
				if (renamed) {
					renaming.getRenamingResults().add("File '" + oldFile.getName() + "' renamed to '" + newFile.getName()
							                      + "' in directory '" + oldFile.getParent() + "'");
				} else {
					errorList.add("Could not rename " + oldFile.getName() + " to " + newFile.getName() + " in " + oldFile.getParent());
				}
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}
	}

	private void renameDirectory(final RenamingData renaming) {
		final File oldDir = folderContent.getFolder(renaming.getOldName());
		if (oldDir == null) {
			throw new RuntimeException("Cannot rename unkown directory: " + renaming.getOldName());
		}
		final String oldDirName = oldDir.getAbsolutePath();
		final String replacement = replaceSeparators(renaming.getNewName());
		final String newDirName = StringUtils.replace(oldDirName, renaming.getOldName(), replacement);
		final File newDir = new File(newDirName);
		final List<String> takeAllFiles = null; // exclude no files ! 
		final FolderContentBasedFolderDuplicator folderDuplicator = new FolderContentBasedFolderDuplicator(oldDir, takeAllFiles);
		folderDuplicator.duplicateTo(newDir);
		FileUtil.deleteDirWithContent(oldDir);
		renaming.getRenamingResults().add("Directory '" + oldDir.getName() + "' renamed to '" + newDir.getName()
                + "' in directory '" + oldDir.getParent() + "'");

	}

	private String replaceSeparators(final String newName) {
		boolean startsWithDot = false;
		String toReturn = newName;
		if (newName.startsWith(".")) {
			toReturn = toReturn.substring(1);
			startsWithDot = true;
		}
		
		//toReturn = toReturn.replace('/', '\\');
		toReturn = StringUtils.replace(toReturn, "/.", SLASHDOT_SAVER);
		//toReturn = toReturn.replace('.', '\\');
		toReturn = StringUtils.replace(toReturn, SLASHDOT_SAVER, "\\.");
		
		if (startsWithDot) {
			return "." + toReturn;
		}
		return toReturn;
	}

	public static class RenamingData {
		private List<String> renamingResults = new ArrayList<String>();
		private String oldName;
		private String newName;
		private boolean isPath;

		public RenamingData(final String oldName, final String newName) {
			this.oldName = oldName;
			this.newName = newName;
		}

		public RenamingData(final String oldName, final String newName, final boolean isDir) {
			this(oldName, newName);
			this.isPath = isDir;
		}

		public String getNewName() {
			return newName;
		}

		public String getOldName() {
			return oldName;
		}

		public List<String> getRenamingResults() {
			Collections.sort(renamingResults);
			return renamingResults;
		}

		public boolean isPath() {
			return isPath;
		}
	}

}
