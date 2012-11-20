package com.iksgmbh.moglicc.infrastructure;

import java.io.File;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.utils.MogliLogUtil;

public class MogliLogger implements Logger {
	
	private File logfile;

	public MogliLogger(File logfile) {
		this.logfile = logfile;
	}

	public File getLogfile() {
		return logfile;
	}

	@Override
	public void logInfo(String message) {
		MogliLogUtil.logInfo(logfile, message);
	}
	

	@Override
	public void logWarning(String message) {
		MogliLogUtil.logWarning(logfile, message);
	}

	@Override
	public void logError(String message) {
		MogliLogUtil.logError(logfile, message);
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
			throw new MogliCoreException("Unknown log level");
		}

	}


}
