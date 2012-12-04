package com.iksgmbh.moglicc.exceptions;

import com.iksgmbh.moglicc.MOGLiTextConstants2;

public class MissingManifestException extends MOGLiCoreException2 {

	private static final long serialVersionUID = 1L;

    public MissingManifestException() {
		super(MOGLiTextConstants2.TEXT_NO_MANIFEST_FOUND);
	}
}
