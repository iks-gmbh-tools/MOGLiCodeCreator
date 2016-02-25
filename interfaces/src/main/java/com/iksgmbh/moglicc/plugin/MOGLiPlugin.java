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
package com.iksgmbh.moglicc.plugin;

import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

/**
 * Basic plugin type on which Mogli plugins are casted when they are loaded by reflection.
 * It allows interaction between Core and Plugins.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface MOGLiPlugin {

	String DEFAULT_DATA_DIR = "defaultInputData";
	String HELP_DATA_DIR = "helpData";
	String FILENAME_PLUGIN_JAR_PROPERTIES = "Mogli.properties";
	String ARTEFACT_PROPERTIES_HELP_FILE = "ArtefactProperties.htm";

	enum PluginType { GENERATOR, PROVIDER};

	/**
	 * @return PluginType to indicate the plugin's purpose
	 */
	PluginType getPluginType();

	/**
	 * @return Unique id of plugin
	 */
	String getId();

	/**
	 * @return List of other Mogli plugins which this plugin needs to execute
	 */
	List<String> getDependencies();

	/**
	 * Called from core to inject the Mogli infrastructure.
	 * @param infrastructure
	 */
	void setInfrastructure(InfrastructureService infrastructure);

	InfrastructureService getInfrastructure();

	/**
	 * Called from core to execute the plugins.
	 * @throws MOGLiPluginException
	 */
	void doYourJob() throws MOGLiPluginException;

	/**
	 * If plugin's input directory does not exist, the Core calls this method
	 * to create it and unpacks default data from plugin's jarfile into it.
	 * @return true if default data has been initialized successfully
	 * @throws MOGLiPluginException
	 */
	boolean unpackDefaultInputData() throws MOGLiPluginException;

	/**
	 * If plugin's help directory does not exist, the Core calls this method
	 * to create it and unpacks help data from plugin's jarfile into it.
	 * @return true if help data has been initialized successfully
	 * @throws MOGLiPluginException
	 */
	boolean unpackPluginHelpFiles() throws MOGLiPluginException;
	
	/**
	 * @return Compressed information about work done
	 */
	String getShortReport();
	
	/**
	 * If possible (in terms of resolved dependencies) plugins are
	 * ordered to execute in the order defined by the return value. 
	 * @return suggested execution order of the pludin
	 */
	int getSuggestedPositionInExecutionOrder();
		
}