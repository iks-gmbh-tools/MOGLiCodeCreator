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
import com.iksgmbh.moglicc.provider.model.standard.Model;

/**
 * Interface for other plugins to use the modelProvider's functionality.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface ModelProvider extends ProviderPlugin 
{
	String getModelFileName();
	
	String getModelName();
	
	/**
	 * @param pluginId of the calling plugin
	 * @return model
	 * @throws MOGLiPluginException
	 */
	Model getModel(String pluginId) throws MOGLiPluginException;
	
	/**
	 * @param pluginId of the calling plugin
	 * @param inputData data needed to build the model (if neccesarry)
	 *        The specific type needs only to be known by the caller and the implementation of this method. 
	 * @return model
	 * @throws MOGLiPluginException
	 */
	Model getModel(String pluginId, Object inputData) throws MOGLiPluginException;

}