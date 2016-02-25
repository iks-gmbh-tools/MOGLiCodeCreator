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
package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_REPORT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_GENERATION_REPORT_FILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_PROVIDER_REPORT_FILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_SHORT_REPORT_FILE;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.moglicc.lineinserter.modelbased.velocity.VelocityModelBasedLineInserterStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.OSUtil;

public class B_WorkspaceConfigurationAcceptanceSystemTest extends __AbstractSystemTest {

	private static final String PROPERTIES_DIR = "properties";

	@Test
	public void doesNotExecuteDeactivatedPlugin() throws Exception {
		// prepare test
		final File workspacePropertiesFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES);
		final String sep = FileUtil.getSystemLineSeparator();
		MOGLiFileUtil.createNewFileWithContent(workspacePropertiesFile,
				                              "com.iksgmbh.moglicc.treebuilder.modelbased.velocity=activated" + sep +
				                              "com.iksgmbh.moglicc.filemaker.classbased.velocity=activated"+ sep +
				                              "com.iksgmbh.moglicc.lineinserter.modelbased.velocity=deactivated"+ sep +
				                              "com.iksgmbh.moglicc.provider.engine.velocity=activated" + sep +
				                              "com.iksgmbh.moglicc.provider.model.standard=activated");

		// call functionality under test
		executeMogliApplication();

		// cleanup
		workspacePropertiesFile.delete();

		// verify test result
		assertChildrenNumberInDirectory(applicationOutputDir, 3);
		final File pluginOutputDir = new File(applicationOutputDir, VelocityModelBasedLineInserterStarter.PLUGIN_ID);
		assertFileDoesNotExist(pluginOutputDir);
	}

	@Test
	public void createsWorkspaceDirProvidedAsArgumentOfStartScript() throws Exception {
		// prepare test
		final String workspace = "workspaces/test3";
		final File workspaceDir = initWorkspaceDir(workspace);
		final File startFile;
		final String startScript;
		final String exeCommand;
		
		if (OSUtil.isWindows()) {
			startScript = "start.bat";
			startFile = new File(applicationRootDir, startScript);
			MOGLiFileUtil.createNewFileWithContent(startFile, MOGLiReleaseBuilder.FILENAME_STARTBAT + " " + workspace);
			exeCommand = startScript;
		} else {
			startScript = "start.sh";
			startFile = new File(applicationRootDir, startScript);
			MOGLiFileUtil.createNewFileWithContent(startFile, "#!/bin/sh"  + System.getProperty("line.separator") +  
					                                           System.getProperty("line.separator") +
					                                          "sh " + MOGLiReleaseBuilder.FILENAME_STARTSH + " " + workspace);
			exeCommand = "sh " + startScript;
		}

		// call functionality under test
		executeMogliApplication(exeCommand);

		// cleanup
		startFile.delete();

		// verify test result
		assertWorkspace(workspaceDir);
	}

	@Test
	public void createsWorkspaceDirConfiguredInStartScript() throws Exception {
		// prepare test
		final File workspaceDir = initWorkspaceDir("workspaces/test2");
		setCustomizedStartScriptFile();

		// call functionality under test
		executeMogliApplication();

		// cleanup
		resetDefaultStartScriptFile();

		// verify test result
		assertWorkspace(workspaceDir);
	}

	@Test
	public void createsWorkspaceConfiguredInApplicationPropertiesFile() throws Exception {
		// prepare test
		final String workspaceDirName =  "workspaces/test";
		final File applicationPropertiesFile = initApplicationPropertiesFile(
				MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
				 + "/" + workspaceDirName);
		assertFileExists(applicationPropertiesFile);
		final File workspaceDir = initWorkspaceDir(workspaceDirName);

		// call functionality under test
		executeMogliApplication();

		// cleanup
		applicationPropertiesFile.delete();

		// verify test result
		assertWorkspace(workspaceDir);
	}

	@Test
	public void createsDefaultWorkspace() throws Exception {
		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertWorkspace(applicationRootDir);
	}

	@Test
	public void createsReportFilesInCustomWorkspace() throws Exception {
		// prepare test
		final String workspaceDirName =  "workspaces/test";
		final File applicationPropertiesFile = initApplicationPropertiesFile(
				MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER
				 + "/" + workspaceDirName);
		assertFileExists(applicationPropertiesFile);

		final File shortReportFile = new File(applicationRootDir, workspaceDirName + "/" + DIR_REPORT_FILES + "/" 
                                                                   + FILENAME_SHORT_REPORT_FILE);
		shortReportFile.delete();
		assertFileDoesNotExist(shortReportFile);
		
		final File generatorReportFile = new File(applicationRootDir, workspaceDirName + "/" + DIR_REPORT_FILES + "/" 
		                                                             + FILENAME_GENERATION_REPORT_FILE);
		generatorReportFile.delete();
		assertFileDoesNotExist(generatorReportFile);

		
		final File providerReportFile = new File(applicationRootDir, workspaceDirName + "/" + DIR_REPORT_FILES + "/" 
                                                 + FILENAME_PROVIDER_REPORT_FILE);
		providerReportFile.delete();
		assertFileDoesNotExist(providerReportFile);

		// call functionality under test
		executeMogliApplication();

		// cleanup
		applicationPropertiesFile.delete();

		// verify test result
		assertFileExists(shortReportFile);
		assertFileExists(providerReportFile);
		assertFileExists(generatorReportFile);
	}

	// *************************************************************************************
	//                       helper methods
	// *************************************************************************************


	private void assertWorkspace(final File workspaceDir) {
		assertFileExists(workspaceDir);
		final File inputDir = new File(workspaceDir, MOGLiSystemConstants.DIR_INPUT_FILES);
		assertFileExists(inputDir);
		final File outputDir = new File(workspaceDir, MOGLiSystemConstants.DIR_OUTPUT_FILES);
		assertFileExists(outputDir);
		final File logsDir = new File(workspaceDir, MOGLiSystemConstants.DIR_LOGS_FILES);
		assertFileExists(logsDir);

		assertWorkspacePropertiesFile(workspaceDir);
	}

	private void assertWorkspacePropertiesFile(File workspaceDir) {
		final File workspacePropertiesFile = new File(workspaceDir, MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES);
		assertFileExists(workspacePropertiesFile);
		final File expectedFile = new File(PROJECT_ROOT_DIR + "../core/src/main/resources/" + PROPERTIES_DIR + "/"
				                           + MOGLiSystemConstants.FILENAME_WORKSPACE_PROPERTIES);
		assertFileEquals(expectedFile, workspacePropertiesFile);
	}

	private File initApplicationPropertiesFile(final String workspaceDirName) throws Exception {
		final File applicationPropertiesFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_APPLICATION_PROPERTIES);
		FileUtil.createNewFileWithContent(applicationPropertiesFile, MOGLiSystemConstants.WORKSPACE_PROPERTY + "=" + workspaceDirName);
		return applicationPropertiesFile;
	}

	private File initWorkspaceDir(final String workspaceDirName) throws Exception {
		final File workspaceDir = new File(applicationRootDir, workspaceDirName);
		FileUtil.deleteDirWithContent(workspaceDir);
		assertFileDoesNotExist(workspaceDir);
		return workspaceDir;
	}

	private void setCustomizedStartScriptFile() {
		final File sourceFile;
		final File targetFile;
		
		if (OSUtil.isWindows()) {			
			sourceFile = new File(getProjectTestResourcesDir(), "startBatWithCustomizedWorkspaceDir.bat");
			targetFile = new File(applicationRootDir, "startMOGLiCodeCreator.bat");
		} else {
			sourceFile = new File(getProjectTestResourcesDir(), "startShWithCustomizedWorkspaceDir.sh");
			targetFile = new File(applicationRootDir, "startMOGLiCodeCreator.sh");
		}
		
		MOGLiFileUtil.createNewFileWithContent(targetFile, MOGLiFileUtil.getFileContent(sourceFile));
	}

	private void resetDefaultStartScriptFile() {
		final File sourceFile;
		final File targetFile;

		if (OSUtil.isWindows()) {
			sourceFile = new File(getProjectResourcesDir(), "release/startMOGLiCodeCreator.bat");
			targetFile = new File(applicationRootDir, "startMOGLiCodeCreator.bat");			
		} else {
			sourceFile = new File(getProjectResourcesDir(), "release/startMOGLiCodeCreator.sh");
			targetFile = new File(applicationRootDir, "startMOGLiCodeCreator.sh");			
		}
		
		MOGLiFileUtil.createNewFileWithContent(targetFile, MOGLiFileUtil.getFileContent(sourceFile));
	}
}