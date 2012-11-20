package com.iksgmbh.moglicc.exceptions;

public class MogliCoreException extends RuntimeException {

	private static final long serialVersionUID = -1;

	public MogliCoreException(Exception e) {
		super(e);
	}

	public MogliCoreException(String message) {
		super(message);
	}

	public MogliCoreException(String message, Exception e) {
		super(message, e);
	}
}
