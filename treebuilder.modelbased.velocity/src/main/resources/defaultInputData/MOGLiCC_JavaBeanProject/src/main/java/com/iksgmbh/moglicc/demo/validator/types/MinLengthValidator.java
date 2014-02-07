package com.iksgmbh.moglicc.demo.validator.types;

import com.iksgmbh.moglicc.demo.validator.FieldValidationResult;
import com.iksgmbh.moglicc.demo.validator.FieldValidationResult.Status;
import com.iksgmbh.moglicc.demo.validator.FieldValidator;


public class MinLengthValidator extends FieldValidator {

	private Integer minLength;
	private Class<?> expectedType;

	public MinLengthValidator(final String fieldName, final Integer maxLength) {
		this(fieldName, maxLength, String.class);
	}

	public MinLengthValidator(final String fieldName, final Integer minLength, final Class<?> expectedType) {
		this.fieldName = fieldName;
		this.minLength = minLength;
		this.expectedType = expectedType; 
	}

	@Override
	public FieldValidationResult validateValue(final Object value) {
		if (value == null) {
			return STATUS_OK; // nothing to validate
		}
		
		if (expectedType.isInstance(value)) {
			int actualLength = value.toString().length();
			if (actualLength < minLength) {
				return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
						                                       "Minimum length for field '" + fieldName +  
						                                       "' (" + minLength + ") not reached: " + actualLength);
			}
		} else {
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
					                                       "Unexpected java type for value in field '" + fieldName +  
					                                       "'. Expected '" + expectedType.getName() + "' - Actual '" +  
					                                       value.getClass() + "'");
		}		
		
		return STATUS_OK;
	}
}
