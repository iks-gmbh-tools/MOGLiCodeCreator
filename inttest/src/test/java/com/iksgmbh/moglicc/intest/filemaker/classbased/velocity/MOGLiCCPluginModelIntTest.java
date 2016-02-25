/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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