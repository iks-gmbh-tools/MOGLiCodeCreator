/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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