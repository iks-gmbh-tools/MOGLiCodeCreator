package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MogliSystemConstants.APPLICATION_ROOT_IDENTIFIER;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_HELP_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_TEMP_FILES;
import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_LOG_FILE;
import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_WORKSPACE_PROPERTIES;
import static com.iksgmbh.moglicc.MogliSystemConstants.WORKSPACE_PROPERTY;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_APPLICATION_TERMINATED;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_DONE;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_NOTHING_TO_DO;
import static com.iksgmbh.moglicc.MogliTextConstants.TEXT_PLUGINS_FOUND;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.DuplicatePluginIdException;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.helper.MetaDataLoader;
import com.iksgmbh.moglicc.helper.PluginExecutor;
import com.iksgmbh.moglicc.helper.PluginExecutor.PluginExecutionData;
import com.iksgmbh.moglicc.helper.PluginLoader;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.utils.MogliFileUtil;
import com.iksgmbh.moglicc.utils.MogliLogUtil;
import com.iksgmbh.utils.FileUtil;

/**
 * Starter class of the MogliCodeCreator application
 * @author Reik Oberrath
 */
public class MogliCodeCreator {
	
	// *****************************  static stuff  ************************************

	public static final String VERSION = "0.1.2-SNAPSHOT";
	
	private static String applicationRootDir = System.getProperty("user.dir");

	private static File emergencyLogfile;
	
	public static String getApplicationRootDir() {
		return applicationRootDir;
	}

	public static void setApplicationRootDir(String applicationRootDir) {
		MogliCodeCreator.applicationRootDir = applicationRootDir;
	}
	
	public static File getLogFile() {
		return MogliLogUtil.getCoreLogfile();
	}

	public static void main(String[] args) {
		initStatics(args);
		try {
			final MogliCodeCreator mogliCodeCreator = new MogliCodeCreator();
			mogliCodeCreator.doYourJob();
		} catch (Throwable t) {
			t.printStackTrace();
			if (MogliLogUtil.getCoreLogfile() == null) {
				initEmergencyLogFile();
			}
			MogliLogUtil.logError(t.getMessage());
		}
	}

	private static void initEmergencyLogFile() {
		MogliLogUtil.setCoreLogfile(emergencyLogfile);
		initLogFileContent();
	}

	private static void initStatics(String[] args) {
		if (args.length > 0) {
			setApplicationRootDir(args[0]);
		}		
		 // used for basic errors before regular logFile could be created
		emergencyLogfile = new File(applicationRootDir +  "/" + FILENAME_LOG_FILE);
		emergencyLogfile.delete(); // delete if it exists
	}

	// **************************  Instance fields  *********************************	

	private List<PluginMetaData> pluginMetaDataList;
	private Properties applicationProperties;
	final List<String> logEntriesBeforeLogFileExists = new ArrayList<String>();
	private File workspaceDir;
	private File tempDir;
	private File inputDir;
	private File helpDir;
	private File logDir;
	private File outputDir; 

	// *****************************  Constructor  ************************************	
	
	public MogliCodeCreator() {
		readApplicationPropertiesFile();
		initWorkspace();
		initApplicationDirectories();
	}
	
	private void initWorkspace() {
		initWorkspaceDir();
		createMogliLogFile();
		checkWorkspacePropertiesFile();
		initWorkspaceDirectories();
	}

	private void initWorkspaceDir() {
		String workspace = readWorkspaceDirFromApplicationProperties();
		if (workspace ==  null) {
			workspace = applicationRootDir;
		} else if (workspace.startsWith(APPLICATION_ROOT_IDENTIFIER)) {
			workspace = workspace.replace(APPLICATION_ROOT_IDENTIFIER, applicationRootDir);
		}
		workspaceDir = new File(workspace);
		if (! workspaceDir.exists()) {
			boolean ok = workspaceDir.mkdirs();
			if (! ok) {
				throw new MogliCoreException("Error creating workspaceDir <" + workspaceDir.getAbsolutePath() + ">");
			}
		}
	}

	private void checkWorkspacePropertiesFile() {
		final File workspacePropertiesFile = new File(workspaceDir, FILENAME_WORKSPACE_PROPERTIES);
		if (! workspacePropertiesFile.exists()) {
			try {
				workspacePropertiesFile.createNewFile();
				final String defaultContent = FileUtil.readTextResourceContentFromClassPath(getClass(), FILENAME_WORKSPACE_PROPERTIES);
				FileUtil.appendToFile(workspacePropertiesFile, defaultContent);
				logEntriesBeforeLogFileExists.add("File '" + FILENAME_WORKSPACE_PROPERTIES 
						                           + "' did not exist and was created.");
			} catch (IOException e) {
				throw new MogliCoreException("Error creating " + workspacePropertiesFile.getAbsolutePath(),  e);
			}
		}
	
	}

	String readWorkspaceDirFromApplicationProperties() {
		String workspace = applicationProperties.getProperty(WORKSPACE_PROPERTY);
		if (workspace == null) {
			logEntriesBeforeLogFileExists.add("File '" + FILENAME_APPLICATION_PROPERTIES 
					                          + "' does not contain a workspace definition. " 
					                          + "ApplicationRootDir is used.");
		}
		return workspace;
	}

	private void initWorkspaceDirectories() {
		outputDir = new File(workspaceDir, DIR_OUTPUT_FILES);
		initDirectory(outputDir, true);
		
		tempDir = new File(workspaceDir, DIR_TEMP_FILES);
		initDirectory(tempDir, false); // will be created when needed
		
		inputDir = new File(workspaceDir, DIR_INPUT_FILES);		
	}

	private void initApplicationDirectories() {
		helpDir = new File(applicationRootDir + "/" + DIR_HELP_FILES);
	}

	private void initDirectory(final File dir, final boolean createDir) {
		FileUtil.deleteDirWithContent(dir.getAbsolutePath());
		if (dir.exists()) {
			throw new MogliCoreException("Could not delete " + dir.getAbsolutePath());
		}
		
		if (createDir) {
			dir.mkdirs();
		}

		if (createDir && ! dir.exists()) {
			throw new MogliCoreException("Could not create " + dir.getAbsolutePath());
		}
	}
	
	private void createMogliLogFile() {
		logDir = new File(workspaceDir, DIR_LOGS_FILES);
		System.out.println(logDir.getAbsolutePath());
		initDirectory(logDir, true);
		MogliLogUtil.createNewLogfile(new File(logDir, FILENAME_LOG_FILE));
		initLogFileContent();
		for (final String logEntry : logEntriesBeforeLogFileExists) {
			MogliLogUtil.logInfo(logEntry);
		}
	}

	private static void initLogFileContent() {
		MogliLogUtil.logInfo("Mogli Version " + VERSION);
		MogliLogUtil.logInfo("----------------------------");
	}
	
	// *****************************  explicitely tested methods  ************************************

	void doYourJob() {		
		pluginMetaDataList = MetaDataLoader.doYourJob(applicationProperties);
		if (getNumberOfPluginsToLoad(pluginMetaDataList) == 0) {
			MogliLogUtil.logInfo(TEXT_NOTHING_TO_DO);
			return;
		}
		
		List<PluginExecutable> plugins = null;
		try {
			plugins = PluginLoader.doYourJob(pluginMetaDataList);
		} catch (DuplicatePluginIdException e) {
			MogliLogUtil.logInfo(TEXT_APPLICATION_TERMINATED + e.getMessage());
			return;
		}
		
		try {
			pluginMetaDataList = PluginExecutor.doYourJob(createPluginExecutionData(plugins));
		} catch (UnresolvableDependenciesException e) {
			logPluginMetaData(pluginMetaDataList);
			MogliLogUtil.logInfo(TEXT_APPLICATION_TERMINATED + e.getMessage());
			return;
		}
		
		logFinalInformation();
	}
	
	
	private void logFinalInformation() {
		MogliLogUtil.logInfo("");
		logPluginMetaData(pluginMetaDataList);
		MogliLogUtil.logInfo("");
		int numberOfSuccessfullyExecutedPlugins = getNumberOfSuccessfullyExecutedPlugins(pluginMetaDataList);
		int numberOfNotExecutedPlugins = getNumberOfNotExecutedPlugins(pluginMetaDataList);
		if (numberOfNotExecutedPlugins == 0) {
			MogliLogUtil.logInfo("All " + numberOfSuccessfullyExecutedPlugins + " plugins executed successfully!");
		} else {
			MogliLogUtil.logInfo(numberOfSuccessfullyExecutedPlugins + " plugins executed successfully!");
			MogliLogUtil.logInfo(numberOfNotExecutedPlugins + " plugins not or erroneously executed!");
		}
		MogliLogUtil.logInfo(TEXT_DONE);
	}

	private PluginExecutionData createPluginExecutionData(List<PluginExecutable> plugins) {
		InfrastructureInitData infrastructureInitData = new InfrastructureInitData(new File(applicationRootDir), 
				                                            logDir, outputDir, tempDir, inputDir, helpDir,
				                                            applicationProperties);
		return new PluginExecutionData(plugins, pluginMetaDataList, infrastructureInitData);
	}

	void logPluginMetaData(List<PluginMetaData> pluginMetaDataList) {
		if (pluginMetaDataList.size() == 0) {
			return;
		}
		MogliLogUtil.logInfo(TEXT_PLUGINS_FOUND);
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			MogliLogUtil.logInfo(pluginMetaData.toString());
		}
	}
	
	
	int getNumberOfPluginsToLoad(List<PluginMetaData> pluginMetaDataList) {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.isStatusOK()) {
				counter++;
			}
		}
		return counter;
	}

	void checkApplicationPropertiesFile() {
		final File applicationPropertiesFile = MogliFileUtil.getNewFileInstance(FILENAME_APPLICATION_PROPERTIES);
		if (! applicationPropertiesFile.exists()) {
			try {
				applicationPropertiesFile.createNewFile();
				final String defaultContent = FileUtil.readTextResourceContentFromClassPath(getClass(), FILENAME_APPLICATION_PROPERTIES);
				FileUtil.appendToFile(applicationPropertiesFile, defaultContent);
				logEntriesBeforeLogFileExists.add("File '" + FILENAME_APPLICATION_PROPERTIES 
						                           + "' did not exist and was created.");
			} catch (IOException e) {
				throw new MogliCoreException("Error creating " + applicationPropertiesFile.getAbsolutePath(),  e);
			}
		}
	}
	
	
	// *****************************  private methods  ************************************

	private int getNumberOfSuccessfullyExecutedPlugins(List<PluginMetaData> pluginMetaDataList) {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getStatus() == PluginStatus.EXECUTED) {
				counter++;
			}
		}
		return counter;
	}

	private int getNumberOfNotExecutedPlugins(List<PluginMetaData> pluginMetaDataList) {
		int counter = 0;
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.getStatus() != PluginStatus.EXECUTED) {
				counter++;
			}
		}
		return counter;
	}


	private void readApplicationPropertiesFile() {
		checkApplicationPropertiesFile();
		
		final File propertiesFile = new File(MogliCodeCreator.getApplicationRootDir() 
				+ "/" + FILENAME_APPLICATION_PROPERTIES);
		applicationProperties = readProperties(propertiesFile);
	}

	protected Properties readProperties(final File propertiesFile) {
		final Properties properties = new Properties();
		try {
			final FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			properties.load(fileInputStream);
		    fileInputStream.close();
		} catch (IOException e) {
			throw new MogliCoreException("Could not load " + FILENAME_APPLICATION_PROPERTIES, e);
		}
		return properties;
	}
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public List<PluginMetaData> getPluginMetaDataList() {
		return pluginMetaDataList;
	}
	
}


















































































































































































































































