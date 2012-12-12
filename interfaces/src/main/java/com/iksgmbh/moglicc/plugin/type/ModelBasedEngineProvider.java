package com.iksgmbh.moglicc.plugin.type;

import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.type.basic.EngineProvider;

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