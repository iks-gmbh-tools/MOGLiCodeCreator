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

public abstract class FieldValidator {
	
	public final static FieldValidationResult STATUS_OK = new FieldValidationResult(FieldValidationResult.Status.OK, null);

	protected String fieldName;
	
	public String getFieldName() {
		return fieldName;
	}

	protected abstract FieldValidationResult validateValue(Object value);
	
}