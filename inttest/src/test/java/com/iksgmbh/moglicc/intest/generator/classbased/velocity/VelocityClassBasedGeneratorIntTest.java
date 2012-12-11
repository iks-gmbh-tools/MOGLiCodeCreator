package com.iksgmbh.moglicc.intest.generator.classbased.velocity;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedGeneratorIntTest extends IntTestParent {

	private File foreignResourceFileWithSameName = new File(PROJECT_ROOT_DIR +  "../inserter.modelbased.velocity/target/classes/defaultInputData/generator.properties");
	private File tmpFile = new File(PROJECT_ROOT_DIR +  "../inserter.modelbased.velocity/target/classes/defaultInputData/generator.properties.tmp");

	@Before
	public void setup() {
		// There are two files with name 'generator.properties'. 
		// When unpacking them by FileUtil.readTextResourceContentFromClassPath 
		// the first match on the classloader's classpath is returned.
		// In the test environment this might be the wrong file.
		// To be sure the right file is unpacked, the wrong one is deleted.
		FileUtil.copyTextFile(foreignResourceFileWithSameName, tmpFile);
		foreignResourceFileWithSameName.delete();
		super.setup();
	}
	
	@After
	public void tearDown() {
		// restore deleted file for following tests
		FileUtil.copyTextFile(tmpFile, foreignResourceFileWithSameName);
		tmpFile.delete();
	}
		
	@Test
	public void createsJavaBeanMisc() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
//		velocityEngineProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedGeneratorStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedGeneratorStarter.getMOGLiInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiJavaBean/Misc.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "ExpectedMisc.java");
		assertFileEquals(expectedFile, file);
	}

	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityClassBasedGeneratorStarter.getMOGLiInfrastructure().getPluginInputDir(), 
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" + FileUtil.getSystemLineSeparator() +
				                                     "@NameOfValidModel na" + FileUtil.getSystemLineSeparator() + 
				                                     "${classDescriptor.simpleName}");

		// call functionality under test
		velocityClassBasedGeneratorStarter.doYourJob();

		// verify test result
		final File artefactTargetDir = new File(velocityClassBasedGeneratorStarter.getMOGLiInfrastructure().getPluginOutputDir(), 
				artefactName);	
		assertFileDoesNotExist(artefactTargetDir);
		
		
		// prepare follow up test
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" +
				FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel DemoModel" + FileUtil.getSystemLineSeparator() + 
                "${classDescriptor.simpleName}");

		// call functionality under test
		velocityClassBasedGeneratorStarter.doYourJob();
		
		// verify test result
		assertFileExists(artefactTargetDir);
				
	}

	@Test
	public void createsResultFileWithUmlauts() throws MOGLiPluginException, IOException {
		// prepare test
		final File defaultModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                               StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE);
		defaultModelFile.delete();
		assertFileDoesNotExist(defaultModelFile);
		final File testPropertiesFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(),
                                                 StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE);
		testPropertiesFile.delete();
		assertFileDoesNotExist(testPropertiesFile);
		final File testModelFile = new File(standardModelProviderStarter.getMOGLiInfrastructure().getPluginInputDir(), 
				                            "Umlauts.txt");
		MOGLiFileUtil.createNewFileWithContent(testModelFile, "model DemoModel" + FileUtil.getSystemLineSeparator() + 
				                                              "metainfo umlauts ßüäöÜÄÖ" + FileUtil.getSystemLineSeparator() + 
                                                              "class de.Test");
		assertFileExists(testModelFile);
		
		File inputDir = velocityClassBasedGeneratorStarter.getMOGLiInfrastructure().getPluginInputDir();
		FileUtil.deleteDirWithContent(inputDir);
		assertFileDoesNotExist(inputDir);
		inputDir = new File(inputDir, "Test");
		inputDir.mkdirs();
		final File templateFile = new File(inputDir, "Umlauts.tpl");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() + 
				                                             "@TargetFileName Umlauts.txt" + FileUtil.getSystemLineSeparator() + 
				                                             "@TargetDir <applicationRootDir>/example" + FileUtil.getSystemLineSeparator() +
				                                             "ßüäöÜÄÖ $model.getMetaInfoValueFor(\"umlauts\")");
		assertFileExists(templateFile);
		

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedGeneratorStarter.doYourJob();

		// verify test result
		final File resultFile = new File(applicationRootDir, "example/Umlauts.txt");
		assertFileExists(resultFile);
		final String actualFileContent = FileUtil.getFileContent(resultFile);
		assertStringEquals("file content", "ßüäöÜÄÖ ßüäöÜÄÖ", actualFileContent);
	}
}
