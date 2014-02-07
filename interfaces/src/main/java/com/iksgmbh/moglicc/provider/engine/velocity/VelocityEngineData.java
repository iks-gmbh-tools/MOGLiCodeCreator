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
