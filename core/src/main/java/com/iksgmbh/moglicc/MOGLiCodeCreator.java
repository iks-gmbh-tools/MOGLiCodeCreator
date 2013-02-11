package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_HELP_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_TEMP_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_INTRODUCTION_HELPFILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.WORKSPACE_PROPERTY;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_APPLICATION_TERMINATED;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DONE;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NOTHING_TO_DO;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_PLUGINS_FOUND;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.PluginMetaData.PluginStatus;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.DuplicatePluginIdException;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.helper.MetaDataLoader;
import com.iksgmbh.moglicc.helper.PluginExecutor;
import com.iksgmbh.moglicc.helper.PluginExecutor.PluginExecutionData;
import com.iksgmbh.moglicc.helper.PluginLoader;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;

/**
 * Starter class of the MOGLiCodeCreator application
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MOGLiCodeCreator {

	// *****************************  static stuff  ************************************

	public static final String VERSION = "1.1.0-SNAPSHOT";

	private static String applicationRootDir = System.getProperty("user.dir");
	private static String workspaceDirArgument;

	private static File emergencyLogfile;

	public static String getApplicationRootDir() {
		return applicationRootDir;
	}

	public static void setApplicationRootDir(String applicationRootDir) {
		MOGLiCodeCreator.applicationRootDir = applicationRootDir;
	}

	public static File getLogFile() {
		return MOGLiLogUtil.getCoreLogfile();
	}

	public static void main(String[] args) {
		doBasicInits(args);

		try {
			final MOGLiCodeCreator mogliCodeCreator = new MOGLiCodeCreator();
			mogliCodeCreator.doYourJob();
		} catch (Throwable t) {
			t.printStackTrace();
			if (MOGLiLogUtil.getCoreLogfile() == null) {
				// used for basic errors before regular logFile could be created
				initEmergencyLogFile();
			}
			MOGLiLogUtil.logError(t.getMessage());
		}
	}

	protected static void doBasicInits(String[] args) {
		cleanEmergencyLogFile();
		System.setProperty("file.encoding", "UTF-8");
		setArgumentParameters(args);
	}

	private static void initEmergencyLogFile() {
		MOGLiLogUtil.setCoreLogfile(emergencyLogfile);
		initLogFileContent();
	}

	private static void setArgumentParameters(String[] args) {
		if (args.length > 0) {
			// setApplicationRootDir(args[0]); argument is application root
			workspaceDirArgument = args[0];
		}
	}

	private static void cleanEmergencyLogFile() {
		emergencyLogfile = new File(applicationRootDir +  "/" + FILENAME_LOG_FILE);
		emergencyLogfile.delete(); // delete if it exists
	}

	// **************************  Instance fields  *********************************

	private List<PluginMetaData> pluginMetaDataList;
	private Properties applicationProperties;
	private Properties workspaceProperties;
	final List<String> logEntriesBeforeLogFileExists = new ArrayList<String>();
	private File workspaceDir;
	private File tempDir;
	private File inputDir;
	private File helpDir;
	private File logDir;
	private File outputDir;

	// *****************************  Constructor  ************************************

	public MOGLiCodeCreator() {
		readApplicationPropertiesFile();
		initWorkspace();
		initApplicationDirectories();
	}

	private void initWorkspace() {
		initWorkspaceDir();
		createMogliLogFile();
		readWorkspaceProperties();
		initWorkspaceDirectories();
	}

	private void initWorkspaceDir() {
		final String workspace;
		if (workspaceDirArgument != null) {
			// check first application argument
			workspace = workspaceDirArgument;
			logEntriesBeforeLogFileExists.add("Workspace set by application argument to <" + workspace + ">");
		} else {
			final String tmp = readWorkspaceDirFromApplicationProperties();
			if (tmp == null) {
				// last fallback
				workspace = applicationRootDir;
				logEntriesBeforeLogFileExists.add("No workspace defined. Application root dir is used: " + workspace);
			} else {
				workspace = tmp.replace(APPLICATION_ROOT_IDENTIFIER, applicationRootDir);
				logEntriesBeforeLogFileExists.add("Workspace defined in application properties as <" + workspace + ">");
			}
		}

		workspaceDir = new File(workspace);
		if (! workspaceDir.exists()) {
			boolean ok = workspaceDir.mkdirs();
			if (! ok) {
				throw new MOGLiCoreException("Error creating workspaceDir <" + workspaceDir.getAbsolutePath() + ">");
			}
		}
	}

	private void readWorkspaceProperties() {
		final File workspacePropertiesFile = new File(workspaceDir, FILENAME_WORKSPACE_PROPERTIES);
		if (! workspacePropertiesFile.exists()) {
			try {
				workspacePropertiesFile.createNewFile();
				final String defaultContent = FileUtil.readTextResourceContentFromClassPath(getClass(), FILENAME_WORKSPACE_PROPERTIES);
				FileUtil.appendToFile(workspacePropertiesFile, defaultContent);
				logEntriesBeforeLogFileExists.add("File '" + FILENAME_WORKSPACE_PROPERTIES
						                           + "' did not exist and was created.");
			} catch (IOException e) {
				throw new MOGLiCoreException("Error creating " + workspacePropertiesFile.getAbsolutePath(),  e);
			}
		}

		workspaceProperties = readProperties(workspacePropertiesFile, FILENAME_WORKSPACE_PROPERTIES);

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
		inputDir.mkdirs();
	}

	private void initApplicationDirectories() {
		helpDir = new File(applicationRootDir + "/" + DIR_HELP_FILES);
		if (! helpDir.exists()) {
			MOGLiLogUtil.logInfo("Application help directory does not exist and will be created!");
			helpDir.mkdirs();
			String content = null;

			try {
				content = FileUtil.readTextResourceContentFromClassPath(getClass(), FILENAME_INTRODUCTION_HELPFILE);
			} catch (IOException e) {
				MOGLiLogUtil.logWarning("IOException reading " + FILENAME_INTRODUCTION_HELPFILE + " from core jarfile: " + e.getMessage());
				return;
			}

			if (content == null) {
				MOGLiLogUtil.logWarning("Cannot find " + FILENAME_INTRODUCTION_HELPFILE + " in core jarfile.");
				return;
			}

			try {
				final File file = new File(helpDir, FILENAME_INTRODUCTION_HELPFILE);
				FileUtil.appendToFile(file, content);
			} catch (IOException e) {
				MOGLiLogUtil.logWarning("Error writing " + FILENAME_INTRODUCTION_HELPFILE + " to " + helpDir.getAbsolutePath());
				return;
			}

			MOGLiLogUtil.logInfo("Application help directory created");
		}
	}

	private void initDirectory(final File dir, final boolean createDir) {
		FileUtil.deleteDirWithContent(dir.getAbsolutePath());
		if (dir.exists()) {
			throw new MOGLiCoreException("Could not delete " + dir.getAbsolutePath());
		}

		if (createDir) {
			dir.mkdirs();
		}

		if (createDir && ! dir.exists()) {
			throw new MOGLiCoreException("Could not create " + dir.getAbsolutePath());
		}
	}

	private void createMogliLogFile() {
		logDir = new File(workspaceDir, DIR_LOGS_FILES);
		initDirectory(logDir, true);
		MOGLiLogUtil.createNewLogfile(new File(logDir, FILENAME_LOG_FILE));
		initLogFileContent();
		for (final String logEntry : logEntriesBeforeLogFileExists) {
			MOGLiLogUtil.logInfo(logEntry);
		}
	}

	private static void initLogFileContent() {
		MOGLiLogUtil.logInfo("MOGLi Code Creator " + VERSION);
		MOGLiLogUtil.logInfo("----------------------------");
		MOGLiLogUtil.logInfo("Encoding: " + System.getProperty("file.encoding"));
	}

	// *****************************  explicitely tested methods  ************************************

	void doYourJob() {
		pluginMetaDataList = MetaDataLoader.doYourJob(workspaceProperties);
		if (getNumberOfPluginsToLoad(pluginMetaDataList) == 0) {
			MOGLiLogUtil.logInfo(TEXT_NOTHING_TO_DO);
			return;
		}

		List<MOGLiPlugin> plugins = null;
		try {
			plugins = PluginLoader.doYourJob(pluginMetaDataList);
		} catch (DuplicatePluginIdException e) {
			MOGLiLogUtil.logInfo(TEXT_APPLICATION_TERMINATED + e.getMessage());
			return;
		}

		try {
			pluginMetaDataList = PluginExecutor.doYourJob(createPluginExecutionData(plugins));
		} catch (UnresolvableDependenciesException e) {
			logPluginMetaData(pluginMetaDataList);
			MOGLiLogUtil.logInfo(TEXT_APPLICATION_TERMINATED + e.getMessage());
			return;
		}

		logFinalInformation();
	}


	private void logFinalInformation() {
		MOGLiLogUtil.logInfo("");
		logPluginMetaData(pluginMetaDataList);
		MOGLiLogUtil.logInfo("");
		int numberOfSuccessfullyExecutedPlugins = getNumberOfSuccessfullyExecutedPlugins(pluginMetaDataList);
		int numberOfNotExecutedPlugins = getNumberOfNotExecutedPlugins(pluginMetaDataList);
		if (numberOfNotExecutedPlugins == 0) {
			MOGLiLogUtil.logInfo("All " + numberOfSuccessfullyExecutedPlugins + " plugins executed successfully!");
		} else {
			MOGLiLogUtil.logInfo(numberOfSuccessfullyExecutedPlugins + " plugins executed successfully!");
			MOGLiLogUtil.logInfo(numberOfNotExecutedPlugins + " plugins not or erroneously executed!");
		}
		MOGLiLogUtil.logInfo(TEXT_DONE);
	}

	private PluginExecutionData createPluginExecutionData(List<MOGLiPlugin> plugins) {
		InfrastructureInitData infrastructureInitData = new InfrastructureInitData(new File(applicationRootDir),
				                                            logDir, outputDir, tempDir, inputDir, helpDir,
				                                            applicationProperties);
		return new PluginExecutionData(plugins, pluginMetaDataList, infrastructureInitData);
	}

	void logPluginMetaData(List<PluginMetaData> pluginMetaDataList) {
		if (pluginMetaDataList.size() == 0) {
			return;
		}
		MOGLiLogUtil.logInfo(TEXT_PLUGINS_FOUND);
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			MOGLiLogUtil.logInfo(pluginMetaData.toString());
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
		final File applicationPropertiesFile = MOGLiFileUtil.getNewFileInstance(FILENAME_APPLICATION_PROPERTIES);
		if (! applicationPropertiesFile.exists()) {
			try {
				applicationPropertiesFile.createNewFile();
				final String defaultContent = FileUtil.readTextResourceContentFromClassPath(getClass(), FILENAME_APPLICATION_PROPERTIES);
				FileUtil.appendToFile(applicationPropertiesFile, defaultContent);
				logEntriesBeforeLogFileExists.add("File '" + FILENAME_APPLICATION_PROPERTIES
						                           + "' did not exist and was created.");
			} catch (IOException e) {
				throw new MOGLiCoreException("Error creating " + applicationPropertiesFile.getAbsolutePath(),  e);
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

		final File applicationPropertiesFile = new File(MOGLiCodeCreator.getApplicationRootDir()
				+ "/" + FILENAME_APPLICATION_PROPERTIES);
		applicationProperties = readProperties(applicationPropertiesFile, FILENAME_APPLICATION_PROPERTIES);
	}

	protected Properties readProperties(final File propertiesFile, final String filename) {
		final Properties properties = new Properties();
		try {
			final FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			properties.load(fileInputStream);
		    fileInputStream.close();
		} catch (IOException e) {
			throw new MOGLiCoreException("Could not load " + filename, e);
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