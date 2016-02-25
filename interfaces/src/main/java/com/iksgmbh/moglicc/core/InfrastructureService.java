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
package com.iksgmbh.moglicc.core;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;

/**
 * Provides the Mogli core functionality to the plugins.
 * Interface for Transfer Object in {@link MOGLiPlugin.setMogliInfrastructure}.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface InfrastructureService {

	
	/**
 	 * @return plugin specific subdir in the application input dir
	 */
	File getPluginInputDir();

	/**
 	 * @return plugin specific subdir in the application output dir
	 */
	File getPluginOutputDir();
	
	/**
	 * @return plugin specific subdir in the application temp dir
	 */
	File getPluginTempDir();
	
	/**
	 * @return plugin specific logFile in the standard directory for logFiles
	 */
	File getPluginLogFile();
	
	/**
	 * @return plugin specific directory for help files
	 */
	File getPluginHelpDir();
	
	/**
	 * @return logger to log into the plugin specific logFile 
	 */
	Logger getPluginLogger();
	
	/**
	 * Reads properties from workspace.properties
	 * @param key
	 * @return property for key
	 */
	String getWorkspaceProperty(String key);

	/**
	 * @return workspace directory for logFiles
	 */
	File getWorkspaceLogsDir();
	
	/**
	 * @return workspace directory for temporary files
	 */
	File getWorkspaceTempDir();
	
	File getApplicationRootDir();
	
	ProviderPlugin getProvider(String id);
	GeneratorPlugin getGenerator(String id);

	<T> List<T> getPluginsOfType(Class<T> type);
		
}