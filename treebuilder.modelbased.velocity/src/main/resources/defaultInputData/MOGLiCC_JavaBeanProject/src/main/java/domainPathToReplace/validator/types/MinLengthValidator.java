package <domainPathToReplace>.validator.types;

import <domainPathToReplace>.validator.helper.FieldValidationResult;
import <domainPathToReplace>.validator.helper.FieldValidationResult.Status;
import <domainPathToReplace>.validator.helper.FieldValidator;


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
			if (value.toString().length() < minLength) {
				return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
						                                       "Minimum length for field '" + fieldName +  
						                                       "' (" + minLength + ") is not reached by value '" + value + "'");
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
