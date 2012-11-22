package com.iksgmbh.moglicc.intest.core;

import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_LIB;
import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_PLUGIN;
import static com.iksgmbh.moglicc.MogliSystemConstants.FILENAME_LOG_FILE;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.MogliSystemConstants;
import com.iksgmbh.moglicc.build.MogliReleaseBuilder;
import com.iksgmbh.moglicc.build.helper.ReleaseFileCollector;
import com.iksgmbh.moglicc.build.helper.ReleaseFileCollector.FileCollectionData;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.utils.MogliFileUtil;
import com.iksgmbh.utils.FileUtil;

public class CoreIntTest extends IntTestParent {
	
	public final String[] args = new String[0];

	@Override
	@Before
	public void setup() {
		super.setup();
		initTestRootDir();
	}
	
	private void initTestRootDir() {
		FileUtil.deleteDirWithContent(applicationRootDir);
		
		initBuildPropertiesFile();
		
		final File root = new File(applicationRootDir);
		root.mkdirs();
		
		ReleaseFileCollector.doYourJob(createFileCollectionData());
	}
	
	private void initBuildPropertiesFile() {
		final File sourceFile = new File(getProjectRootDir(), "../application/" 
				                         + RESOURCES_DIR + MogliReleaseBuilder.FILENAME_BUILD_PROPERTIES);
		final List<String> fileContentAsList = MogliFileUtil.getFileContentAsList(sourceFile);
		String newContent = "";
		for (final String line : fileContentAsList) {
			if (line.startsWith(MogliReleaseBuilder.PROPERTY_RELEASE_VERSION)) {
				System.out.println(MogliCodeCreator.VERSION);
				newContent += MogliReleaseBuilder.PROPERTY_RELEASE_VERSION + "="
                              + MogliCodeCreator.VERSION + FileUtil.getSystemLineSeparator();
			} else {
				newContent += line + FileUtil.getSystemLineSeparator();
			}
		}
		final File targetFile = new File(getProjectRootDir(), "target/classes/" + MogliReleaseBuilder.FILENAME_BUILD_PROPERTIES);
		MogliFileUtil.appendToFile(targetFile, newContent);
	}

	private FileCollectionData createFileCollectionData() {
		final MogliReleaseBuilder mogliReleaseBuilder = new MogliReleaseBuilder();
		final FileCollectionData fileCollectionData = new FileCollectionData();
		fileCollectionData.libSubdir = DIR_LIB;
		fileCollectionData.pluginsSubdir = DIR_PLUGIN;
		fileCollectionData.sourceDir = null;
		fileCollectionData.releaseDir = new File(applicationRootDir);
		fileCollectionData.fileListForRootDir = null;
		fileCollectionData.jarsOfCoreComponents = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfCoreModules());
		fileCollectionData.jarsOfPlugins = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfPluginModules());
		fileCollectionData.thirdPartyJars = mogliReleaseBuilder.getThirdPartyJars();
		return fileCollectionData;
	}


	@Test
	public void createsEmergencyLogFileIfDefinedWorkspaceDirCannotBeCreated() {
		// prepare test
		initPropertiesWith("workspace=");
		final File emergencyLogFile = new File(applicationRootDir, FILENAME_LOG_FILE);
		assertFileDoesNotExist(emergencyLogFile);
		
		// call functionality under test
		MogliCodeCreator.main(args);
		
		// cleanup critical stuff before possible test failures
		applicationPropertiesFile.delete();
		assertFileDoesNotExist(applicationPropertiesFile);
		
		// verify test result
		assertFileExists(emergencyLogFile);
		assertFileContainsEntry(emergencyLogFile, "ERROR: Error creating workspaceDir");
	}
	
	private void assertWorkspace(final File workspaceDir) {
		assertFileExists(workspaceDir);
		final File inputDir = new File(workspaceDir, MogliSystemConstants.DIR_INPUT_FILES);
		assertFileExists(inputDir);
		final File outputDir = new File(workspaceDir, MogliSystemConstants.DIR_OUTPUT_FILES);
		assertFileExists(outputDir);
		final File logsDir = new File(workspaceDir, MogliSystemConstants.DIR_LOGS_FILES);
		assertFileExists(logsDir);
		
		assertWorkspacePropertiesFile(workspaceDir);
	}

	private void assertWorkspacePropertiesFile(File workspaceDir) {
		final File workspacePropertiesFile = new File(workspaceDir, MogliSystemConstants.FILENAME_WORKSPACE_PROPERTIES);
		assertFileExists(workspacePropertiesFile);
		final File expectedFile = new File(PROJECT_ROOT_DIR + "../core/src/main/resources/" 
				                           + MogliSystemConstants.FILENAME_WORKSPACE_PROPERTIES);
		assertFileEquals(expectedFile, workspacePropertiesFile);
	}

	protected File initApplicationPropertiesFile(final String workspaceDirName) throws Exception {
		final File applicationPropertiesFile = new File(applicationRootDir, MogliSystemConstants.FILENAME_APPLICATION_PROPERTIES);
		FileUtil.createNewFileWithContent(applicationPropertiesFile, MogliSystemConstants.WORKSPACE_PROPERTY + "=" + workspaceDirName);
		return applicationPropertiesFile;
	}

	protected File initWorkspaceDir(final String workspaceDirName) throws Exception {
		final File workspaceDir = new File(applicationRootDir, workspaceDirName);
		assertFileDoesNotExist(workspaceDir);
		return workspaceDir;
	}

	@Test
	public void createsWorkspaceDefinedInSubdir() throws Exception {
		// prepare test
		final String workspaceDirName =  "workspaces/test";
		final File applicationPropertiesFile = initApplicationPropertiesFile(
				MogliSystemConstants.APPLICATION_ROOT_IDENTIFIER
				 + "/" + workspaceDirName);
		assertFileExists(applicationPropertiesFile);
		final File workspaceDir = initWorkspaceDir(workspaceDirName);

		// call functionality under test
		MogliCodeCreator.main(args);
		
		// verify test result
		assertWorkspace(workspaceDir);
	}

	@Test
	public void createsDefaultWorkspace() throws Exception {
		// call functionality under test
		MogliCodeCreator.main(args);
		
		// verify test result
		final File workspaceDir = new File(applicationRootDir);
		assertWorkspace(workspaceDir);
	}


}
