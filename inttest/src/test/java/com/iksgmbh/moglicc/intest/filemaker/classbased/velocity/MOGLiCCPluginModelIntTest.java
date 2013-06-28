package com.iksgmbh.moglicc.intest.filemaker.classbased.velocity;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;

public class MOGLiCCPluginModelIntTest extends IntTestParent {

	@Test
	public void createsNewMOGLiCCGeneratorPlugin() throws MOGLiPluginException {
		// prepare test
		setModelFile("MOGLiCCPlugin/MOGLiCC_NewGeneratorPluginModel.txt");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_NewPluginModel/NewGeneratorStarter.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "MOGLiCCPlugin/ExpectedNewGeneratorStarter.java");
		assertFileEquals(expectedFile, file);
	}


	@Test
	public void createsNewMOGLiCCEngineProviderPlugin() throws MOGLiPluginException {
		// prepare test
		setModelFile("MOGLiCCPlugin/MOGLiCC_NewEngineProviderPluginModel.txt");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_NewPluginModel/NewEngineProviderStarter.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "MOGLiCCPlugin/ExpectedNewEngineProviderStarter.java");
		assertFileEquals(expectedFile, file);
	}

	@Test
	public void createsNewMOGLiCCModelProviderPlugin() throws MOGLiPluginException {
		// prepare test
		setModelFile("MOGLiCCPlugin/MOGLiCC_NewModelProviderPluginModel.txt");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_NewPluginModel/NewModelProviderStarter.java");
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), "MOGLiCCPlugin/ExpectedNewModelProviderStarter.java");
		assertFileEquals(expectedFile, file);
	}

}
