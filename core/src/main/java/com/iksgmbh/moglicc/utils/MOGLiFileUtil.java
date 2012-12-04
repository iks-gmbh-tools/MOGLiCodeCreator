package com.iksgmbh.moglicc.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.utils.FileUtil;

public class MOGLiFileUtil {
	
	static final char COMMENT_INDICATOR = '#';

	public static String getFileContent(final String filename) {
		try {
			return FileUtil.getFileContent(MOGLiFileUtil.getNewFileInstance(filename));
		} catch (IOException e) {
			throw new MOGLiCoreException("Error reading file " + filename, e);
		}
	}
	
	public static String getFileContent(final File file) {
		try {
			return FileUtil.getFileContent(file);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error reading file " + file.getAbsolutePath(), e);
		}
	}
	
	public static List<String> getFileContentAsList(final File file) {
		try {
			return FileUtil.getFileContentAsList(file);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error reading file " + file.getAbsolutePath(), e);
		}
	}
	
	public static void appendToFile(final File file, final String text) {
		try {
			FileUtil.appendToFile(file, text);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error appending to file " + file.getAbsolutePath(), e);
		}
	}

	
	/**
	 * Use this method to create a file in the ApplicationRootDir!
	 * @param filename
	 * @return file
	 */
	public static File getNewFileInstance(final String filename) {
		return new File(MOGLiCodeCreator.getApplicationRootDir() + "/" + filename);
	}
	
	public static void createFileWithContent(final File file, final String content) {
		try {
			FileUtil.createFileWithContent(file, content);
		} catch (Exception e) {
			throw new MOGLiCoreException("Error creating file " + file.getAbsolutePath(), e);
		}
	}

	public static void createNewFileWithContent(final File file, final String content) {
		try {
			FileUtil.createNewFileWithContent(file, content);
		} catch (Exception e) {
			throw new MOGLiCoreException("Error creating file " + file.getAbsolutePath(), e);
		}
	}	
}
