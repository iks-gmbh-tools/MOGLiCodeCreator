package com.iksgmbh.moglicc.intest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.iksgmbh.moglicc.MOGLiCodeCreator2;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityClassBasedGeneratorStarter;
import com.iksgmbh.moglicc.generator.utils.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure2;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityModelBasedInserterStarter;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.utils.FileUtil;

public class IntTestParent extends AbstractMOGLiTest {

	public static final String PROJECT_ROOT_DIR = "../inttest/";
	
	protected InfrastructureInitData infrastructureInitData;
	protected VelocityClassBasedGeneratorStarter velocityClassBasedGeneratorStarter;
	protected VelocityModelBasedInserterStarter velocityModelBasedInserterStarter;
	protected StandardModelProviderStarter standardModelProviderStarter;
	protected VelocityEngineProviderStarter velocityEngineProviderStarter;
	protected String applicationRootDir;
	
	private File modelTextfile;
	
	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}
	
	@Override
	protected String initTestApplicationRootDir() {
		applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		FileUtil.deleteDirWithContent(applicationRootDir);
		MOGLiCodeCreator2.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

	@Before
	public void setup() {
		super.setup();
		applicationLogDir.mkdirs();
		
		final List<MOGLiPlugin2> plugins = new ArrayList<MOGLiPlugin2>();
		infrastructureInitData = createInfrastructureInitData(applicationProperties, plugins, null);
		
		standardModelProviderStarter = new StandardModelProviderStarter();
		plugins.add(standardModelProviderStarter);
		velocityEngineProviderStarter = new VelocityEngineProviderStarter();
		plugins.add(velocityEngineProviderStarter);
		velocityClassBasedGeneratorStarter = new VelocityClassBasedGeneratorStarter();
		plugins.add(velocityClassBasedGeneratorStarter);
		velocityModelBasedInserterStarter = new VelocityModelBasedInserterStarter();
		plugins.add(velocityModelBasedInserterStarter);
		
		try {
			initPlugin(standardModelProviderStarter);
			initPlugin(velocityEngineProviderStarter);
			initPlugin(velocityClassBasedGeneratorStarter);
			initPlugin(velocityModelBasedInserterStarter);
		} catch (MOGLiPluginException2 e) {
			throw new RuntimeException(e);
		}
		
		final File pluginInputDir = standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir();
		modelTextfile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE);
	}
	
	protected MOGLiInfrastructure2 initPlugin(final MOGLiPlugin2 plugin) throws MOGLiPluginException2 {
		infrastructureInitData.idOfThePluginToThisInfrastructure = plugin.getId();
		final MOGLiInfrastructure2 infrastructure = new MOGLiInfrastructure2(infrastructureInitData);
		plugin.setMOGLiInfrastructure(infrastructure);
		plugin.unpackDefaultInputData();
		return infrastructure;
	}

	protected void setModelFile(final String filename) {
		final File source = new File(getProjectTestResourcesDir(), filename);
		FileUtil.copyTextFile(source, modelTextfile);
	}	

	protected void setMetaInfoValidationFile(final MOGLiPlugin2 plugin,
			                                 final String filename) {
		final File source = new File(getProjectTestResourcesDir(), filename);
		final File target = new File(plugin.getMOGLiInfrastructure().getPluginInputDir(), 
				                     MetaInfoValidationUtil.FILENAME_VALIDATION);
		FileUtil.copyTextFile(source, target);
	}

}
