package com.iksgmbh.moglicc.intest.inserter.modelbased.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.utils.MogliFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedInserterIntTest extends IntTestParent {

	@Test
	public void createsBeanFactory() throws MogliPluginException {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();

		// call functionality under test
		velocityModelBasedInserterStarter.doYourJob();

		// verify test result in plugin directory
		final InfrastructureService infrastructure = velocityModelBasedInserterStarter.getMogliInfrastructure();
		File file = new File(infrastructure.getPluginOutputDir(), "BeanFactoryClass/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), "BeanFactoryInsertAboveTemplate/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), "BeanFactoryInsertBelowTemplate/BeanFactory.java");
		assertFileExists(file);
		file = new File(infrastructure.getPluginOutputDir(), "BeanFactoryReplaceTemplate/BeanFactory.java");
		assertFileExists(file);
				
		// verify test result in target directory read from template file
		file = new File(applicationRootDir + "/example", "BeanFactory.java");
		List<String> fileContentAsList = MogliFileUtil.getFileContentAsList(file);
		System.err.println(file.getAbsolutePath());
		assertEquals("Line number", 29, fileContentAsList.size());
	}
	
	@Test
	public void createsArtefactOnlyIfModelIsValid() throws Exception {
		// prepare test
		final String artefactName = "TestArtefact";
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();
		final File artefactTemplateDir = new File(velocityModelBasedInserterStarter.getMogliInfrastructure().getPluginInputDir(), 
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
		final File artefactTargetDir = new File(velocityModelBasedInserterStarter.getMogliInfrastructure().getPluginOutputDir(), 
				artefactName);	
		assertFileDoesNotExist(artefactTargetDir);
		
		
		// prepare follow up test
		FileUtil.createNewFileWithContent(testTemplate, "@TargetFileName ${classDescriptor.simpleName}.txt" +
				FileUtil.getSystemLineSeparator() +
				"@TargetDir <applicationRootDir>/example" +
				FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel DefaultModel" + FileUtil.getSystemLineSeparator() + 
                "${classDescriptor.simpleName}");

		// call functionality under test
		velocityModelBasedInserterStarter.doYourJob();
		
		// verify test result
		assertFileExists(artefactTargetDir);
				
	}

}
