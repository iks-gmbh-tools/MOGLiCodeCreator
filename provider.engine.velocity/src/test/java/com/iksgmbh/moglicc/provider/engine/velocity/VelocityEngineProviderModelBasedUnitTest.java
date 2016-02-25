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