package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants;

public class MissingManifestException extends MOGLiCoreException {

	private static final long serialVersionUID = 1L;

    public MissingManifestException() {
		super(MOGLiTextConstants.TEXT_NO_MANIFEST_FOUND);
	}
}
