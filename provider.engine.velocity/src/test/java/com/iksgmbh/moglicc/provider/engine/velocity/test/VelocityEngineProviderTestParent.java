package com.iksgmbh.moglicc.provider.engine.velocity.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MOGLiCodeCreator2;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MOGLiInfrastructure2;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineProviderStarter;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.utils.FileUtil;

public class VelocityEngineProviderTestParent extends AbstractMOGLiTest {
	
	public static final String PROJECT_ROOT_DIR = "../provider.engine.velocity/";
	public static final String TEMPLATE_WITH_SUBTEMPLATE = "testMainTemplateWithSub.tpl";
	public static final String SUBTEMPLATE = "testSubTemplate.tpl";
	public static final String TEMPLATE_NO_INCLUDED_SUBTEMPLATE = "testMainTemplateNoSub.tpl";
	public static final String INSERTER_TARGET_FILE = "testInserterTargetFile.txt";
	
	protected VelocityEngineProviderStarter velocityEngineProvider;
	protected File generatorPluginInputDir;
	protected File generatorPluginInputDirWithArtefactSubDir;
			
	@Override
	public void setup() {
		super.setup();
		
		velocityEngineProvider = new VelocityEngineProviderStarter();
		velocityEngineProvider.setMOGLiInfrastructure(createInfrastructure());
		
		generatorPluginInputDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID);
		generatorPluginInputDirWithArtefactSubDir = new File(generatorPluginInputDir, MockDataBuilder.ARTEFACT_TYPE);
		
		FileUtil.deleteDirWithContent(generatorPluginInputDir);
		FileUtil.deleteDirWithContent(applicationOutputDir);
		FileUtil.deleteDirWithContent(applicationTempDir);
		
		generatorPluginInputDirWithArtefactSubDir.mkdirs();
	}
	
	protected void createInputTestFiles() {
		File templateSource = new File(getProjectTestResourcesDir(), TEMPLATE_WITH_SUBTEMPLATE);
		File template = new File(generatorPluginInputDir, TEMPLATE_WITH_SUBTEMPLATE);
		FileUtil.copyTextFile(templateSource, template);
		final File subtemplateSource = new File(getProjectTestResourcesDir(), SUBTEMPLATE);
		final File subtemplate = new File(generatorPluginInputDir, SUBTEMPLATE);
		FileUtil.copyTextFile(subtemplateSource, subtemplate);

		templateSource = new File(getProjectTestResourcesDir(), TEMPLATE_NO_INCLUDED_SUBTEMPLATE);
		template = new File(generatorPluginInputDirWithArtefactSubDir, TEMPLATE_NO_INCLUDED_SUBTEMPLATE);
		FileUtil.copyTextFile(templateSource, template);
	}
	
	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MOGLiCodeCreator2.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

	protected InfrastructureService createInfrastructure() {
		final List<MOGLiPlugin2> list = new ArrayList<MOGLiPlugin2>();
		list.add(new DummyGeneratorStarter());

		final InfrastructureInitData infrastructureInitData =
			       createInfrastructureInitData(applicationProperties, list, VelocityEngineProviderStarter.PLUGIN_ID);
		return new MOGLiInfrastructure2(infrastructureInitData);
	}
}
