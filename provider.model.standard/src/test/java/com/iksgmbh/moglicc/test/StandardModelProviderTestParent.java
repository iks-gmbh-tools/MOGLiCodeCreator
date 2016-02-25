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
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.utils.ImmutableUtil;

public class StandardModelProviderTestParent extends AbstractMOGLiTest {

	protected static final String PROJECT_ROOT_DIR = "../provider.model.standard/";
	private static boolean isFirstTest = true;
	
	protected MOGLiInfrastructure infrastructure;
	protected File modelTextfile;
	

	@Override
	public void setup() {
		super.setup();
		final File pluginInputDir = new File(applicationRootDir + "/" + DIR_INPUT_FILES + "/" 
				+ StandardModelProviderStarter.PLUGIN_ID); 
		if (isFirstTest) {
			isFirstTest = false;
			initForFirstUnitTest();
			pluginInputDir.mkdirs();
		}
		modelTextfile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		modelTextfile.delete();
		final List<MOGLiPlugin> emptyImmutableList = ImmutableUtil.getEmptyImmutableListOf(null);
		final InfrastructureInitData initInfrastructureData = 
			createInfrastructureInitData(null, emptyImmutableList, StandardModelProviderStarter.PLUGIN_ID);
		infrastructure = new MOGLiInfrastructure(initInfrastructureData);
	}
	
	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String getPluginId() {
		return StandardModelProviderStarter.PLUGIN_ID;
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
}