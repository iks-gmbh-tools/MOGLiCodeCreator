package com.iksgmbh.moglicc.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.utils.FileUtil;

public class MogliFileUtil {
	
	static final char COMMENT_INDICATOR = '#';

	public static String getFileContent(String filename) {
		try {
			return FileUtil.getFileContent(MogliFileUtil.getNewFileInstance(filename));
		} catch (IOException e) {
			throw new MogliCoreException("Error reading file " + filename, e);
		}
	}
	
	public static String getFileContent(File file) {
		try {
			return FileUtil.getFileContent(file);
		} catch (IOException e) {
			throw new MogliCoreException("Error reading file " + file.getAbsolutePath(), e);
		}
	}
	
	public static List<String> getFileContentAsList(File file) {
		try {
			return FileUtil.getFileContentAsList(file);
		} catch (IOException e) {
			throw new MogliCoreException("Error reading file " + file.getAbsolutePath(), e);
		}
	}
	
	
	public static void appendToFile(File file, String text) {
		try {
			FileUtil.appendToFile(file, text);
		} catch (IOException e) {
			throw new MogliCoreException("Error appending to file " + file.getAbsolutePath(), e);
			}
	}
	
	/**
	 * Use this method to create a file in the ApplicationRootDir!
	 * @param filename
	 * @return file
	 */
	public static File getNewFileInstance(String filename) {
		return new File(MogliCodeCreator.getApplicationRootDir() + "/" + filename);
	}

}
