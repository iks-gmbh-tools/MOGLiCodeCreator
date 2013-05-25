package com.iksgmbh.helper;

import java.io.File;
import java.util.List;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

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

	public void duplicateTo(final File targetDir) {
		final List<File> folders = folderContent.getFolders();
		createTargetDirectories(targetDir, folders);
		createTargetFiles(targetDir, folders);
	}

	private void createTargetFiles(final File targetDir, final List<File> folders) {
		for (final File folder : folders) {
			final List<File> files = folderContent.getFilesFor(folder);
			for (final File file : files) {
				final String nameWithoutOldPath = cutOldPath(file);
				final File newFile = new File(targetDir.getAbsolutePath(), nameWithoutOldPath);
				FileUtil.copyBinaryFile(file, newFile);  // is also ok for text files
			}
		}
	}

	protected void createTargetDirectories(final File targetDir, final List<File> folders) {
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
