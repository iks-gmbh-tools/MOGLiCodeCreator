package com.iksgmbh.moglicc.core;

public interface Logger {

	enum LOG_LEVEL { INFO, WARNING, ERROR };
	
	void logInfo(String message);
	void logWarning(String message);
	void logError(String message);
	
	void log(LOG_LEVEL level, String message);
}
