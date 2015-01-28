package <domainPathToReplace>.validator.types;

import <domainPathToReplace>.validator.helper.FieldValidationResult;
import <domainPathToReplace>.validator.helper.FieldValidationResult.Status;
import <domainPathToReplace>.validator.helper.FieldValidator;

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
			                                                   fieldName + "' (" + maxLength + ") is exceeded by current value '" + value + "'.");
			}
		} else {
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " +  
                    									  "Unexpected java type for value in field '" + fieldName + 
					                                       "'. Expected '" + Integer.class + "' - Actual '" + value.getClass() + "'");
		}		
		
		return STATUS_OK;
	}

}
