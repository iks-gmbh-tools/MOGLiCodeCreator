package com.iksgmbh.moglicc.infrastructure;

import java.io.File;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException2;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil2;

public class MOGLiLogger2 implements Logger {
	
	private File logfile;

	public MOGLiLogger2(File logfile) {
		this.logfile = logfile;
	}

	public File getLogfile() {
		return logfile;
	}

	@Override
	public void logInfo(String message) {
		MOGLiLogUtil2.logInfo(logfile, message);
	}
	

	@Override
	public void logWarning(String message) {
		MOGLiLogUtil2.logWarning(logfile, message);
	}

	@Override
	public void logError(String message) {
		MOGLiLogUtil2.logError(logfile, message);
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
			throw new MOGLiCoreException2("Unknown log level");
		}

	}


}
