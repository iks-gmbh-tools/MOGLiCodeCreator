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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharSet;

import <domainPathToReplace>.validator.helper.FieldValidationResult;
import <domainPathToReplace>.validator.helper.FieldValidationResult.Status;
import <domainPathToReplace>.validator.helper.FieldValidator;

public class ValidCharFieldValidator extends FieldValidator {

	private CharSet validChars;

	public ValidCharFieldValidator(final String fieldName, final String validChars) {
		this.fieldName = fieldName;
		this.validChars = CharSet.getInstance(validChars);
	}

	@Override
	public FieldValidationResult validateValue(final Object value) {
		if (value == null) {
			return STATUS_OK; // nothing to validate
		}
		
		if (value instanceof String) {
			final List<Character> invalidChars = getInvalidChars( (String) value);
			if ( invalidChars.isEmpty() )  {
				return STATUS_OK;
			}
			else
			{
				final String invalidCharsAsString = ArrayUtils.toString(invalidChars);
				return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
                        "Field '" + fieldName + "' contains invalid char(s): "  + invalidCharsAsString);
			}
		}
		else
		{
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
                    "Field '" + fieldName + "' cannot be applied to fields of type '" + value.getClass().getSimpleName() + "'");
		}
		
			
	}


	protected List<Character> getInvalidChars(final String value)
	{
		final char[] charArray = value.toCharArray();
		final List<Character> toReturn = new ArrayList<Character>();
		
		for (char c : charArray) {
			if (! validChars.contains(c))
			{
				toReturn.add(new Character(c));
			}
		}
		
		return toReturn;
	}
	
}