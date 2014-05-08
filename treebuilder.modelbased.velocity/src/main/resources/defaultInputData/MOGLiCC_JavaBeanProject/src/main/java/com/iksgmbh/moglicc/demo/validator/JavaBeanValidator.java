package com.iksgmbh.moglicc.demo.validator;

import java.util.HashMap;
import java.util.List;

public class JavaBeanValidator {
	
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	protected List<String> validationErrors;
	protected HashMap<String, List<FieldValidator>> validators;

	protected void validateField(final String fieldName, Object value) {
		final List<FieldValidator> fieldValidators = validators.get(fieldName);
		for (final FieldValidator fieldValidator : fieldValidators) {
			final FieldValidationResult validationResult = fieldValidator.validateValue(value);
			if (validationResult != FieldValidator.STATUS_OK) {
				validationErrors.add(validationResult.getErrorMessage());
			}
		}
	}

	protected String buildErrorMessage() {
		final StringBuffer sb = new StringBuffer();
		for (final String  errorMessage : validationErrors) {
			sb.append(errorMessage);
			sb.append(LINE_SEPARATOR);
		}
		return sb.toString().trim();
	}

}
