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
package com.iksgmbh.moglicc.test;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class ExcelStandardModelProviderTestParent extends AbstractMOGLiTest {

	protected static final String PROJECT_ROOT_DIR = "../provider.model.standard.excel/";
	protected static final String TESTFILENAME = "TestDataExample.xlsx";

	private static boolean isFirstTest = true;
	
	protected MOGLiInfrastructure infrastructure;
	protected File propertiesFile;
	

	@Override
	public void setup() {
		super.setup();
		final File pluginInputDir = new File(applicationRootDir + "/" + DIR_INPUT_FILES + "/" 
				+ ExcelStandardModelProviderStarter.PLUGIN_ID); 
		if (isFirstTest) {
			isFirstTest = false;
			initForFirstUnitTest();
			pluginInputDir.mkdirs();
		}
		final List<MOGLiPlugin> emptyImmutableList = ImmutableUtil.getEmptyImmutableListOf(null);
		final InfrastructureInitData initInfrastructureData = 
			createInfrastructureInitData(null, emptyImmutableList, ExcelStandardModelProviderStarter.PLUGIN_ID);
		infrastructure = new MOGLiInfrastructure(initInfrastructureData);
		propertiesFile = new File(infrastructure.getPluginInputDir(), ExcelStandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
	}
	
	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String getPluginId() {
		return ExcelStandardModelProviderStarter.PLUGIN_ID;
	}	

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}
	
	protected List<String> getFileContent(final String... lines) {
		final List<String> fileContentAsList = new ArrayList<String>();
		for (String line : lines) {
			fileContentAsList.add(line);
		}
		return fileContentAsList;
	}
	
	protected void createPropertiesFileWith(final String content)
	{
		try {
			FileUtil.createNewFileWithContent(propertiesFile, content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}