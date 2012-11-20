package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MogliTextConstants;


public class UnresolvableDependenciesException extends MogliCoreException {

	private static final long serialVersionUID = 1L;

	public UnresolvableDependenciesException() {
		super(MogliTextConstants.TEXT_UNRESOLVABLE_DEPENDENCIES);
	}
}
