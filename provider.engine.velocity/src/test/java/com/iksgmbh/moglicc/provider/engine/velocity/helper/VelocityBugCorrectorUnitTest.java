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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;

public class VelocityBugCorrectorUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void fixesRemovedFileExtensionBug() throws Exception {
		// prepare test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		final String targetFileNameId = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		buildUpGeneratorResultData.addProperty(targetFileNameId, "filename");
		
		final List<String> originalTemplateContent = new ArrayList<String>();
		originalTemplateContent.add("@CreateNew true");
		originalTemplateContent.add("@TargetFileName filename.ext # comment");
		originalTemplateContent.add("@TargetDir targetDir");
		
		// call functionality under test
		VelocityBugCorrector.doYourJob(buildUpGeneratorResultData, originalTemplateContent);

		// verify test result
		assertStringContains(buildUpGeneratorResultData.getProperty(targetFileNameId), "filename.ext");
	}

	@Test
	public void doesNothingBecauseExtentionWasNotCut() throws Exception {
		// prepare test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		final String targetFileNameId = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		buildUpGeneratorResultData.addProperty(targetFileNameId, "filename.ext");
		
		final List<String> originalTemplateContent = new ArrayList<String>();
		originalTemplateContent.add("@CreateNew true");
		originalTemplateContent.add("@TargetFileName filename.ext # comment");
		originalTemplateContent.add("@TargetDir targetDir");
		
		// call functionality under test
		VelocityBugCorrector.doYourJob(buildUpGeneratorResultData, originalTemplateContent);

		// verify test result
		assertStringContains(buildUpGeneratorResultData.getProperty(targetFileNameId), "filename.ext");
	}

	@Test
	public void doesNothingBecauseNoExtentionExits() throws Exception {
		// prepare test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		final String targetFileNameId = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		buildUpGeneratorResultData.addProperty(targetFileNameId, "filename");
		
		final List<String> originalTemplateContent = new ArrayList<String>();
		originalTemplateContent.add("@CreateNew true");
		originalTemplateContent.add("@TargetFileName filename # comment");
		originalTemplateContent.add("@TargetDir targetDir");
		
		// call functionality under test
		VelocityBugCorrector.doYourJob(buildUpGeneratorResultData, originalTemplateContent);

		// verify test result
		assertStringContains(buildUpGeneratorResultData.getProperty(targetFileNameId), "filename");
	}

}