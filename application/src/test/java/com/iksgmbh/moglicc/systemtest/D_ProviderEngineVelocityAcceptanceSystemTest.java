package com.iksgmbh.moglicc.systemtest;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineProviderStarter;
import com.iksgmbh.utils.FileUtil;

public class D_ProviderEngineVelocityAcceptanceSystemTest  extends __AbstractSystemTest {
	
	public static final String ENGINE_PLUGIN_ID = VelocityEngineProviderStarter.PLUGIN_ID;


	// *****************************  test methods  ************************************
	
	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, ENGINE_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginLogFile);
	}
	
	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, ENGINE_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 3);
	}
}
