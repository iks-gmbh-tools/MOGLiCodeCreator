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

/**
 * To be thrown only from code of plugin and code from common module that is used by plugins.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MOGLiPluginException extends Exception {

	private static final long serialVersionUID = -1;
	
	protected String pluginErrorMessage;

	public MOGLiPluginException(Exception e) {
		super(e);
	}

	public MOGLiPluginException(String message) {
		super(message);
		pluginErrorMessage = message;
	}

	public MOGLiPluginException(String message, Exception e) {
		super(message, e);
		pluginErrorMessage = message;
	}

	public String getPluginErrorMessage() {
		return pluginErrorMessage;
	}
	
}