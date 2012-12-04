package com.iksgmbh.moglicc.core;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.plugin.type.basic.DataProvider;
import com.iksgmbh.moglicc.plugin.type.basic.EngineProvider;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.plugin.type.basic.ModelProvider;

/**
 * Provides the Mogli core functionality to the plugins.
 * Interface for Transfer Object in {@link MOGLiPlugin2.setMogliInfrastructure}.
 * 
 * @author Reik Oberrath
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
	
	ModelProvider getModelProvider(String id);
	DataProvider getDataProvider(String id);
	EngineProvider getEngineProvider(String id);
	Generator getGenerator(String id);

	<T> List<T> getPluginsOfType(Class<T> type);
		
}
