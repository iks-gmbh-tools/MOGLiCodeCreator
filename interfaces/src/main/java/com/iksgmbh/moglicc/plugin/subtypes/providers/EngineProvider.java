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
package com.iksgmbh.moglicc.plugin.subtypes.providers;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;

public interface EngineProvider extends ProviderPlugin {

	/**
	 * Called by a plugin to feed a engine provider with neccassary data.
	 * Two interacting plugins know their interface and can cast 
	 * the engineData object on a concrete type
	 * @throws MOGLiPluginException
	 */
	void setEngineData(Object engineData) throws MOGLiPluginException;
	
	/**
	 * Called by a plugin to use the engineProvider's functionality to do its job.
	 * Two interacting plugins know their interface and can cast the return object 
	 * on the corresponding type
	 * @return arbitrary result object
	 * @throws MOGLiPluginException
	 */
	Object startEngine() throws MOGLiPluginException;
}