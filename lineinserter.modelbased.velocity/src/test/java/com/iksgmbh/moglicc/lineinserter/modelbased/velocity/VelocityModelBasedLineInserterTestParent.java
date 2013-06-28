package com.iksgmbh.moglicc.lineinserter.modelbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.lineinserter.modelbased.velocity.VelocityModelBasedLineInserterStarter;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedLineInserterTestParent extends AbstractMOGLiTest {

	public static final String PROJECT_ROOT_DIR = "../lineinserter.modelbased.velocity/";

	private static boolean isFirstTime = true;

	protected File generatorPluginInputDir;
	protected VelocityModelBasedLineInserterStarter velocityModelBasedLineInserter;
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
		return VelocityModelBasedLineInserterStarter.PLUGIN_ID;
	}

	@Override
	public void setup() {
		super.setup();

		infrastructure = createInfrastructure();
		velocityModelBasedLineInserter = new VelocityModelBasedLineInserterStarter();
		velocityModelBasedLineInserter.setInfrastructure(infrastructure);
		generatorPluginInputDir = new File(applicationInputDir, VelocityModelBasedLineInserterStarter.PLUGIN_ID);

		if (isFirstTime) {
			isFirstTime = false;
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			applicationTempDir.mkdirs();
		}

		initPluginInputDirWithDefaultDataIfNotExisting();
	}

	private void fail(String string) {
	}

	protected InfrastructureService createInfrastructure() {
		final List<MOGLiPlugin> list = new ArrayList<MOGLiPlugin>();
		try {
			list.add((MOGLiPlugin) MockDataBuilder.getStandardModelProvider());
			velocityEngineProvider = (DummyVelocityEngineProviderStarter) MockDataBuilder.getVelocityEngineProvider();
			list.add((MOGLiPlugin) velocityEngineProvider);
		} catch (MOGLiPluginException e) {
			fail(e.getMessage());
		}
		final InfrastructureInitData infrastructureInitData =
			   createInfrastructureInitData(applicationProperties, list, VelocityModelBasedLineInserterStarter.PLUGIN_ID);
		return new MOGLiInfrastructure(infrastructureInitData);
	}

	protected InfrastructureService createInfrastructure(final File inputDir) {
		final InfrastructureInitData infrastructureInitData = new InfrastructureInitData(applicationRootDir,
				applicationLogDir, applicationOutputDir, applicationTempDir, inputDir, applicationHelpDir,
				applicationProperties);
		final List<MOGLiPlugin> list = new ArrayList<MOGLiPlugin>();
		try {
			list.add((MOGLiPlugin) MockDataBuilder.getStandardModelProvider());
			velocityEngineProvider = (DummyVelocityEngineProviderStarter) MockDataBuilder.getVelocityEngineProvider();
			list.add((MOGLiPlugin) velocityEngineProvider);
		} catch (MOGLiPluginException e) {
			fail(e.getMessage());
		}
		infrastructureInitData.pluginList = list;
		infrastructureInitData.idOfThePluginToThisInfrastructure = VelocityModelBasedLineInserterStarter.PLUGIN_ID;
		return new MOGLiInfrastructure(infrastructureInitData);
	}

}
