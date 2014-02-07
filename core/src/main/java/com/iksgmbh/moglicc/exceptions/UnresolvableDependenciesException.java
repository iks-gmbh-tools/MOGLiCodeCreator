package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants;


public class UnresolvableDependenciesException extends MOGLiCoreException {

	private static final long serialVersionUID = 1L;

	public UnresolvableDependenciesException() {
		super(MOGLiTextConstants.TEXT_UNRESOLVABLE_DEPENDENCIES);
	}
}
