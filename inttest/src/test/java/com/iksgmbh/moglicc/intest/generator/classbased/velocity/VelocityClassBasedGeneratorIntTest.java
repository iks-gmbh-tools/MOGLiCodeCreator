package com.iksgmbh.moglicc.intest.generator.classbased.velocity;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.utils.FileUtil;

public class VelocityClassBasedGeneratorIntTest extends IntTestParent {

	@Test
	public void createsJavaBeanMisc() throws MOGLiPluginException2 {
		// prepare test
		standardModelProviderStarter.doYourJob();
		velocityEngineProviderStarter.doYourJob();

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
}
