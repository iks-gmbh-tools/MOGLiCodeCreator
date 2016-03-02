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

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class F_ExcelStandardModelProviderAcceptanceSystemTest extends __AbstractSystemTest {
	
	private static final String PROVIDER_PLUGIN_ID = ExcelStandardModelProviderStarter.PLUGIN_ID;

	// *****************************  test methods  ************************************
		
	@Test
	public void createsDefaultPropertiesFileOfExcelStandardModelProvider() {
		// prepare test
		final File modelDir = new File(testDir + "/" + DIR_INPUT_FILES + "/" + PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(modelDir);
		assertFileDoesNotExist(modelDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		final File modelFile = new File(modelDir, ExcelStandardModelProviderStarter.FILENAME_STANDARD_EXCEL_FILE); 
		assertFileExists(modelFile);
		String fileContent = MOGLiFileUtil.getFileContent(modelFile);
		assertFileContainsEntry(modelFile, fileContent);
	}
	
	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 1);
	}
	
	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, PROVIDER_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginLogFile);
	}
}