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
package com.iksgmbh.moglicc.exceptions;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DUPLICATE_PLUGINIDS;

public class DuplicatePluginIdException extends MOGLiCoreException {

	private static final long serialVersionUID = -7861226890315250149L;
	
	private String pluginId;

	public DuplicatePluginIdException(String pluginId) {
		super(TEXT_DUPLICATE_PLUGINIDS + pluginId);
		this.pluginId = pluginId;
	}

	public String getPluginId() {
		return pluginId;
	}
	
}