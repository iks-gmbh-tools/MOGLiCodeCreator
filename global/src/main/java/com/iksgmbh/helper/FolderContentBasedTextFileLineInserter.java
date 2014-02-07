package com.iksgmbh.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

/**
 * Inserts in each file found by the {@link FolderContent}-functionality a block of text, typically a single line.
 * @author Reik Oberrath
 */
public class FolderContentBasedTextFileLineInserter {
	
	private FolderContent folderContent;
	private String fileExtension;
	private String lineMarkerToInsertAfter;
	private List<String> errorList = new ArrayList<String>();
	private IOEncodingHelper encodingHelper = IOEncodingHelper.STANDARD;

	public FolderContentBasedTextFileLineInserter(final File rootDir, final List<String> toIgnore) {
		folderContent = new FolderContent(rootDir, toIgnore);
	}

	public FolderContentBasedTextFileLineInserter(final FolderContent folderContent) {
		this.folderContent = folderContent;
	}

	void setFileExtension(final String fileExtension) {
		this.fileExtension = fileExtension;
	}

	void setLineMarkerToInsertAfter(String lineMarkerToInsertAfter) {
		this.lineMarkerToInsertAfter = lineMarkerToInsertAfter;
	}

	public void setEncodingHelper(IOEncodingHelper encodingHelper) {
		this.encodingHelper = encodingHelper;
	}

	public void insert(final String text) {
		checkPreconditions();
		
		final List<File> filesWithExtensions = folderContent.getFilesWithEndingPattern(fileExtension);
		for (final File file : filesWithExtensions) {
			insert(text, file);
		}
		
		if (errorList.size() > 0) {
			final StringBuffer sb = new StringBuffer();
			for (final String msg : errorList) {
				sb.append(msg).append(FileUtil.getSystemLineSeparator());
			}
			throw new RuntimeException(sb.toString().trim());
		}
	}

	private void checkPreconditions() {
		if (fileExtension == null) {
			throw new RuntimeException("Parameter 'fileExtension' not defined.");
		}
		if (lineMarkerToInsertAfter == null) {
			throw new RuntimeException("Parameter 'lineMarkerToInsertAfter' not defined.");
		}
	}

	private void insert(final String text, final File file) {
		final List<String> fileContentAsList;
		try {
			fileContentAsList = FileUtil.getFileContentAsList(file);
		} catch (IOException e1) {
			errorList.add("Error reading " + file.getAbsolutePath());
			return;
		}
		final List<String> newContent = new ArrayList<String>();
		int matches = 0;
		
		for (final String line : fileContentAsList) {
			newContent.add(line);
			if (line.equals(lineMarkerToInsertAfter)) {
				matches++;
				newContent.add(text);
			} 
		}
		
		if (matches > 0) {
			try {
				FileUtil.createNewFileWithContent(encodingHelper, file, newContent);
			} catch (Exception e) {
				errorList.add("Error writing " + file.getAbsolutePath());
				return;
			}
			System.out.println(matches + " insertions made in " + file.getAbsolutePath());
		}
	}
	
	public FolderContent getFolderContent() {
		return folderContent;
	}

}
