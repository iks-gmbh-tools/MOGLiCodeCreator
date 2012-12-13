package com.iksgmbh.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Data object to access files and subfolders of a given rootDir.
 *  
 * @author Reik Oberrath
 */
public class FolderContent {
	
	private File rootDir;
	private List<String> toIgnore;
	private List<File> directories = new ArrayList<File>();
	private HashMap<File, List<File>> content = new HashMap<File, List<File>>();
	
	/**
	 * @param rootDir
	 * @param toIgnore names of files and subFolders to ignore (must equals exactly)
	 */
	public FolderContent(final File rootDir, final List<String> toIgnore) {
		if (rootDir == null) {
			throw new RuntimeException("Argument 'mainDir' not set!");
		}
		if (! rootDir.exists()) {
			throw new RuntimeException("Folder '" + rootDir + "'  does not exist!");
		}
		
		this.rootDir = rootDir;
		
		if (toIgnore == null) {
			this.toIgnore = new ArrayList<String>();
		} else {
			this.toIgnore = toIgnore;
		}
		
		analyseContent(this.rootDir);
	}

	private void analyseContent(final File dir) {
		final List<File> subFolders = new ArrayList<File>();
		final List<File> files = new ArrayList<File>();
		
		final File[] listFiles = dir.listFiles();
		for (final File file : listFiles) {
			if (! isFileToIgnore(file)) {
				if (file.isDirectory()) {
					subFolders.add(file);
				} else {
					files.add(file);
				}
			}
		}
		
		content.put(dir, files);
		directories.add(dir);
		
		for (final File subFolder : subFolders) {
			analyseContent(subFolder);
		}		
	}

	protected boolean isFileToIgnore(final File file) {
		for (final String ignore : toIgnore) {
			if (file.getName().equals(ignore)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return all files of all subFolders and rootDir with the given file extension
	 */
	public List<File> getFilesWithExtensions(final String extension) {
		final List<File> toReturn = new ArrayList<File>();
		for (final File dir : directories) {
			final List<File> list = content.get(dir);
			for (final File file : list) {
				if (extension == null || file.getName().endsWith("." + extension)) {
					toReturn.add(file);
				}
			}
		}
		return toReturn;
	}

	/**
	 * @return all folders including rootDir
	 */
	public List<File> getFolders() {
		final List<File> toReturn = new ArrayList<File>();
		for (final File dir : directories) {
			toReturn.add(dir);
		}
		return toReturn;
	}

	/**
	 * @return all files of all subFolders and rootDir
	 */
	public List<File> getFiles() {
		return getFilesWithExtensions(null);
	}

	public List<File> getFilesFor(final File folder) {
		return content.get(folder);
	}

}