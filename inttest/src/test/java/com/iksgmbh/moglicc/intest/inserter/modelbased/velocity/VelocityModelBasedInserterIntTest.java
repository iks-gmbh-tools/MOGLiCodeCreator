package com.iksgmbh.moglicc.intest.inserter.modelbased.velocity;

import static com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityModelBasedInserterStarter.BEAN_FACTORY_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedInserterIntTest extends IntTestParent {

	@Test
	public void createsBeanFactory() throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();

		// call functionality under test
		velocityModelBasedInserterStarter.doYourJob();

		// verify test result in plugin directory
		final InfrastructureService infrastructure = velocityModelBasedInserterStarter.getMOGLiInfrastructure();
		File file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), BEAN_FACTORY_DIR + "/BeanFactory.java");
		assertFileExists(file);
				
		// verify test result in target directory read from template file
		file = new File(applicationRootDir + "/example", "BeanFactory.java");
		List<String> fileContentAsList = MOGLiFileUtil.getFileContentAsList(file);
		assertEquals("Line number", 28, fileContentAsList.size());
	}
	
	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityModelBasedInserterStarter.getMOGLiInfrastructure().getPluginInputDir(), 
				artefactName);
		artefactTemplateDir.mkdirs();
		final File testTemplate = new File(artefactTemplateDir, "Main.tpl");
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" + 
														FileUtil.getSystemLineSeparator() +
														"@TargetDir <applicationRootDir>/example" +FileUtil.getSystemLineSeparator() +
														"@NameOfValidModel na" + FileUtil.getSystemLineSeparator() + 
				                                        "${classDescriptor.simpleName}");

		// call functionality under test
		velocityModelBasedInserterStarter.doYourJob();

		// verify test result
		final File artefactTargetDir = new File(velocityModelBasedInserterStarter.getMOGLiInfrastructure().getPluginOutputDir(), 
				artefactName);	
		assertFileDoesNotExist(artefactTargetDir);
		
		// prepare follow up test
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" +
				FileUtil.getSystemLineSeparator() +
				"@TargetDir <applicationRootDir>/example" +
				FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel DemoModel" + FileUtil.getSystemLineSeparator() + 
                "${classDescriptor.simpleName}");

		// call functionality under test
		velocityModelBasedInserterStarter.doYourJob();
		
		// verify test result
		assertFileExists(artefactTargetDir);
	}

	@Test
	public void throwsExceptionIfTwoMainTemplatesDoNotDifferInTheirTargetFilename() throws MOGLiPluginException {
		executeTargetDefinitionTestWith("file1.txt", "file2.txt", "temp", "temp");
	}

	@Test
	public void throwsExceptionIfTwoMainTemplatesDoNotDifferInTheirTargetDir() throws MOGLiPluginException {
		executeTargetDefinitionTestWith("file.txt", "file.txt", "temp1", "temp2");
	}

	public void executeTargetDefinitionTestWith(final String filename1, final String filename2,
			                                    final String targetDirString1, final String targetDirString2) 
	                                            throws MOGLiPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File targetDir1 = new File(applicationRootDir, targetDirString1);
		targetDir1.mkdirs();
		final File targetFile1 = new File(targetDir1, filename1);
		MOGLiFileUtil.createNewFileWithContent(targetFile1, "test");
		final File targetDir2 = new File(applicationRootDir, targetDirString2);
		
		if(! targetDirString1.equals(targetDirString2)) {
			targetDir2.mkdirs();
			final File targetFile2 = new File(targetDir1, filename2);
			MOGLiFileUtil.createNewFileWithContent(targetFile2, "test");
			
		}
			
		final File templateDir = new File(velocityModelBasedInserterStarter.getMOGLiInfrastructure().getPluginInputDir(), 
				                          "TestArtifact");
		templateDir.mkdirs();
		final File mainTemplate1 = new File(templateDir, "Main1.tpl");
		MOGLiFileUtil.createNewFileWithContent(mainTemplate1, "@" + KnownGeneratorPropertyNames.TargetFileName.name()
				                                                  + " " + filename1
				                                                  + FileUtil.getSystemLineSeparator()
													                + "@" + KnownGeneratorPropertyNames.TargetDir
													                + " " + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER 
													                + "/" + targetDirString1);
		final File mainTemplate2 = new File(templateDir, "Main2.tpl");
		MOGLiFileUtil.createNewFileWithContent(mainTemplate2, "@" + KnownGeneratorPropertyNames.TargetFileName.name()
												                + " " + filename2
												                + FileUtil.getSystemLineSeparator()
												                + "@" + KnownGeneratorPropertyNames.TargetDir
												                + " " + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER 
												                + "/" + targetDirString2);

		// call functionality under test
		try {
			velocityModelBasedInserterStarter.doYourJob();
			// cleanup
			FileUtil.deleteDirWithContent(targetDir1);
			FileUtil.deleteDirWithContent(targetDir2);

			fail("Expected exception not thrown!");
		} catch (MOGLiPluginException e) {
			assertStringEquals("error message", "There are main templates for artefact 'TestArtifact' " +
					                            "that differ in there targetFileName or targetDir!", e.getMessage());

			// cleanup
			FileUtil.deleteDirWithContent(targetDir1);
			FileUtil.deleteDirWithContent(targetDir2);
		}
	}

}
