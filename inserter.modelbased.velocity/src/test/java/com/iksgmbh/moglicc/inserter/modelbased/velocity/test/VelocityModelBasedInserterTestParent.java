package com.iksgmbh.moglicc.inserter.modelbased.velocity.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityModelBasedInserterStarter;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest2;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedInserterTestParent extends AbstractMOGLiTest2 {
	
	public static final String PROJECT_ROOT_DIR = "../inserter.modelbased.velocity/";
	
	private static boolean isFirstTime = true;
	
	protected File generatorPluginInputDir;
	protected VelocityModelBasedInserterStarter velocityModelBasedInserter;
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
	public void setup() {
		super.setup();
		
		infrastructure = createInfrastructure();
		velocityModelBasedInserter = new VelocityModelBasedInserterStarter();
		velocityModelBasedInserter.setMOGLiInfrastructure(infrastructure);
		generatorPluginInputDir = new File(applicationInputDir, VelocityModelBasedInserterStarter.PLUGIN_ID);
		
		if (isFirstTime) {
			isFirstTime = false;
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			applicationTempDir.mkdirs();
		}

		createInputTestFilesIfNotExisting();
	}
	
	private void fail(String string) {
	}

	protected void createInputTestFilesIfNotExisting() {
		if (! generatorPluginInputDir.exists()) {
			final List<File> artefactList = getArtefactList();
			for (final File artefactDir : artefactList) {
				copyTemplatesFrom(artefactDir);
			}
		}
	}
	
	private void copyTemplatesFrom(final File artefactDir) {
		final File targetDir = new File(generatorPluginInputDir, artefactDir.getName());
		targetDir.mkdirs();
		
		final File[] templateFiles = artefactDir.listFiles();
		for (int i = 0; i < templateFiles.length; i++) {
			FileUtil.copyTextFile(templateFiles[i], targetDir.getAbsolutePath());
		}
	}

	private List<File> getArtefactList() {
		final List<File> toReturn = new ArrayList<File>();
		final File defaultDataDir = new File(getProjectResourcesDir(), MOGLiPlugin.DEFAULT_DATA_DIR);
		final File[] files = defaultDataDir.listFiles();
		for (final File dir : files) {
			if (dir.isDirectory()) {
				toReturn.add(dir);
			}
		}
		return toReturn;
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
			   createInfrastructureInitData(applicationProperties, list, VelocityModelBasedInserterStarter.PLUGIN_ID);
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
		infrastructureInitData.idOfThePluginToThisInfrastructure = VelocityModelBasedInserterStarter.PLUGIN_ID;
		return new MOGLiInfrastructure(infrastructureInitData);
	}

}
