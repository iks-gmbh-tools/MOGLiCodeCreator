package com.iksgmbh.moglicc.exceptions;

/**
 * To be thrown only from code of plugin and code from common module that is used by plugins.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MOGLiPluginException extends Exception {

	private static final long serialVersionUID = -1;
	
	protected String pluginErrorMessage;

	public MOGLiPluginException(Exception e) {
		super(e);
	}

	public MOGLiPluginException(String message) {
		super(message);
		pluginErrorMessage = message;
	}

	public MOGLiPluginException(String message, Exception e) {
		super(message, e);
		pluginErrorMessage = message;
	}

	public String getPluginErrorMessage() {
		return pluginErrorMessage;
	}
	
}