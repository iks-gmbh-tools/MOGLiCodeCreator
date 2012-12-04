package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants;

public class MissingStarterclassException extends MOGLiCoreException {

	private static final long serialVersionUID = 1L;

	public MissingStarterclassException() {
		super(MOGLiTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
	}
}
