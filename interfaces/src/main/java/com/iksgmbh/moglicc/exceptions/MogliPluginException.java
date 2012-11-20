package com.iksgmbh.moglicc.exceptions;

/**
 * To be thrown only from code of plugin and code from common module that is used by plugins.
 * 
 * @author Reik Oberrath
 */
public class MogliPluginException extends Exception {

	private static final long serialVersionUID = -1;
	
	protected String pluginErrorMessage;

	public MogliPluginException(Exception e) {
		super(e);
	}

	public MogliPluginException(String message) {
		super(message);
		pluginErrorMessage = message;
	}

	public MogliPluginException(String message, Exception e) {
		super(message, e);
		pluginErrorMessage = message;
	}

	public String getPluginErrorMessage() {
		return pluginErrorMessage;
	}
	
}
