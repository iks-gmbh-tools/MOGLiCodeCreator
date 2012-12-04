package com.iksgmbh.moglicc.exceptions;

public class MetaInfoValidatorException extends MOGLiPluginException2 {

	private static final long serialVersionUID = -1;
	
	public MetaInfoValidatorException(Exception e) {
		super(e);
	}

	public MetaInfoValidatorException(String message) {
		super(message);
	}

	public MetaInfoValidatorException(String message, Exception e) {
		super(message, e);
	}

	public String getValidatorErrorMessage() {
		return pluginErrorMessage;
	}
	
}
