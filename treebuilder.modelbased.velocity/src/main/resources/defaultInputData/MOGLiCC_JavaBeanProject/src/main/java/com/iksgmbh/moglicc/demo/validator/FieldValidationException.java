package com.iksgmbh.moglicc.demo.validator;

public class FieldValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FieldValidationException(final String errorMessage) {
		super(errorMessage);
	}

}
