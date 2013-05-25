package com.iksgmbh.moglicc.generator.classbased.velocity.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityClassBasedGeneratorStarter;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedGeneratorTestParent extends AbstractMOGLiTest {
	
	public static final String PROJECT_ROOT_DIR = "../generator.classbased.velocity/";
	
	private static boolean isFirstTime = true;
	
	protected File generatorPluginInputDir;
	protected VelocityClassBasedGeneratorStarter velocityClassBasedGenerator;
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
		return VelocityClassBasedGeneratorStarter.PLUGIN_ID;
	}	
	
	@Override
	public void setup() {
		super.setup();
		
		infrastructure = createInfrastructure();
		velocityClassBasedGenerator = new VelocityClassBasedGeneratorStarter();
		velocityClassBasedGenerator.setMOGLiInfrastructure(infrastructure);
		velocityClassBasedGenerator.setTestDir(PROJECT_ROOT_DIR + TEST_SUBDIR);
		generatorPluginInputDir = new File(applicationInputDir, VelocityClassBasedGeneratorStarter.PLUGIN_ID);
		
		if (isFirstTime) {
			isFirstTime = false;
			FileUtil.deleteDirWithContent(generatorPluginInputDir);
			applicationTempDir.mkdirs();
		}

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
			     createInfrastructureInitData(applicationProperties, list, VelocityClassBasedGeneratorStarter.PLUGIN_ID);
		return new MOGLiInfrastructure(infrastructureInitData);
	}
}
