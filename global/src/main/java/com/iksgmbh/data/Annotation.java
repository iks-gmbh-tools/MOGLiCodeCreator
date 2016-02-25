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
package com.iksgmbh.data;

public class Annotation {

	private String name;
	private String additionalInfo;
	
	public Annotation(final String name) {
		this.name = name;
	}

	public void setAdditionalInfo(final String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getName() {
		return name;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
	public String getFullInfo() {
		if (additionalInfo == null) {
			return name;
		}
		return name + " " + additionalInfo;
	}

	@Override
	public String toString() {
		return "Annotation [name=" + name + ", additionalInfo="
				+ additionalInfo + "]";
	}
	
	
	
}