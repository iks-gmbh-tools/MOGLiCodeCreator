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
package com.iksgmbh.moglicc.treebuilder.modelbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.test.plugin.ModelProviderDummy;
import com.iksgmbh.moglicc.test.plugin.VelocityEngineProviderDummy;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedTreeBuilderTestParent extends AbstractMOGLiTest {

	public static final String PROJECT_ROOT_DIR = "../treebuilder.modelbased.velocity/";
	public static final String METAINFO_MODEL_TARGETDIR = "ModelTargetTestDir";
	public static final String METAINFO_MODEL_PROJECT_DESCRIPTION = "Description of project";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";

	protected File generatorPluginInputDir;
	protected VelocityModelBasedTreeBuilderStarter treeBuilderGenerator;
	protected VelocityEngineProviderDummy velocityEngineProviderDummy;
	protected InfrastructureService infrastructure;

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
		return VelocityModelBasedTreeBuilderStarter.PLUGIN_ID;
	}

	@Override
	public void setup() {
		super.setup();

		velocityEngineProviderDummy = new VelocityEngineProviderDummy();
		infrastructure = createInfrastructure();
		treeBuilderGenerator = new VelocityModelBasedTreeBuilderStarter();
		treeBuilderGenerator.setInfrastructure(infrastructure);
		generatorPluginInputDir = new File(applicationInputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID);

		FileUtil.deleteDirWithContent(applicationRootDir);
		applicationRootDir.mkdirs();
		applicationTempDir.mkdirs();

		initPluginInputDirWithDefaultDataIfNotExisting();
		
		giveSystemTimeToExecute();
	}


	protected InfrastructureService createInfrastructure() {
		final List<MOGLiPlugin> list = new ArrayList<MOGLiPlugin>();

		final InfrastructureInitData infrastructureInitData =
			   createInfrastructureInitData(applicationProperties, list, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID);

		addPluginList(infrastructureInitData);
		return new MOGLiInfrastructure(infrastructureInitData);
	}

	private void addPluginList(InfrastructureInitData infrastructureInitData) {
		final List<MOGLiPlugin> list = new ArrayList<MOGLiPlugin>();
		list.add((MOGLiPlugin) getDummyModelProvider("MOGLiCC_JavaBeanModel", getStandardModelMetaInfos()));
		list.add(velocityEngineProviderDummy);
		infrastructureInitData.pluginList = list;
	}

	protected HashMap<String, String> getStandardModelMetaInfos() {
		final HashMap<String, String> modelMetaInfos = new HashMap<String, String>();
		modelMetaInfos.put("eclipseProjectDir", MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER);
		modelMetaInfos.put("ProjectName", METAINFO_MODEL_TARGETDIR);
		modelMetaInfos.put("projectDescription", METAINFO_MODEL_PROJECT_DESCRIPTION);
		return modelMetaInfos;
	}

	protected InfrastructureService createInfrastructure(final File inputDir) {
		final InfrastructureInitData infrastructureInitData = new InfrastructureInitData(applicationRootDir,
				applicationLogDir, applicationOutputDir, applicationTempDir, inputDir, applicationHelpDir,
				applicationProperties);

		addPluginList(infrastructureInitData);

		infrastructureInitData.idOfThePluginToThisInfrastructure = VelocityModelBasedTreeBuilderStarter.PLUGIN_ID;
		return new MOGLiInfrastructure(infrastructureInitData);
	}

	protected ModelProvider getDummyModelProvider(final String modelName, final HashMap<String, String> modelMetaInfos) {
		return new ModelProviderDummy(MODEL_PROVIDER_ID, getDummyModel(modelName, modelMetaInfos));
	}


	protected Model getDummyModel(final String modelName, final HashMap<String, String> modelMetaInfos) {
		return new DummyModel(modelName, modelMetaInfos);
	}

	protected Model getDummyModel(final String modelName) {
		return new DummyModel(modelName, null);
	}

	class DummyModel implements Model {

		private HashMap<String, String> modelMetaInfos;
		private String modelName;

		public DummyModel(final String modelName, final HashMap<String, String> modelMetaInfos) {
			this.modelMetaInfos = modelMetaInfos;
			this.modelName = modelName;
		}

		@Override
		public String getMetaInfoValueFor(final String metaInfoName) {
			if (modelMetaInfos == null || modelMetaInfos.get(metaInfoName) == null) {
				return null;
			}
			return modelMetaInfos.get(metaInfoName);
		}

		@Override
		public List<String> getAllMetaInfoValuesFor(String metaInfoName) {
			return null;
		}

		@Override
		public String getCommaSeparatedListOfAllMetaInfoValuesFor(String metaInfoName) {
			return null;
		}

		@Override
		public List<MetaInfo> getMetaInfoList() {
			return null;
		}

		@Override
		public List<MetaInfo> getAllMetaInfos() {
			return null;
		}

		@Override
		public boolean doesHaveMetaInfo(String metaInfoName, String value) {
			return false;
		}

		@Override
		public boolean doesHaveAnyMetaInfosWithName(String metaInfoName) {
			return false;
		}

		@Override
		public List<ClassDescriptor> getClassDescriptorList() {
			return null;
		}

		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public String getName() {
			return modelName;
		}

		@Override
		public boolean isValueAvailable(final String metaInfoValue) {
			if (metaInfoValue == null) {
				return false;
			}
			return ! (metaInfoValue.startsWith(META_INFO_NOT_FOUND_START) && metaInfoValue.endsWith(META_INFO_NOT_FOUND_END));
		}

		@Override
		public List<MetaInfo> getMetaInfosWithNameStartingWith(String prefix)
		{
			return null;
		}

		@Override
		public ClassDescriptor getClassDescriptor(String classname)
		{
			return null;
		}

		@Override
		public boolean isValueNotAvailable(String metaInfoValue)
		{
			return false;
		}

	}
}