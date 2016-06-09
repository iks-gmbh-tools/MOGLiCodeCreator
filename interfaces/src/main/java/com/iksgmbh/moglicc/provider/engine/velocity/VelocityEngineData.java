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
package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;

import com.iksgmbh.moglicc.provider.model.standard.Model;

public interface VelocityEngineData {
	
	/**
	 * used for Logging purpose  
	 */
	String getGeneratorPluginId();
	
	/**
	 * used for Logging purpose  
	 */
	String getArtefactType();
	
	/**
	 * @return Best Practice: input/<<generatorPluginId>>/<<artefactType>>
	 */
	File getTemplateDir();
	
	/**
	 * @return e.g. 'JavaBean.tpl'
	 */
	String getMainTemplateSimpleFileName();
	
	/**
	 * @return the model originally retrieved from a ModelProvider
	 */
	Model getModel();
}