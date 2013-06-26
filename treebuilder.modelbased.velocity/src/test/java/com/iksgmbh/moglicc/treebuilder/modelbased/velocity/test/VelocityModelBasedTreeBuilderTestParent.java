package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.type.basic.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.VelocityModelBasedTreeBuilderStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedTreeBuilderTestParent extends AbstractMOGLiTest {

	public static final String PROJECT_ROOT_DIR = "../treebuilder.modelbased.velocity/";
	public static final String METAINFO_MODEL_TARGETDIR = "ModelTargetTestDir";
	public static final String METAINFO_MODEL_PROJECT_DESCRIPTION = "Description of project";

	private static boolean isFirstTime = true;

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
		treeBuilderGenerator.setMOGLiInfrastructure(infrastructure);
		generatorPluginInputDir = new File(applicationInputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID);

		if (isFirstTime) {
			isFirstTime = false;
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			applicationTempDir.mkdirs();
		}

		initPluginInputDirWithDefaultDataIfNotExisting();
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
		return new DummyModelProvider(modelName, modelMetaInfos);
	}

	class DummyModelProvider implements ModelProvider {

		private Model model;

		public DummyModelProvider(final String modelName, final HashMap<String, String> modelMetaInfos) {
			 model = getDummyModel(modelName, modelMetaInfos);
		}

		@Override
		public PluginType getPluginType() {
			return PluginType.MODEL_PROVIDER;
		}

		@Override
		public String getId() {
			return VelocityModelBasedTreeBuilderStarter.MODEL_PROVIDER_ID;
		}

		@Override
		public List<String> getDependencies() {
			return null;
		}

		@Override
		public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
		}

		@Override
		public InfrastructureService getMOGLiInfrastructure() {
			return null;
		}

		@Override
		public void doYourJob() throws MOGLiPluginException {
		}

		@Override
		public boolean unpackDefaultInputData() throws MOGLiPluginException {
			return false;
		}

		@Override
		public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
			return false;
		}

		@Override
		public Model getModel(String pluginId) throws MOGLiPluginException {
			return model;
		}

		@Override
		public String getModelName() {
			return null;
		}

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
			return 0;
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

	}
}
