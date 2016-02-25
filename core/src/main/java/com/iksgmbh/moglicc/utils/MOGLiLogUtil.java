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

import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.utils.FileUtil;

public class MOGLiLogUtil {

	private static File coreLogfile;
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public static void setCoreLogfile(final File lf) {
		coreLogfile = lf;
	}

	public static File getCoreLogfile() {
		return coreLogfile;
	}

	public static File createNewLogfile(final File file) {
		coreLogfile = file;
		if (coreLogfile.exists()) {
			if (! coreLogfile.delete()) {
				throw new MOGLiCoreException("Error deleting " + coreLogfile.getAbsolutePath());
			}
		}
		try {
			coreLogfile.createNewFile();
		} catch (IOException e) {
			throw new MOGLiCoreException("Error creating " + coreLogfile.getAbsolutePath() + coreLogfile.exists(), e);
		}
		System.out.println("Logfile created!");	
		return coreLogfile;
	}
	
	public static void logInfo(final String message) {
		logInfo(coreLogfile, message);
	}

	public static void logInfo(final File currentLogfile, final String message) {
		if (currentLogfile == null) {
			throw new MOGLiCoreException("Method createNewLogfile not called!");
		}
		
		try {
			FileUtil.appendToFile(currentLogfile, message);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error writer to logfile " + currentLogfile.getAbsolutePath(), e);
		}
		System.out.println(message);
	}
	
	public static void logWarning(final String message) {
		logWarning(coreLogfile, message);
	}
	
	public static void logWarning(final File currentLogfile, final String message) {
		logInfo(currentLogfile, "Warning: " + message);
	}
	

	public static void logError(final String message) {
		logError(coreLogfile, message);
	}
	
	public static void logError(final File currentLogfile, final String message) {
		logInfo(currentLogfile, "ERROR: " + message);
	}

}