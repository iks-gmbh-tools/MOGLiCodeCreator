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