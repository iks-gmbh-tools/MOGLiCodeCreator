package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.test.MockDataBuilder;

public class VelocityEngineProviderSetDataUnitTest extends VelocityEngineProviderTestParent {
	
	private final Model buildStandardModel = MockDataBuilder.buildStandardModel();
	
	@Before
	@Override
	public void setup() {
		super.setup();

		createInputTestFiles();
	}

	@Test
	public void throwsExceptionForNullData() {
		try {
			velocityEngineProvider.setEngineData(null);
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "Parameter 'engineData' must not be null!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForWrongDataType() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData("");
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "VelocityEngineData expected! Wrong engine data set: java.lang.String", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingModell() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
					                  null, MockDataBuilder.GENERATOR_PLUGIN_ID, "Javabean", "template.tpl", applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "Model not set!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingGeneratorPluginId() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
					                             buildStandardModel, null, "Javabean", "template.tpl", applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "GeneratorPluginId not set!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForNotExistingGeneratorPlugin() throws MogliPluginException {
		try {			
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
					                             buildStandardModel, "a", "Javabean", "template.tpl", applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "Unknown GeneratorPlugin!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForMissingArtefactType() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
                                                 buildStandardModel, MockDataBuilder.GENERATOR_PLUGIN_ID, "", "template.tpl", applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "ArtefactType not set!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingMainTemplateName() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
					buildStandardModel, MockDataBuilder.GENERATOR_PLUGIN_ID, "Javabean", null, applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "MainTemplateName not set!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForMissingMainTemplateFile() throws MogliPluginException {
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMock(
					               buildStandardModel, MockDataBuilder.GENERATOR_PLUGIN_ID, "Javabean", "template.tpl", applicationInputDir));
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), "Main Template File does not exist");
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingTemplateDir() throws MogliPluginException {		
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMockWithStandardData(TEMPLATE_WITH_SUBTEMPLATE, null));
		} catch (MogliPluginException e) {
			assertStringEquals("Error message", "TemplateDir not set!", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForNotExistingTemplateDir() throws MogliPluginException {
		final File templateDir = new File("aaa");
		try {
			velocityEngineProvider.setEngineData(MockDataBuilder.buildVelocityEngineDataMockWithStandardData(TEMPLATE_WITH_SUBTEMPLATE, templateDir));
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), "TemplateDir does not exist");
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void throwsExceptionForMissingVelocityEngineData() throws MogliPluginException {
		try {
			velocityEngineProvider.startEngine();
		} catch (MogliPluginException e) {
			assertStringContains(e.getMessage(), VelocityEngineProviderStarter.ENGINE_STARTED_WITHOUT_DATA);
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void acceptsVelocityEngineDataWithTemplateInPluginInputDir() throws MogliPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir, MockDataBuilder.GENERATOR_PLUGIN_ID);
		final VelocityEngineData engineData = MockDataBuilder.buildVelocityEngineDataMockWithStandardData(TEMPLATE_WITH_SUBTEMPLATE, templateDir);

		// call functionality under test
		velocityEngineProvider.setEngineData(engineData);

		// verify test result
		assertNotNull(velocityEngineProvider.getVelocityEngineData());
	}
	
	@Test
	public void acceptsVelocityEngineDataWithTemplateInSubdir() throws MogliPluginException {
		// prepare test
		final File templateDir = new File(applicationInputDir,  
											MockDataBuilder.GENERATOR_PLUGIN_ID
											+ "/" + MockDataBuilder.ARTEFACT_TYPE);
		final VelocityEngineData engineData = 
			MockDataBuilder.buildVelocityEngineDataMockWithStandardData(TEMPLATE_NO_INCLUDED_SUBTEMPLATE, 
					                                                    templateDir);

		// call functionality under test
		velocityEngineProvider.setEngineData(engineData);

		// verify test result
		assertNotNull(velocityEngineProvider.getVelocityEngineData());
	}
	

}
