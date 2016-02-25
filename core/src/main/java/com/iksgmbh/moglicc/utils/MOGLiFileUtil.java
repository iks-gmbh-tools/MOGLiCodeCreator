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
	
	public static void createNewFileWithContent(final File file, final String content) {
		try {
			FileUtil.createNewFileWithContent(file, content);
		} catch (Exception e) {
			throw new MOGLiCoreException("Error creating file " + file.getAbsolutePath(), e);
		}
	}	
}