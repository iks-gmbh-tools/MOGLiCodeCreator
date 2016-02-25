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
package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_TEMP_FILES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.EngineProvider;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class MOGLiInfrastructureUnitTest extends CoreTestParent {

	public static final String PLUGIN_TEST_ID = "PLUGIN_TEST_ID";
	public static final String TEST_PROPERTY = "Test";

	private InfrastructureInitData infrastructureInitData;

	@Before
	public void setup() {
		super.setup();
		final List<MOGLiPlugin> plugins = getPluginListForTest();
		initWorkspacePropertiesWith(PLUGIN_TEST_ID + " = " + TEST_PROPERTY);
		infrastructureInitData = createInfrastructureInitData(workspaceProperties, null, null);
		infrastructureInitData.pluginList = plugins;
	}

	// *****************************  test methods  ************************************

	@Test
	public void returnsWorkspaceProperty() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);

		// call functionality under test
		final String property = infrastructure.getWorkspaceProperty(PLUGIN_TEST_ID);

		// verify test result
		assertNotNull(property);
		assertStringEquals("property", TEST_PROPERTY, property);
	}

	@Test
	public void returnsLogDir() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		final File logsDir = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES);
		FileUtil.deleteDirWithContent(logsDir);
		assertFalse("LogDir not deleted: " + logsDir.getAbsolutePath(), logsDir.exists());

		// call functionality under test
		File actualLogsDir = infrastructure.getWorkspaceLogsDir();

		// verify test result
		assertStringEquals("Wrong logDir created", logsDir.getAbsolutePath(), actualLogsDir.getAbsolutePath());
	}

	@Test
	public void returnsPluginInputDir() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);

		// call functionality under test
		File actualPluginInputDir = infrastructure.getPluginInputDir();

		// verify test result
		final File applicationInputDir = MOGLiFileUtil.getNewFileInstance(DIR_INPUT_FILES);
		assertStringEquals("Wrong logDir created", applicationInputDir.getAbsolutePath() + File.separator + PLUGIN_TEST_ID, 
				                                   actualPluginInputDir.getAbsolutePath());
	}

	@Test
	public void createsPluginTempDir() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		final File tempDir = MOGLiFileUtil.getNewFileInstance(DIR_TEMP_FILES);
		FileUtil.deleteDirWithContent(tempDir);
		assertFileDoesNotExist(tempDir);

		// call functionality under test
		final File actualPluginTempDir = infrastructure.getPluginTempDir();

		// verify test result
		assertStringEquals("Wrong tempDir created", tempDir.getAbsolutePath() + File.separator + PLUGIN_TEST_ID, 
				                                    actualPluginTempDir.getAbsolutePath());
	}

	@Test
	public void createsWorkspaceTempDir() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		final File tempDir = MOGLiFileUtil.getNewFileInstance(DIR_TEMP_FILES);
		FileUtil.deleteDirWithContent(tempDir);
		assertFileDoesNotExist(tempDir);

		// call functionality under test
		final File actualWorkspaceTempDir = infrastructure.getWorkspaceTempDir();

		// verify test result
		assertStringEquals("Wrong tempDir created", tempDir.getAbsolutePath(), actualWorkspaceTempDir.getAbsolutePath());
	}

	@Test
	public void createsPluginOutputDir() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		final File resultDir = MOGLiFileUtil.getNewFileInstance(DIR_OUTPUT_FILES);
		FileUtil.deleteDirWithContent(resultDir);
		assertFileDoesNotExist(resultDir);

		// call functionality under test
		final File pluginResultDir = infrastructure.getPluginOutputDir();

		// verify test result
		assertFileExists(pluginResultDir);
		assertStringEquals("PluginResultDir", resultDir.getAbsolutePath() + File.separator + PLUGIN_TEST_ID, 
				                              pluginResultDir.getAbsolutePath());
	}


	@Test
	public void createsPluginLogFile() throws IOException {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		final File logDir = MOGLiFileUtil.getNewFileInstance(DIR_LOGS_FILES);
		FileUtil.deleteDirWithContent(logDir);
		assertFileDoesNotExist(logDir);

		// call functionality under test
		final File pluginLogFile = infrastructure.getPluginLogFile();

		// verify test result
		assertFileExists(pluginLogFile);
		assertStringEquals("PluginResultDir", logDir.getAbsolutePath() + File.separator + PLUGIN_TEST_ID + ".log", 
				                              pluginLogFile.getAbsolutePath());
	}

	@Test
	public void returnsGenerator() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);

		// call functionality under test
		GeneratorPlugin generator = infrastructure.getGenerator("null");

		// verify test result
		assertNull("Null expected", generator);
		generator = infrastructure.getGenerator("DummyGenerator");
		assertNotNull("Not null expected", generator);
	}

	@Test
	public void returnsModelProvider() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);

		// call functionality under test
		ModelProvider modelProvider = (ModelProvider) infrastructure.getProvider("null");

		// verify test result
		assertNull("Null expected", modelProvider);
		modelProvider = (ModelProvider) infrastructure.getProvider("StandardModelProvider");
		assertNotNull("Not null expected", modelProvider);
	}


	@Test
	public void returnsEngineProvider() {
		// prepare test
		infrastructureInitData.idOfThePluginToThisInfrastructure = PLUGIN_TEST_ID;
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);

		// call functionality under test
		EngineProvider engineProvider = (EngineProvider) infrastructure.getProvider("null");

		// verify test result
		assertNull("Null expected", engineProvider);
		engineProvider = (EngineProvider) infrastructure.getProvider("VelocityEngineProvider");
		assertNotNull("Not null expected", engineProvider);
	}

}