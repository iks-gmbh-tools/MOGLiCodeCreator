package com.iksgmbh.moglicc.intest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityClassBasedGeneratorStarter;
import com.iksgmbh.moglicc.generator.modelbased.filestructure.FilestructureModelBasedGeneratorStarter;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityModelBasedInserterStarter;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class IntTestParent extends AbstractMOGLiTest {

	public static final String PROJECT_ROOT_DIR = "../inttest/";

	protected String applicationRootDir;
	protected InfrastructureInitData infrastructureInitData;
	
	// plugin starter instances
	/** ADD INSTANCES FOR NEW PLUGINS BELOW THIS LINE - DO NOT MODIFY THIS LINE, IT IS A MARKER LINE FOR MOGLiCC-INSERTER ! */ 
	protected VelocityClassBasedGeneratorStarter velocityClassBasedGeneratorStarter;
	protected VelocityModelBasedInserterStarter velocityModelBasedInserterStarter;
	protected StandardModelProviderStarter standardModelProviderStarter;
	protected FilestructureModelBasedGeneratorStarter filestructureModelBasedGeneratorStarter;
	protected VelocityEngineProviderStarter velocityEngineProviderStarter;

	protected File modelFile;
	protected File modelPropertiesFile;

	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String getPluginId() {
		return null;  // no specific plugin is tested here
	}

	@Override
	protected String initTestApplicationRootDir() {
		applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		FileUtil.deleteDirWithContent(applicationRootDir);
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

	@Before
	public void setup() {
		super.setup();
		
		applicationLogDir.mkdirs();

		final List<MOGLiPlugin> plugins = new ArrayList<MOGLiPlugin>();
		infrastructureInitData = createInfrastructureInitData(applicationProperties, plugins, null);

		/** CREATE STARTER INSTANCES FOR NEW PLUGINS BELOW THIS LINE - DO NOT MODIFY THIS LINE, IT IS A MARKER LINE FOR MOGLiCC-INSERTER ! */ 
		filestructureModelBasedGeneratorStarter = new FilestructureModelBasedGeneratorStarter();
		plugins.add(filestructureModelBasedGeneratorStarter);
		standardModelProviderStarter = new StandardModelProviderStarter();
		plugins.add(standardModelProviderStarter);
		velocityEngineProviderStarter = new VelocityEngineProviderStarter();
		plugins.add(velocityEngineProviderStarter);
		velocityClassBasedGeneratorStarter = new VelocityClassBasedGeneratorStarter();
		plugins.add(velocityClassBasedGeneratorStarter);
		velocityModelBasedInserterStarter = new VelocityModelBasedInserterStarter();
		plugins.add(velocityModelBasedInserterStarter);


		try {
			/** INIT STARTER INSTANCES FOR NEW PLUGINS BELOW THIS LINE - DO NOT MODIFY THIS LINE, IT IS A MARKER LINE FOR MOGLiCC-INSERTER ! */ 
			initPlugin(filestructureModelBasedGeneratorStarter);
			initPlugin(standardModelProviderStarter);
			initPlugin(velocityEngineProviderStarter);
			initPlugin(velocityClassBasedGeneratorStarter);
			initPlugin(velocityModelBasedInserterStarter);
		} catch (MOGLiPluginException e) {
			throw new RuntimeException(e);
		}

		final File pluginInputDir = standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir();
		modelFile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE);
		modelPropertiesFile = new File(pluginInputDir, StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
	}

	protected MOGLiInfrastructure initPlugin(final MOGLiPlugin plugin) throws MOGLiPluginException {
		infrastructureInitData.idOfThePluginToThisInfrastructure = plugin.getId();
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(infrastructureInitData);
		plugin.setMOGLiInfrastructure(infrastructure);
		plugin.unpackDefaultInputData();
		plugin.unpackPluginHelpFiles();
		return infrastructure;
	}

	protected void setModelFile(final String filename) {
		final File source = new File(getProjectTestResourcesDir(), filename);
		FileUtil.copyTextFile(source, modelFile);
	}

	protected void createModelFile(final String sourceFilename, final String targetFileName) {
		final File sourceFile = new File(getProjectTestResourcesDir(), sourceFilename);
		final File targetFile = new File(modelFile.getParentFile(), targetFileName);
		FileUtil.copyTextFile(sourceFile, targetFile);
	}


	protected void createModelPropertiesFileWithContent(final String content) {
		MOGLiFileUtil.createNewFileWithContent(modelPropertiesFile, content);
	}

	protected void setMetaInfoValidationFile(final MOGLiPlugin plugin,
			                                 final String filename) {
		final File source = new File(getProjectTestResourcesDir(), filename);
		final File target = new File(plugin.getMOGLiInfrastructure().getPluginInputDir(),
				                     MetaInfoValidationUtil.FILENAME_VALIDATION);
		FileUtil.copyTextFile(source, target);
	}

}
