package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;

import com.iksgmbh.moglicc.provider.model.standard.Model;

public interface VelocityEngineData {
	
	public enum ExecutionMode { FULL_GENERATION,   // This mode is used in a possible second step for the standard generation process.
		                       ONLY_PREPARATION }  // This mode is always used first in order to read the template header properties.
	                                               // For this mode, the generation is performed only with one class in the model.
	                                               // After this preparation step the information is available whether the template is to be applied for full generation.
	                                               // This two step procedure shortens the generation time for big models and many templates for different valid models drastically,
	                                               // because not every class is merged with every template.
	
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