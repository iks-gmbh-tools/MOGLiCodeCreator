package com.iksgmbh.moglicc.intest.provider.engine.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpModel;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.utils.FileUtil;

public class VelocityEngineProviderIntTest extends IntTestParent {

	@Test
	public void usesMetaInfoSupportMethod_DoesHaveAnyMetaInfosWithName() throws MOGLiPluginException2, IOException {
		// prepare test
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMock(buildModel(), 
				velocityClassBasedGeneratorStarter.getId(), "artefact",
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
