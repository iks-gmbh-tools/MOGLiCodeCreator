package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class D_StandardModelProviderAcceptanceSystemTest extends _AbstractSystemTest {
	
	private static final String PROVIDER_PLUGIN_ID = StandardModelProviderStarter.PLUGIN_ID;

	// *****************************  test methods  ************************************
	
	
	@Test
	public void createsDefaultModelFileOfStandardModelProvider() {
		// prepare test
		final File modelDir = new File(testDir + "/" + DIR_INPUT_FILES + "/" + PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(modelDir);
		assertFileDoesNotExist(modelDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		final File modelFile = new File(modelDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE); 
		assertFileExists(modelFile);
		String fileContent = MOGLiFileUtil.getFileContent(modelFile);
		assertFileContainsEntry(modelFile, fileContent);
	}
	
	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 4);
	}
	
	@Test
	public void createsStatisticsResultFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		assertFileDoesNotExist(applicationOutputDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		final File statisticsFile = new File(applicationOutputDir, PROVIDER_PLUGIN_ID 
				                                        + "/" + StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		assertFileExists(statisticsFile);
		final File expectedFile = getTestFile("ExpectedModelStatistics.txt");
		assertFileEquals(expectedFile, statisticsFile);
	}
	
	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, PROVIDER_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginLogFile);
	}
}
