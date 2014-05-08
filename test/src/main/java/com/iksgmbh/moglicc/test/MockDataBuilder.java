package com.iksgmbh.moglicc.test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.test.model.AttributeDescriptorDummy;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter;
import com.iksgmbh.data.ClassNameData;

public class MockDataBuilder {
	
	public static final String JAVA_TYPE = "JavaType";
	public static final String GENERATOR_PLUGIN_ID = DummyGeneratorStarter.PLUGIN_ID;
	public static final String ARTEFACT_TYPE = "javabean";
	public static final String TEST_CLASS_NAME1 = "TestClassA";
	public static final String TEST_CLASS_NAME2 = "TestClassB";
	public static final String PACKAGE_TEST = "de.test";

	public static VelocityEngineData buildVelocityEngineDataMock(final Model model, 
			final String pluginId, final String artefactId, 
			final String templateName, final File templateDir) {
		
		final VelocityEngineData mock = EasyMock.createNiceMock(VelocityEngineData.class);
        expect(mock.getModel()).andReturn(model).anyTimes();
        expect(mock.getGeneratorPluginId()).andReturn(pluginId).anyTimes();
        expect(mock.getArtefactType()).andReturn(artefactId).anyTimes();
        expect(mock.getTemplateDir()).andReturn(templateDir).anyTimes();
        expect(mock.getMainTemplateSimpleFileName()).andReturn(templateName).anyTimes();
        replay(mock);
        return mock;
	}
	
	public static VelocityEngineData buildVelocityEngineDataMockWithStandardData(
									 final String templateName, final File templateDir) {
        return buildVelocityEngineDataMock(buildStandardModel(), GENERATOR_PLUGIN_ID, ARTEFACT_TYPE, 
        		                           templateName, templateDir);
	}

	public static VelocityEngineData buildVelocityEngineDataMockWithStandardData(final String generatorId,
			                         final String templateName, final File templateDir) {
		return buildVelocityEngineDataMock(buildStandardModel(), generatorId, ARTEFACT_TYPE, 
                   templateName, templateDir);
	}
	
	
	public static Model buildStandardModel() {
		final ClassNameData[] classes = { new ClassNameData(PACKAGE_TEST + "." + TEST_CLASS_NAME1),
				                          new ClassNameData(PACKAGE_TEST + "." + TEST_CLASS_NAME2) };
		return buildModelMock(classes);
	}

	public static Model buildModelMock(final ClassNameData... classes) {
		final Model mock = EasyMock.createNiceMock(Model.class);
		expect(mock.getSize()).andReturn(classes.length).anyTimes();
		expect(mock.getClassDescriptorList()).andReturn(buildClassDescriptorList(classes)).anyTimes();
        replay(mock);
        return mock;
	}

	private static List<ClassDescriptor> buildClassDescriptorList(final ClassNameData[] classes) {
		final List<ClassDescriptor> list = new ArrayList<ClassDescriptor>();
		for (ClassNameData classNameData : classes) {
			list.add(buildClassDescriptorMockWithoutAttributes(classNameData));
		}
		return list;
	}
	
	public static ClassDescriptor buildClassDescriptorMockWithoutAttributes(final ClassNameData classNameData) {
		final ClassDescriptor mock = EasyMock.createNiceMock(ClassDescriptor.class);
		expect(mock.getSimpleName()).andReturn(classNameData.getSimpleClassName()).anyTimes();
		expect(mock.getFullyQualifiedName()).andReturn(classNameData.getFullyQualifiedClassname()).anyTimes();
		expect(mock.getPackage()).andReturn(classNameData.getPackageName()).anyTimes();
        replay(mock);
        return mock;
	}

	public static ClassDescriptor buildClassDescriptorMock(final ClassNameData classNameData,
			                                               final List<AttributeDescriptor> attributeDescriptorList) {
		final ClassDescriptor mock = EasyMock.createNiceMock(ClassDescriptor.class);
		expect(mock.getSimpleName()).andReturn(classNameData.getSimpleClassName()).anyTimes();
		expect(mock.getFullyQualifiedName()).andReturn(classNameData.getFullyQualifiedClassname()).anyTimes();
		expect(mock.getPackage()).andReturn(classNameData.getPackageName()).anyTimes();
		expect(mock.getAttributeDescriptorList()).andReturn(buildStandardAttributeDescriptorList()).anyTimes();
        replay(mock);
        return mock;
	}

	public static List<AttributeDescriptor> buildStandardAttributeDescriptorList() {
		final List<AttributeDescriptor> list = new ArrayList<AttributeDescriptor>();
		list.add(new AttributeDescriptorDummy("Name", "String"));
		list.add(new AttributeDescriptorDummy("Age", "int"));
		return list;
	}
	
	public static ClassDescriptor getClassDescriptorWithTwoAttributes() {
		final ClassNameData classNameData =  new ClassNameData(MockDataBuilder.PACKAGE_TEST + "." + MockDataBuilder.TEST_CLASS_NAME1);
		final List<AttributeDescriptor> buildStandardAttributeDescriptorList = MockDataBuilder.buildStandardAttributeDescriptorList();
		final ClassDescriptor buildClassDescriptorMock = MockDataBuilder.buildClassDescriptorMock(
			                                                    classNameData, buildStandardAttributeDescriptorList);
		return buildClassDescriptorMock;
	}

	public static ModelProvider getStandardModelProvider() throws MOGLiPluginException {
		return new DummyStandardModelProviderStarter();
	}

	public static MOGLiPlugin getVelocityEngineProvider() {
		return new DummyVelocityEngineProviderStarter();
	}
}
