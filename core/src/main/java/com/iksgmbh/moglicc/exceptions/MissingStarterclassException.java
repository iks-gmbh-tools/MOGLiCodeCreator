package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MogliTextConstants;

public class MissingStarterclassException extends MogliCoreException {

	private static final long serialVersionUID = 1L;

	public MissingStarterclassException() {
		super(MogliTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE);
	}
}
