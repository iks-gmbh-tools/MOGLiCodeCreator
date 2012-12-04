package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants2;


public class UnresolvableDependenciesException extends MOGLiCoreException2 {

	private static final long serialVersionUID = 1L;

	public UnresolvableDependenciesException() {
		super(MOGLiTextConstants2.TEXT_UNRESOLVABLE_DEPENDENCIES);
	}
}
