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
package com.iksgmbh.moglicc.infrastructure;

import java.io.File;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;

public class MOGLiLogger implements Logger {
	
	private File logfile;

	public MOGLiLogger(File logfile) {
		this.logfile = logfile;
	}

	public File getLogfile() {
		return logfile;
	}

	@Override
	public void logInfo(String message) {
		MOGLiLogUtil.logInfo(logfile, message);
	}
	

	@Override
	public void logWarning(String message) {
		MOGLiLogUtil.logWarning(logfile, message);
	}

	@Override
	public void logError(String message) {
		MOGLiLogUtil.logError(logfile, message);
	}

	@Override
	public void log(LOG_LEVEL level, String message) {
		if (LOG_LEVEL.INFO == level) {
			logInfo(message);
		} else if (LOG_LEVEL.WARNING == level) {
			logWarning(message);
		} else if (LOG_LEVEL.ERROR == level) {
			logError(message);
		} else {
			throw new MOGLiCoreException("Unknown log level");
		}

	}


}