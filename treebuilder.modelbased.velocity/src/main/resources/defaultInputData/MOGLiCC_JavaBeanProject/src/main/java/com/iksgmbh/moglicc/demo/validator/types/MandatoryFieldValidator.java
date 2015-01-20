package com.iksgmbh.moglicc.demo.validator.types;

import com.iksgmbh.moglicc.demo.validator.helper.FieldValidationResult;
import com.iksgmbh.moglicc.demo.validator.helper.FieldValidationResult.Status;
import com.iksgmbh.moglicc.demo.validator.helper.FieldValidator;

public class MandatoryFieldValidator extends FieldValidator {

	private boolean mandatory;

	public MandatoryFieldValidator(final String fieldName, final boolean mandatory) {
		this.fieldName = fieldName;
		this.mandatory = mandatory;
	}

	@Override
	public FieldValidationResult validateValue(final Object value) {
		if (value == null && mandatory) {
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
					                                       "Mandatory field '" + fieldName + "' has no value.");
		}
			
		return STATUS_OK;
	}
}
