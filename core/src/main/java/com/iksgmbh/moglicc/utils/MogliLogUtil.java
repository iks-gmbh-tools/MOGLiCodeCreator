package com.iksgmbh.moglicc.utils;

import java.io.File;
import java.io.IOException;

import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.utils.FileUtil;

public class MogliLogUtil {

	private static File coreLogfile;
	
	public static void setCoreLogfile(File lf) {
		coreLogfile = lf;
	}

	public static File getCoreLogfile() {
		return coreLogfile;
	}

	public static File createNewLogfile(String filename) {
		coreLogfile = MogliFileUtil.getNewFileInstance(filename);
		if (coreLogfile.exists()) {
			if (! coreLogfile.delete()) {
				throw new MogliCoreException("Error deleting " + coreLogfile.getAbsolutePath());
			}
		}
		try {
			coreLogfile.createNewFile();
		} catch (IOException e) {
			throw new MogliCoreException("Error creating " + coreLogfile.getAbsolutePath() + coreLogfile.exists(), e);
		}
		System.out.println("Logfile created!");	
		return coreLogfile;
	}
	
	public static void logInfo(String message) {
		logInfo(coreLogfile, message);
	}

	public static void logInfo(File currentLogfile, String message) {
		if (currentLogfile == null) {
			throw new MogliCoreException("Method createNewLogfile not called!");
		}
		try {
			FileUtil.appendToFile(currentLogfile, message);
		} catch (IOException e) {
			throw new MogliCoreException("Error writer to logfile " + currentLogfile.getAbsolutePath(), e);
		}
		System.out.println(message);
	}
	
	public static void logWarning(String message) {
		logWarning(coreLogfile, message);
	}
	
	public static void logWarning(File currentLogfile, String message) {
		logInfo(currentLogfile, "Warning: " + message);
	}
	

	public static void logError(String message) {
		logError(coreLogfile, message);
	}
	
	public static void logError(File currentLogfile, String message) {
		logInfo(currentLogfile, "ERROR: " + message);
	}

}
