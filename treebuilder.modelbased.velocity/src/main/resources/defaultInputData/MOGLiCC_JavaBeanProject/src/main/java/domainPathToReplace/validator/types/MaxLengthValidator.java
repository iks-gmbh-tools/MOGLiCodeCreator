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