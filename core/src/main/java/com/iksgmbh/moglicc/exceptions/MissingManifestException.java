package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MogliTextConstants;

public class MissingManifestException extends MogliCoreException {

	private static final long serialVersionUID = 1L;

    public MissingManifestException() {
		super(MogliTextConstants.TEXT_NO_MANIFEST_FOUND);
	}
}
