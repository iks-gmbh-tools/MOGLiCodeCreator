package com.iksgmbh.moglicc.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.iksgmbh.utils.FileUtil;

public class TestUtil {
	
	public static String getFileContent(final File file) {
		try {
			return FileUtil.getFileContent(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file " + file.getName(), e);
		}	
	}
	
	public static List<String> getFileContentAsList(final File file) {
		try {
			return FileUtil.getFileContentAsList(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file " + file.getName(), e);
		}	
	}
}
