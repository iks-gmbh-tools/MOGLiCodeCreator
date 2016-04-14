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
package com.iksgmbh.moglicc.intest.provider.engine.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.utils.FileUtil;

public class VelocityEngineProviderIntTest extends IntTestParent {

	@Test
	public void usesMetaInfoSupportMethod_DoesHaveAnyMetaInfosWithName() throws MOGLiPluginException, IOException {
		// prepare test
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMock(buildModel(), 
				velocityClassBasedFileMakerStarter.getId(), "artefact",
				"DoesHaveAnyMetaInfosWithName_TestTemplate.tpl", new File(getProjectTestResourcesDir()));
		velocityEngineProviderStarter.setEngineData(engineData);		
		
		// call functionality under test
		final GeneratorResultData resultData = velocityEngineProviderStarter.startEngineWithModel();

		// verify test result
		final File expectedFile = new File(getProjectTestResourcesDir(), "DoesHaveAnyMetaInfosWithName_expectedResult.txt");
		final String expected = FileUtil.getFileContent(expectedFile);
		assertEquals("Generated Content", expected.trim(), resultData.getGeneratedContent().trim());
	}

	private Model buildModel() {
		final BuildUpModel buildUpModel = new BuildUpModel("TestModel");
		buildUpModel.addMetaInfo(new BuildUpMetaInfo("ModelMetaInfo"));
		
		ClassNameData classnameData = new ClassNameData("com.iksgmbh.Class1");
		BuildUpClassDescriptor buildUpClassDescriptor = new BuildUpClassDescriptor(classnameData);
		buildUpClassDescriptor.addMetaInfo(new BuildUpMetaInfo("ClassMetaInfo"));
		buildUpModel.addClassDescriptor(buildUpClassDescriptor);
		
		BuildUpAttributeDescriptor buildUpAttributeDescriptor = new BuildUpAttributeDescriptor("Attribute1");
		buildUpAttributeDescriptor.addMetaInfo(new BuildUpMetaInfo("AttributeMetaInfo"));
		buildUpClassDescriptor.addAttributeDescriptor(buildUpAttributeDescriptor);
		
		classnameData = new ClassNameData("com.iksgmbh.Class2");
		buildUpClassDescriptor = new BuildUpClassDescriptor(classnameData);
		buildUpModel.addClassDescriptor(buildUpClassDescriptor);
		buildUpAttributeDescriptor = new BuildUpAttributeDescriptor("Attribute2");
		buildUpClassDescriptor.addAttributeDescriptor(buildUpAttributeDescriptor);

		return buildUpModel;
	}
	
}