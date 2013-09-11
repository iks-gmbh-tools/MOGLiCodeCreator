package com.iksgmbh.moglicc.plugin.subtypes.providers;

import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;

/**
 * EngineProvider that provides a class based execution strategy
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface ClassBasedEngineProvider extends EngineProvider {

	
	/**
	 * When running the engine, for each class in the model a result content is generated.
	 * Typically, this is used to generate an artefact for each class in the model
	 * 
	 * @return GeneratorResultData for each class in the model
	 * @throws MOGLiPluginException
	 */
	List<GeneratorResultData> startEngineWithClassList() throws MOGLiPluginException;
}