package <domainPathToReplace>.validator.types;

import <domainPathToReplace>.validator.helper.FieldValidationResult;
import <domainPathToReplace>.validator.helper.FieldValidationResult.Status;
import <domainPathToReplace>.validator.helper.FieldValidator;

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
