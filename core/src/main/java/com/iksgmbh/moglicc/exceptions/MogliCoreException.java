package com.iksgmbh.moglicc.exceptions;

public class MOGLiCoreException extends RuntimeException {

	private static final long serialVersionUID = -1;

	public MOGLiCoreException(Exception e) {
		super(e);
	}

	public MOGLiCoreException(String message) {
		super(message);
	}

	public MOGLiCoreException(String message, Exception e) {
		super(message, e);
	}
}
