package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants2;

public class MissingStarterclassException extends MOGLiCoreException2 {

	private static final long serialVersionUID = 1L;

	public MissingStarterclassException() {
		super(MOGLiTextConstants2.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
	}
}
