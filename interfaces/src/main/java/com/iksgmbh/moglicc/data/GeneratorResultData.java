package com.iksgmbh.moglicc.data;

/**
 * Return object for EngineProvider.
 * 
 * @author Reik Oberrath
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

}
