package com.iksgmbh.moglicc.plugin.type;

import java.util.List;

import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.plugin.type.basic.EngineProvider;

/**
 * EngineProvider that provides a class based execution strategy
 * 
 * @author Reik Oberrath
 */
public interface ClassBasedEngineProvider extends EngineProvider {

	
	/**
	 * When running the engine, for each class in the model a result content is generated.
	 * Typically, this is used to generate an artefact for each class in the model
	 * 
	 * @return GeneratorResultData for each class in the model
	 * @throws MogliPluginException
	 */
	List<GeneratorResultData> startEngineWithClassList() throws MogliPluginException;
}
