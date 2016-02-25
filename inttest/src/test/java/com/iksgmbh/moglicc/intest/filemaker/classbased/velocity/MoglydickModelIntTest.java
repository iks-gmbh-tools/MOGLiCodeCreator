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
package com.iksgmbh.moglicc.intest.filemaker.classbased.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.utils.FileUtil;

public class MoglydickModelIntTest extends IntTestParent 
{
	@Test
	public void buildsConsoleComicStripShellScript() throws MOGLiPluginException, IOException {
		// prepare test
		createModelPropertiesFileWithContent("modelfile=Moglydick.txt");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File outputDir = new File(infrastructure.getPluginOutputDir(), "ConsoleComicStrip");
		assertChildrenNumberInDirectory(outputDir, 42);
		final File file = new File(infrastructure.getApplicationRootDir(), "Moglydick.sh");
		assertFileExists(file);
		final List<String> content = FileUtil.getFileContentAsList(file);
		assertEquals("lines in file", 21851, content.size());
	}


}