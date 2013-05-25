package com.iksgmbh.moglicc.demo.validator.types;

import com.iksgmbh.moglicc.demo.validator.FieldValidationResult;
import com.iksgmbh.moglicc.demo.validator.FieldValidationResult.Status;
import com.iksgmbh.moglicc.demo.validator.FieldValidator;

public class MaxLengthValidator extends FieldValidator {
	
	private Integer maxLength;
	private Class<?> expectedType;

	public MaxLengthValidator(final String fieldName, final Integer maxLength) {
		this(fieldName, maxLength, String.class);
	}
	
	public MaxLengthValidator(final String fieldName, final Integer maxLength, final Class<?> expectedType) {
		this.fieldName = fieldName;
		this.maxLength = maxLength;
		this.expectedType = expectedType;
	}

	@Override
	public FieldValidationResult validateValue(final Object value) {
		if (value == null) {
			return STATUS_OK; // nothing to validate
		}
		
		if (expectedType.isInstance(value)) {
			int actualLength = value.toString().length();
			if (actualLength > maxLength) {
				return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
						                                       "Max Length for field '" + 
			                                                   fieldName + "' (" + maxLength + ") exceeded: " + actualLength);
			}
		} else {
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " +  
                    									  "Unexpected java type for value in field '" + fieldName + 
					                                       "'. Expected '" + Integer.class + "' - Actual '" + value.getClass() + "'");
		}		
		
		return STATUS_OK;
	}

}
