package com.iksgmbh.moglicc.exceptions;

/**
 * To be thrown only from code of plugin and code from common module that is used by plugins.
 * 
 * @author Reik Oberrath
 */
public class MOGLiPluginException2 extends Exception {

	private static final long serialVersionUID = -1;
	
	protected String pluginErrorMessage;

	public MOGLiPluginException2(Exception e) {
		super(e);
	}

	public MOGLiPluginException2(String message) {
		super(message);
		pluginErrorMessage = message;
	}

	public MOGLiPluginException2(String message, Exception e) {
		super(message, e);
		pluginErrorMessage = message;
	}

	public String getPluginErrorMessage() {
		return pluginErrorMessage;
	}
	
}
