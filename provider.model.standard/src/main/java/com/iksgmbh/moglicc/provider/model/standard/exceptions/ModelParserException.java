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
package com.iksgmbh.moglicc.provider.model.standard.exceptions;

import java.util.List;

public class ModelParserException extends Exception {

	private static final long serialVersionUID = -2227516656058989198L;
	
	private List<String> errorList;

	public ModelParserException(final List<String> errorList) {
		this.errorList = errorList;
	}

	public List<String> getErrorList() {
		return errorList;
	}
	
	public int getErrorNumber() {
		return errorList.size();
	}

	public String getParserErrors() {
		final StringBuffer sb = new StringBuffer();
		for (String error : errorList) {
			sb.append(error + "\n");
		}
		return sb.toString();
	}	
}