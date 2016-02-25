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
package com.iksgmbh.moglicc.provider.engine.velocity.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.utils.FileUtil;

public class MergeResultAnalyserUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void analysesGeneratorTemplateFile() throws Exception {
		// prepare test
		final File originalTemplateFile = new File(getProjectTestResourcesDir(), TEMPLATE_NO_INCLUDED_SUBTEMPLATE);
		final String textToParse = FileUtil.getFileContent(originalTemplateFile);

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse, null);

		// verify test result
		assertNotNull("Not null expected", buildUpGeneratorResultData);
		final String velocityFileContent = "public class ${classDescriptor.simpleName} {" + FileUtil.getSystemLineSeparator() + "}";
		assertEquals("Result File Content", velocityFileContent, buildUpGeneratorResultData.getGeneratedContent());
		assertEquals("Number of Properties", 2, buildUpGeneratorResultData.getPropertiesNumber());
	}

	@Test
	public void removesCommentLines() throws MOGLiPluginException {
		// prepare test
		final String textToParse = "@TargetFileName ${classDescriptor.simpleName}.java # Name of file with extension without path"
									 + FileUtil.getSystemLineSeparator()
									 + "#@TargetDir ../targetDirFromTemplateFile"
									 + FileUtil.getSystemLineSeparator()
									 + "public class ${classDescriptor.simpleName} {"
									 + FileUtil.getSystemLineSeparator() + "}";

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse, null);

		// verify test result
		final String velocityFileContent = "public class ${classDescriptor.simpleName} {" + FileUtil.getSystemLineSeparator() + "}";
		assertEquals("Result File Content", velocityFileContent, buildUpGeneratorResultData.getGeneratedContent());
	}

	@Test
	public void doesNotRemoveLineConsistingOfSeparatorSymbols() throws MOGLiPluginException {
		// prepare test
		final String textToParse = "@TargetFileName ${classDescriptor.simpleName}.java # Name of file with extension without path"
									 + FileUtil.getSystemLineSeparator()
									 + "#@TargetDir ../targetDirFromTemplateFile"
									 + FileUtil.getSystemLineSeparator()
									 + "public class ${classDescriptor.simpleName} {"
									 + FileUtil.getSystemLineSeparator() + "  #####################################"
									 + FileUtil.getSystemLineSeparator() + "}";

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse, null);

		// verify test result
		final String expected = "public class ${classDescriptor.simpleName} {"
				 + FileUtil.getSystemLineSeparator() + "#####################################"
				 + FileUtil.getSystemLineSeparator() + "}";		
		
		assertEquals("Result File Content", expected, buildUpGeneratorResultData.getGeneratedContent());
	}
	
	@Test
	public void parsesPropertiesCaseInsensitive() throws MOGLiPluginException {
		// prepare test
		final String textToParse = "@targetFileName TestName.java # Name of file with extension without path"
									 + FileUtil.getSystemLineSeparator()
									 + "@targetdir dir"
									 + FileUtil.getSystemLineSeparator()
									 + "@TEST foo";

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse, null);

		// verify test result
		String property = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		assertEquals("property", "TestName.java", buildUpGeneratorResultData.getProperty(property));
		property = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetDir.name();
		assertEquals("property", "dir", buildUpGeneratorResultData.getProperty(property));
		property = "TEST";
		assertEquals("property", "foo", buildUpGeneratorResultData.getProperty(property));
	}

}