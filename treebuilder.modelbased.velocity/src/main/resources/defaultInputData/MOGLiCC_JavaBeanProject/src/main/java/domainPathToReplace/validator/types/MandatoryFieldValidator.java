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