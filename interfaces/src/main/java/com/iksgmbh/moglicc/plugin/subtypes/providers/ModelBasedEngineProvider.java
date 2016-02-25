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
import com.iksgmbh.moglicc.generator.GeneratorResultData;

/**
 * EngineProvider that provides a model based execution strategy 
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface ModelBasedEngineProvider extends EngineProvider {

	/**
	 * When running the engine, for the whole model a single result content is generated.
	 * Typically this is used to generate a singe artefact containing information
	 * of all classes in the model, e.g. a central configuration file for domain objects.
	 * 
	 * @return GeneratorResultData for each class in the model
	 * @throws MOGLiPluginException
	 */
	GeneratorResultData startEngineWithModel() throws MOGLiPluginException;
}