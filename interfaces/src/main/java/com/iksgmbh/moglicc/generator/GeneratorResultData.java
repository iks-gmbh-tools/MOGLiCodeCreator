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
package com.iksgmbh.moglicc.generator;

import java.util.List;

/**
 * Most basic, abstract definition of a return object for EngineProviders. 
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface GeneratorResultData {
	
	/**
	 * @return content that has resulted from an generation event
	 */
	String getGeneratedContent();
	
	/**
	 * Can be used to provide meta information such as
	 * filename, targetDir or overwrite instruction, 
	 * e.g. read from a template file
	 * 
	 * @param key
	 * @return value of the key
	 */
	String getProperty(String key);
	
	
	/**
	 * In contrast to getProperty, a list of corresponding values is returned.
	 * Used when keys exist for which more than one values are defined
	 * (e.g. key 'RenameFile' for TreeBuilder 'artefact.properties' file) 
	 * 
	 * @param key
	 * @return list of value to a key
	 */
	List<String> getAllPropertyValues(String key);

}