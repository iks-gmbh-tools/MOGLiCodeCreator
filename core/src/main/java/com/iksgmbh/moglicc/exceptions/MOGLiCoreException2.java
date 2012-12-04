package com.iksgmbh.moglicc.exceptions;

public class MOGLiCoreException2 extends RuntimeException {

	private static final long serialVersionUID = -1;

	public MOGLiCoreException2(Exception e) {
		super(e);
	}

	public MOGLiCoreException2(String message) {
		super(message);
	}

	public MOGLiCoreException2(String message, Exception e) {
		super(message, e);
	}
}
