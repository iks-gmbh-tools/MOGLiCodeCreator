package <domainPathToReplace>.validator.helper;

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

	protected String buildErrorMessage(final String validatedInstance) {
		final StringBuffer sb = new StringBuffer();
		
		if (validationErrors.size() == 1)
		{
			sb.append("A validation error exists for '" + validatedInstance + "':");
			sb.append(LINE_SEPARATOR);
			sb.append(validationErrors.get(0));
		}
		else if (validationErrors.size() > 1)
		{
			sb.append(validationErrors.size() + " validation errors exist for '" + validatedInstance + "':");
			sb.append(LINE_SEPARATOR);
			
			for (final String  errorMessage : validationErrors) {
				sb.append(errorMessage);
				sb.append(LINE_SEPARATOR);
			}			
		}
		
		return sb.toString().trim();
	}

}
