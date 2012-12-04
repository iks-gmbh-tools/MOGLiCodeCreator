package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.utils.FileUtil;

public class VelocityEngineProviderModelBasedUnitTest extends VelocityEngineProviderTestParent {
	
	private static final String TARGET_FILE_SUBDIR = "inserterTargetFiles";
	private static final String TARGET_FILENAME = "testInserterTargetFile.txt";
	private static final String INSERTER_TEMPLATES_REPLACE_INSTRUCTIONS = "testInserterTemplateReplace.tpl";
		
	private File targetFile;
		
	@Before
	@Override
	public void setup() {
		super.setup();
		
		targetFile = new File(applicationTempDir, TARGET_FILENAME);
		targetFile.delete();
		copyFromProjectTestResourcesDirToInputDir(TARGET_FILE_SUBDIR, TARGET_FILENAME);
	}


	protected void copyFromProjectTestResourcesDirToInputDir(String subDir, final String filename) {
		if (StringUtils.isNotEmpty(subDir)) {
			subDir = "/" + subDir;
		} else {
			subDir = "";
		}
		final File templateSource = new File(getProjectTestResourcesDir() + subDir, filename);
		final File template = new File(generatorPluginInputDir, filename);
		FileUtil.copyTextFile(templateSource, template);
	}
	

	@Test
	public void generatesAllClassesIntoContent() throws MOGLiPluginException {
		// prepare test
		copyFromProjectTestResourcesDirToInputDir("inserterTemplates", INSERTER_TEMPLATES_REPLACE_INSTRUCTIONS);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData( 
				                                    INSERTER_TEMPLATES_REPLACE_INSTRUCTIONS, generatorPluginInputDir);
		velocityEngineProvider.setEngineData(engineData);		

		// call functionality under test
		final BuildUpGeneratorResultData buildUpVelocityResultData = (BuildUpGeneratorResultData) 
		                                 velocityEngineProvider.startEngineWithModel();
		
		// verify test result
		assertNotNull("Not null expected", buildUpVelocityResultData);
		assertStringContains(buildUpVelocityResultData.getGeneratedContent(), MockDataBuilder.TEST_CLASS_NAME1);
		assertStringContains(buildUpVelocityResultData.getGeneratedContent(), MockDataBuilder.TEST_CLASS_NAME2);
	}
}
