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
package com.iksgmbh.moglicc.filemaker.classbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.filemaker.classbased.velocity.VelocityClassBasedFileMakerStarter;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedFileMakerTestParent extends AbstractMOGLiTest {
	
	public static final String PROJECT_ROOT_DIR = "../filemaker.classbased.velocity/";
	
	protected File generatorPluginInputDir;
	protected VelocityClassBasedFileMakerStarter velocityClassBasedGenerator;
	protected InfrastructureService infrastructure;
	protected DummyVelocityEngineProviderStarter velocityEngineProvider;

	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}
	
	@Override
	protected String getPluginId() {
		return VelocityClassBasedFileMakerStarter.PLUGIN_ID;
	}	
	
	@Override
	public void setup() {
		super.setup();
		
		infrastructure = createInfrastructure();
		velocityClassBasedGenerator = new VelocityClassBasedFileMakerStarter();
		velocityClassBasedGenerator.setInfrastructure(infrastructure);
		velocityClassBasedGenerator.setTestDir(PROJECT_ROOT_DIR + TEST_SUBDIR);
		generatorPluginInputDir = new File(applicationInputDir, VelocityClassBasedFileMakerStarter.PLUGIN_ID);
		
		FileUtil.deleteDirWithContent(applicationRootDir);
		applicationRootDir.mkdirs();
		applicationTempDir.mkdirs();

		initPluginInputDirWithDefaultDataIfNotExisting();
	}
	
	
	protected InfrastructureService createInfrastructure() {
		final List<MOGLiPlugin> list = new ArrayList<MOGLiPlugin>();
		try {
			list.add((MOGLiPlugin) MockDataBuilder.getStandardModelProvider());
			velocityEngineProvider = (DummyVelocityEngineProviderStarter) MockDataBuilder.getVelocityEngineProvider();
			list.add((MOGLiPlugin) velocityEngineProvider);
		} catch (MOGLiPluginException e) {
			org.junit.Assert.fail(e.getMessage());
		}
		final InfrastructureInitData infrastructureInitData = 
			     createInfrastructureInitData(applicationProperties, list, VelocityClassBasedFileMakerStarter.PLUGIN_ID);
		return new MOGLiInfrastructure(infrastructureInitData);
	}
}