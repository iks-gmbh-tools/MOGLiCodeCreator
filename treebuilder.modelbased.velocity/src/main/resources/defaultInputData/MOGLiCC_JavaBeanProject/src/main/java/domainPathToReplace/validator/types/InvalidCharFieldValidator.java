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

import org.apache.commons.lang.CharSet;

public class InvalidCharFieldValidator extends ValidCharFieldValidator {

	private CharSet invalidChars;

	public InvalidCharFieldValidator(final String fieldName, final String invalidChars) {
		super(fieldName, null);
		this.invalidChars = CharSet.getInstance(invalidChars);
	}

	@Override
	protected List<Character> getInvalidChars(final String value)
	{
		final char[] charArray = value.toCharArray();
		final List<Character> toReturn = new ArrayList<Character>();
		
		for (char c : charArray) {
			if (invalidChars.contains(c))
			{
				toReturn.add(new Character(c));
			}
		}
		
		return toReturn;
	}
	
}