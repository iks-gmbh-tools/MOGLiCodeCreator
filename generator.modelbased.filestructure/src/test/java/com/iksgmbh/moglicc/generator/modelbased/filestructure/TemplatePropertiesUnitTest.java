package com.iksgmbh.moglicc.generator.modelbased.filestructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.modelbased.filestructure.test.FileStructureModelBasedGeneratorTestParent;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.utils.FileUtil;

public class TemplatePropertiesUnitTest extends FileStructureModelBasedGeneratorTestParent {
	
	@Test
	public void readsTemplatePropertiesFromFile() throws MOGLiPluginException {
		// prepare test
		final File artefactPropertiesFile = new File(getProjectResourcesDir(), "defaultInputData/MOGLiCC_NewPluginProject/" 
		                             + FilestructureModelBasedGeneratorStarter.FILENAME_ARTEFACT_PROPERTIES); 

		// call functionality under test
		final TemplateProperties templateProperties = new TemplateProperties(artefactPropertiesFile, 
				                                                             new DummyLogger(), getNewPluginModelDummyModel(), null);

		// verify test result
		assertEquals("root name", METAINFO_MODEL_TARGETDIR, templateProperties.getRootName());
		assertEquals("target dir", MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, templateProperties.getTargetDir());
		assertEquals("name of valid model", "MOGLiCC_NewPluginModel", templateProperties.getNameOfValidModel());
		assertEquals("exclude", ".git", templateProperties.getExcludes().get(0));
		assertEquals("create new", true, templateProperties.isCreateNew());
		assertEquals("number of replacements", 4, templateProperties.getReplacements().size());
	}

	private Model getNewPluginModelDummyModel() {
		final HashMap<String, String> modelMetaInfos = getStandardModelMetaInfos();
		return getDummyModel("MOGLiCC_NewPluginModel", modelMetaInfos);
	}


	@Test
	public void throwsExceptionForUnkownPlaceHolder() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${modelmetainfo=UnkownPlaceholder}");

		try {
			// call functionality under test
			new TemplateProperties(artefactPropertiesFile, new DummyLogger(), getDummyModel(null), null);
			fail("Expected exception not thrown");
		} catch (Exception e) {
			// verify test result
			assertTrue(e.getMessage().startsWith("Unkown placeholder 'modelmetainfo' in template file: "));
		}
	}

	@Test
	public void throwsExceptionForWrongSyntax() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${modelmetainfo}");

		try {
			// call functionality under test
			new TemplateProperties(artefactPropertiesFile, new DummyLogger(), getDummyModel(null), null);
			fail("Expected exception not thrown");
		} catch (Exception e) {
			// verify test result
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().startsWith("Wrong usage of 'ModelMetaInfo' in template file"));
		}
	}


	@Test
	public void parsesTemplateWithoutReplacement() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName TargetDir");

		// call functionality under test
		final TemplateProperties templateProperties = new TemplateProperties(artefactPropertiesFile, new DummyLogger(), 
				                                                             getDummyModel(null), null);
		
		// verify test result
		assertEquals("root name", "TargetDir", templateProperties.getRootName());
	}

	@Test
	public void replacesModelMetaInfoReferences() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${ModelMetaInfo=eclipseProjectDir}");

		// call functionality under test
		final TemplateProperties templateProperties = new TemplateProperties(artefactPropertiesFile, new DummyLogger(), 
				                                                             getNewPluginModelDummyModel(), null);
		
		// verify test result
		assertEquals("root name", MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, templateProperties.getRootName());
	}

	@Test
	public void parsesReplacementData() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${ModelMetaInfo=eclipseProjectDir}" + 
		                                                          FileUtil.getSystemLineSeparator() + 
		                                                          "@ReplaceIn file x y");

		// call functionality under test
		final TemplateProperties templateProperties = new TemplateProperties(artefactPropertiesFile, new DummyLogger(), 
				                                                             getNewPluginModelDummyModel(), null);
		
		// verify test result
		assertEquals("Number replacements", 1, templateProperties.getReplacements().size());
	}

	@Test
	public void throwsErrorForName2IncludingSpaces() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${ModelMetaInfo=eclipseProjectDir}" + 
		                                                          FileUtil.getSystemLineSeparator() + 
		                                                          "@RenameFile name1name2");  // missing space results in missing name2

		try {
			// call functionality under test
			new TemplateProperties(artefactPropertiesFile, new DummyLogger(), getNewPluginModelDummyModel(), null);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertTrue("Unexpected Error message", e.getMessage().startsWith("Missing name2"));			
		}
	}

	@Test
	public void throwsErrorForMissingName2InRenamingData() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${ModelMetaInfo=eclipseProjectDir}" + 
		                                                          FileUtil.getSystemLineSeparator() + 
		                                                          "@RenameFile name1 name 2");

		try {
			// call functionality under test
			new TemplateProperties(artefactPropertiesFile, new DummyLogger(), getNewPluginModelDummyModel(), null);	
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertTrue("Unexpected Error message", e.getMessage().startsWith("Name2 must not contain spaces"));			
		}
	}

	@Test
	public void parsesRenamingData() throws Exception {
		// prepare test
		final File artefactPropertiesFile = new File("target/test.tpl");
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName ${ModelMetaInfo=eclipseProjectDir}" + 
		                                                          FileUtil.getSystemLineSeparator() + 
		                                                          "@RenameFile  name1  name2 ");

		// call functionality under test
		final TemplateProperties templateProperties = new TemplateProperties(artefactPropertiesFile, new DummyLogger(), 
				                                                             getNewPluginModelDummyModel(), null);
		
		// verify test result
		assertEquals("Number replacements", 1, templateProperties.getFileRenamings().size());
	}

	class DummyLogger implements Logger {

		@Override
		public void logInfo(String message) {
		}

		@Override
		public void logWarning(String message) {
		}

		@Override
		public void logError(String message) {
		}

		@Override
		public void log(LOG_LEVEL level, String message) {
		}		
	}
	
}
