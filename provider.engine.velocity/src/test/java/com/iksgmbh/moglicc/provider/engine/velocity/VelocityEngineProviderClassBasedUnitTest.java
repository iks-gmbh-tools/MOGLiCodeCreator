package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.test.MockDataBuilder;
import com.iksgmbh.data.ClassNameData;

public class VelocityEngineProviderClassBasedUnitTest extends VelocityEngineProviderTestParent {

	@Before
	@Override
	public void setup() {
		super.setup();
		createInputTestFiles();
	}
	
	@Test
	public void replacesClassNameInTemplate() throws MOGLiPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID
                                   + "/" + MockDataBuilder.ARTEFACT_TYPE);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData( 
				                              TEMPLATE_NO_INCLUDED_SUBTEMPLATE, templateDir);
		velocityEngineProvider.setEngineData(engineData);
		final ClassNameData classNameData = new ClassNameData(MockDataBuilder.PACKAGE_TEST + "." + MockDataBuilder.TEST_CLASS_NAME1);
		final List<AttributeDescriptor> buildStandardAttributeDescriptorList = MockDataBuilder.buildStandardAttributeDescriptorList();
		final ClassDescriptor buildClassDescriptorMock = MockDataBuilder.buildClassDescriptorMock(
			                                                    classNameData, buildStandardAttributeDescriptorList);
		
		// call functionality under test
		final VelocityContext context = velocityEngineProvider.getVelocityContextWith(
				                                  VelocityEngineProviderStarter.TEMPLATE_REFERENCE_CLASS_DESCRIPTOR, 
				                                  buildClassDescriptorMock);
		final String mergeResult = velocityEngineProvider.mergeTemplateWith(context);

		// verify test result2
		assertStringContains(mergeResult, "@TargetFileName TestClassA.java");
		assertStringContains(mergeResult, "public class TestClassA {");
	}

	@Test
	public void replacesJavaTypeInSubTemplate() throws MOGLiPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData( 
				                              TEMPLATE_WITH_SUBTEMPLATE, templateDir);
		velocityEngineProvider.setEngineData(engineData);

		// call functionality under test
		final VelocityContext context = velocityEngineProvider.getVelocityContextWith(
                VelocityEngineProviderStarter.TEMPLATE_REFERENCE_CLASS_DESCRIPTOR, 
                MockDataBuilder.getClassDescriptorWithTwoAttributes());
		final String mergeResult = velocityEngineProvider.mergeTemplateWith(context);		

		// verify test result
		assertStringContains(mergeResult, "private String name;");
		assertStringContains(mergeResult, "private int age;");
	}

	@Test
	public void generatesVelocityGeneratorResultData_Without_SubTemplateWithArtefactSubDir() throws MOGLiPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID
                                   + "/" + MockDataBuilder.ARTEFACT_TYPE);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData( 
				                              TEMPLATE_NO_INCLUDED_SUBTEMPLATE, templateDir);
		velocityEngineProvider.setEngineData(engineData);
		
		// call functionality under test
		List<GeneratorResultData> resultDataList = velocityEngineProvider.startEngineWithClassList();
		assertEquals("number target files", 2, resultDataList.size());
		assertStringContains(resultDataList.get(0).getGeneratedContent(), "public class " + MockDataBuilder.TEST_CLASS_NAME1 + " {");
	}

	@Test
	public void createsOutputFileWithSubTemplate_Without_ArtefactSubDir() throws MOGLiPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData( 
				                              TEMPLATE_WITH_SUBTEMPLATE, templateDir);
		velocityEngineProvider.setEngineData(engineData);
		
		// call functionality under test
		velocityEngineProvider.startEngine();

		// verify test result
		List<GeneratorResultData> resultDataList = velocityEngineProvider.startEngineWithClassList();
		assertEquals("number target files", 2, resultDataList.size());
		assertStringContains(resultDataList.get(1).getGeneratedContent(), "public class Standard {");
		assertStringContains(resultDataList.get(1).getGeneratedContent(), "// comment2");
	}

}
